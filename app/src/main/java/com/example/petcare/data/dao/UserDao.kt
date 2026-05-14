package com.example.petcare.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.petcare.data.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Insert
    suspend fun register(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}
