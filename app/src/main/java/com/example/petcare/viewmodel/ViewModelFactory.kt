package com.example.petcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.petcare.repository.*

class ViewModelFactory(private val repository: Any) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PetViewModel(repository as PetRepository) as T
        }
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository as UserRepository) as T
        }
        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordViewModel(repository as RecordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
