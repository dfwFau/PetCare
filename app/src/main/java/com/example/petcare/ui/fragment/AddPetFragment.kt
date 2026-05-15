package com.example.petcare.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.petcare.R
import com.example.petcare.data.entity.Pet
import com.example.petcare.database.AppDatabase
import com.example.petcare.databinding.FragmentAddPetBinding
import com.example.petcare.repository.PetRepository
import com.example.petcare.viewmodel.PetViewModel
import com.example.petcare.viewmodel.ViewModelFactory

import android.net.Uri
import android.os.Environment
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddPetFragment : Fragment() {

    private var _binding: FragmentAddPetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PetViewModel
    private var currentPhotoPath: String? = null
    private var petToEdit: Pet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            petToEdit = it.getSerializable(ARG_PET) as? Pet
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                Glide.with(this).load(path).into(binding.ivPet)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            performCapture()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Glide.with(this).load(it).into(binding.ivPet)
            currentPhotoPath = it.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = PetRepository(AppDatabase.getDatabase(requireContext()).petDao())
        viewModel = ViewModelProvider(this, ViewModelFactory(repository))[PetViewModel::class.java]

        setupSpinner()
        checkForEditMode()

        binding.btnCapture.setOnClickListener {
            capturePhoto()
        }

        binding.btnGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            if (petToEdit != null) updatePet() else savePet()
        }
    }

    private fun checkForEditMode() {
        petToEdit?.let { pet ->
            binding.etName.setText(pet.name)
            binding.etBreed.setText(pet.breed)
            binding.etAge.setText(pet.age.toString())
            binding.etWeight.setText(pet.weight.toString())
            if (pet.gender == "Male") binding.rbMale.isChecked = true else binding.rbFemale.isChecked = true
            
            val types = arrayOf("Dog", "Cat", "Bird", "Rabbit", "Other")
            binding.spinnerType.setSelection(types.indexOf(pet.type))
            
            if (pet.imagePath != null) {
                currentPhotoPath = pet.imagePath
                Glide.with(this).load(pet.imagePath).into(binding.ivPet)
            }
            
            binding.btnSave.text = getString(R.string.update_pet)
        }
    }

    private fun updatePet() {
        val name = binding.etName.text.toString()
        val type = binding.spinnerType.selectedItem.toString()
        val breed = binding.etBreed.text.toString()
        val ageText = binding.etAge.text.toString()
        val weightText = binding.etWeight.text.toString()
        val gender = if (binding.rbMale.isChecked) "Male" else "Female"

        if (name.isEmpty() || breed.isEmpty() || ageText.isEmpty() || weightText.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        petToEdit?.let {
            it.name = name
            it.type = type
            it.breed = breed
            it.age = ageText.toInt()
            it.gender = gender
            it.weight = weightText.toDouble()
            it.imagePath = currentPhotoPath

            viewModel.updatePet(it)
            Toast.makeText(requireContext(), "Pet updated!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun capturePhoto() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            performCapture()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun performCapture() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: Exception) {
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.petcare.fileprovider",
                it
            )
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PET_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun setupSpinner() {
        val types = arrayOf("Dog", "Cat", "Bird", "Rabbit", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter
    }

    private fun savePet() {
        val name = binding.etName.text.toString()
        val type = binding.spinnerType.selectedItem.toString()
        val breed = binding.etBreed.text.toString()
        val ageText = binding.etAge.text.toString()
        val weightText = binding.etWeight.text.toString()
        val gender = if (binding.rbMale.isChecked) "Male" else "Female"

        if (name.isEmpty() || breed.isEmpty() || ageText.isEmpty() || weightText.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val pet = Pet(
            name = name,
            type = type,
            breed = breed,
            age = ageText.toInt(),
            gender = gender,
            weight = weightText.toDouble(),
            imagePath = currentPhotoPath
        )

        viewModel.insertPet(pet) { id ->
            Toast.makeText(requireContext(), "Pet saved!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PET = "pet"

        fun newInstance(pet: Pet? = null): AddPetFragment {
            val fragment = AddPetFragment()
            if (pet != null) {
                val args = Bundle()
                args.putSerializable(ARG_PET, pet)
                fragment.arguments = args
            }
            return fragment
        }
    }
}
