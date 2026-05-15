package com.example.petcare.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.petcare.R
import com.example.petcare.data.entity.Pet
import com.example.petcare.database.AppDatabase
import com.example.petcare.databinding.FragmentPetDetailBinding
import com.example.petcare.repository.PetRepository
import com.example.petcare.viewmodel.PetViewModel
import com.example.petcare.viewmodel.ViewModelFactory

class PetDetailFragment : Fragment() {

    private var _binding: FragmentPetDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PetViewModel
    private var pet: Pet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pet = it.getSerializable(ARG_PET) as? Pet
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = PetRepository(AppDatabase.getDatabase(requireContext()).petDao())
        viewModel = ViewModelProvider(this, ViewModelFactory(repository))[PetViewModel::class.java]

        displayPetDetails()

        binding.btnDeletePet.setOnClickListener {
            showDeleteConfirmation()
        }

        binding.btnEditPet.setOnClickListener {
            pet?.let {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddPetFragment.newInstance(it))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun displayPetDetails() {
        pet?.let {
            binding.tvNameDetail.text = it.name
            binding.tvBreedDetail.text = "${it.type} | ${it.breed}"
            binding.tvAgeDetail.text = it.age.toString()
            binding.tvGenderDetail.text = it.gender
            binding.tvWeightDetail.text = getString(R.string.weight_kg, it.weight.toString())

            if (it.imagePath != null) {
                Glide.with(this).load(it.imagePath).into(binding.ivPetDetail)
            } else {
                binding.ivPetDetail.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_pet_title)
            .setMessage(getString(R.string.delete_pet_message, pet?.name))
            .setPositiveButton(R.string.delete_action) { _, _ ->
                pet?.let {
                    viewModel.deletePet(it)
                    Toast.makeText(requireContext(), R.string.pet_deleted, Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
            .setNegativeButton(R.string.cancel_action, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PET = "pet"

        fun newInstance(pet: Pet): PetDetailFragment {
            val fragment = PetDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_PET, pet)
            fragment.arguments = args
            return fragment
        }
    }
}
