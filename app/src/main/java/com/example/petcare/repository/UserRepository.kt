package com.example.petcare.repository

import com.example.petcare.data.dao.UserDao
import com.example.petcare.data.entity.User

class UserRepository(private val userDao: UserDao) {

    suspend fun login(username: String, password: String): User? {
        return userDao.login(username, password)
    }

    suspend fun register(user: User) {
        userDao.register(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
}
