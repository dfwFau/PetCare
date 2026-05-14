package com.example.petcare.viewmodel

import androidx.lifecycle.*
import com.example.petcare.data.entity.User
import com.example.petcare.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(username, password)
            _loginResult.postValue(user)
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            val existingUser = repository.getUserByUsername(username)
            if (existingUser == null) {
                repository.register(User(username = username, password = password))
                _registerResult.postValue(true)
            } else {
                _registerResult.postValue(false)
            }
        }
    }
}
