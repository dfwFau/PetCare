package com.example.petcare.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.petcare.data.entity.Vaccination

@Dao
interface VaccinationDao {
    @Query("SELECT * FROM vaccinations WHERE petId = :petId ORDER BY dateGiven DESC")
    fun getVaccinationsForPet(petId: Long): LiveData<List<Vaccination>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccination(vaccination: Vaccination)

    @Update
    suspend fun updateVaccination(vaccination: Vaccination)

    @Delete
    suspend fun deleteVaccination(vaccination: Vaccination)
}
