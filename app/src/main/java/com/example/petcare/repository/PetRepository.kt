package com.example.petcare.repository

import androidx.lifecycle.LiveData
import com.example.petcare.data.dao.PetDao
import com.example.petcare.data.entity.Pet

class PetRepository(private val petDao: PetDao) {

    val allPets: LiveData<List<Pet>> = petDao.getAllPets()

    fun searchPets(query: String): LiveData<List<Pet>> {
        return petDao.searchPets("%$query%")
    }

    suspend fun insertPet(pet: Pet): Long {
        return petDao.insertPet(pet)
    }

    suspend fun updatePet(pet: Pet) {
        petDao.updatePet(pet)
    }

    suspend fun deletePet(pet: Pet) {
        petDao.deletePet(pet)
    }

    suspend fun getPetById(petId: Long): Pet? {
        return petDao.getPetById(petId)
    }
}
