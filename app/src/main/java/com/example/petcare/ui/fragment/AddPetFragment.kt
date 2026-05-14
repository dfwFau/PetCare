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
import androidx.activity.result.contract.ActivityResultContracts
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

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                Glide.with(this).load(path).into(binding.ivPet)
            }
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

        binding.btnCapture.setOnClickListener {
            capturePhoto()
        }

        binding.btnSave.setOnClickListener {
            savePet()
        }
    }

    private fun capturePhoto() {
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
}
