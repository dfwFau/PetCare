package com.example.petcare.viewmodel

import androidx.lifecycle.*
import com.example.petcare.data.entity.Pet
import com.example.petcare.repository.PetRepository
import kotlinx.coroutines.launch

class PetViewModel(private val repository: PetRepository) : ViewModel() {

    val allPets: LiveData<List<Pet>> = repository.allPets

    fun searchPets(query: String): LiveData<List<Pet>> {
        return repository.searchPets(query)
    }

    fun insertPet(pet: Pet, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertPet(pet)
            onResult(id)
        }
    }

    fun updatePet(pet: Pet) = viewModelScope.launch {
        repository.updatePet(pet)
    }

    fun deletePet(pet: Pet) = viewModelScope.launch {
        repository.deletePet(pet)
    }
}
