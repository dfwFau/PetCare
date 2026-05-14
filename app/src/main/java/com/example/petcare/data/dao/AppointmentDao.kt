package com.example.petcare.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.petcare.data.entity.Appointment

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments WHERE petId = :petId ORDER BY dateTime ASC")
    fun getAppointmentsForPet(petId: Long): LiveData<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)
}
