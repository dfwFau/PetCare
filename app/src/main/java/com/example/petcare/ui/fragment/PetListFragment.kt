package com.example.petcare.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.R
import com.example.petcare.database.AppDatabase
import com.example.petcare.databinding.FragmentPetListBinding
import com.example.petcare.repository.PetRepository
import com.example.petcare.ui.adapter.PetAdapter
import com.example.petcare.viewmodel.PetViewModel
import com.example.petcare.viewmodel.ViewModelFactory

class PetListFragment : Fragment() {

    private var _binding: FragmentPetListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PetViewModel
    private lateinit var adapter: PetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = PetRepository(AppDatabase.getDatabase(requireContext()).petDao())
        viewModel = ViewModelProvider(this, ViewModelFactory(repository))[PetViewModel::class.java]

        setupRecyclerView()

        viewModel.allPets.observe(viewLifecycleOwner) { pets ->
            adapter.submitList(pets)
            binding.tvEmpty.visibility = if (pets.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddPet.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddPetFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchPets(newText ?: "").observe(viewLifecycleOwner) { pets ->
                    adapter.submitList(pets)
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = PetAdapter { pet ->
            // On Click Pet
        }
        binding.rvPets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPets.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
