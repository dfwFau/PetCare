package com.example.petcare.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.petcare.data.entity.Pet

@Dao
interface PetDao {
    @Query("SELECT * FROM pets ORDER BY name ASC")
    fun getAllPets(): LiveData<List<Pet>>

    @Query("SELECT * FROM pets WHERE name LIKE :searchQuery OR breed LIKE :searchQuery")
    fun searchPets(searchQuery: String): LiveData<List<Pet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet): Long

    @Update
    suspend fun updatePet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("SELECT * FROM pets WHERE id = :petId")
    suspend fun getPetById(petId: Long): Pet?
}
