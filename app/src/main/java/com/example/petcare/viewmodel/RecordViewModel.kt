package com.example.petcare.viewmodel

import androidx.lifecycle.*
import com.example.petcare.data.entity.*
import com.example.petcare.repository.RecordRepository
import kotlinx.coroutines.launch

class RecordViewModel(private val repository: RecordRepository) : ViewModel() {

    fun getVaccinations(petId: Long) = repository.getVaccinationsForPet(petId)
    fun insertVaccination(vaccination: Vaccination) = viewModelScope.launch { repository.insertVaccination(vaccination) }
    fun updateVaccination(vaccination: Vaccination) = viewModelScope.launch { repository.updateVaccination(vaccination) }
    fun deleteVaccination(vaccination: Vaccination) = viewModelScope.launch { repository.deleteVaccination(vaccination) }

    fun getFeedingSchedules(petId: Long) = repository.getFeedingSchedulesForPet(petId)
    fun insertFeedingSchedule(schedule: FeedingSchedule) = viewModelScope.launch { repository.insertFeedingSchedule(schedule) }
    fun updateFeedingSchedule(schedule: FeedingSchedule) = viewModelScope.launch { repository.updateFeedingSchedule(schedule) }
    fun deleteFeedingSchedule(schedule: FeedingSchedule) = viewModelScope.launch { repository.deleteFeedingSchedule(schedule) }

    fun getAppointments(petId: Long) = repository.getAppointmentsForPet(petId)
    fun insertAppointment(appointment: Appointment) = viewModelScope.launch { repository.insertAppointment(appointment) }
    fun updateAppointment(appointment: Appointment) = viewModelScope.launch { repository.updateAppointment(appointment) }
    fun deleteAppointment(appointment: Appointment) = viewModelScope.launch { repository.deleteAppointment(appointment) }

    fun getMedicalRecords(petId: Long) = repository.getMedicalRecordsForPet(petId)
    fun insertMedicalRecord(record: MedicalRecord) = viewModelScope.launch { repository.insertMedicalRecord(record) }
    fun updateMedicalRecord(record: MedicalRecord) = viewModelScope.launch { repository.updateMedicalRecord(record) }
    fun deleteMedicalRecord(record: MedicalRecord) = viewModelScope.launch { repository.deleteMedicalRecord(record) }
}
