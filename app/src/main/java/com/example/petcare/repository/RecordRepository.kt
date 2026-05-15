package com.example.petcare.repository

import androidx.lifecycle.LiveData
import com.example.petcare.data.dao.*
import com.example.petcare.data.entity.*

class RecordRepository(
    private val vaccinationDao: VaccinationDao,
    private val feedingDao: FeedingDao,
    private val appointmentDao: AppointmentDao,
    private val medicalRecordDao: MedicalRecordDao
) {

    // Vaccinations
    fun getVaccinationsForPet(petId: Long): LiveData<List<Vaccination>> = vaccinationDao.getVaccinationsForPet(petId)
    suspend fun insertVaccination(vaccination: Vaccination) = vaccinationDao.insertVaccination(vaccination)
    suspend fun updateVaccination(vaccination: Vaccination) = vaccinationDao.updateVaccination(vaccination)
    suspend fun deleteVaccination(vaccination: Vaccination) = vaccinationDao.deleteVaccination(vaccination)

    // Feeding
    fun getFeedingSchedulesForPet(petId: Long): LiveData<List<FeedingSchedule>> = feedingDao.getFeedingSchedulesForPet(petId)
    suspend fun insertFeedingSchedule(schedule: FeedingSchedule) = feedingDao.insertFeedingSchedule(schedule)
    suspend fun updateFeedingSchedule(schedule: FeedingSchedule) = feedingDao.updateFeedingSchedule(schedule)
    suspend fun deleteFeedingSchedule(schedule: FeedingSchedule) = feedingDao.deleteFeedingSchedule(schedule)

    // Appointments
    fun getAppointmentsForPet(petId: Long): LiveData<List<Appointment>> = appointmentDao.getAppointmentsForPet(petId)
    suspend fun insertAppointment(appointment: Appointment) = appointmentDao.insertAppointment(appointment)
    suspend fun updateAppointment(appointment: Appointment) = appointmentDao.updateAppointment(appointment)
    suspend fun deleteAppointment(appointment: Appointment) = appointmentDao.deleteAppointment(appointment)
    suspend fun getAppointmentById(id: Long): Appointment? = appointmentDao.getAppointmentById(id)
    fun getAllAppointments(): LiveData<List<Appointment>> = appointmentDao.getAllAppointments()
    fun getUpcomingAppointments(currentTime: Long): LiveData<List<Appointment>> = appointmentDao.getUpcomingAppointments(currentTime)

    // Medical Records
    fun getMedicalRecordsForPet(petId: Long): LiveData<List<MedicalRecord>> = medicalRecordDao.getMedicalRecordsForPet(petId)
    suspend fun insertMedicalRecord(record: MedicalRecord) = medicalRecordDao.insertMedicalRecord(record)
    suspend fun updateMedicalRecord(record: MedicalRecord) = medicalRecordDao.updateMedicalRecord(record)
    suspend fun deleteMedicalRecord(record: MedicalRecord) = medicalRecordDao.deleteMedicalRecord(record)
}
