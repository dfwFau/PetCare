package com.example.petcare.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.petcare.data.entity.MedicalRecord

@Dao
interface MedicalRecordDao {
    @Query("SELECT * FROM medical_records WHERE petId = :petId ORDER BY date DESC")
    fun getMedicalRecordsForPet(petId: Long): LiveData<List<MedicalRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecord(record: MedicalRecord)

    @Update
    suspend fun updateMedicalRecord(record: MedicalRecord)

    @Delete
    suspend fun deleteMedicalRecord(record: MedicalRecord)
}
