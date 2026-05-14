package com.example.petcare.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String,
    var type: String,
    var breed: String,
    var age: Int,
    var gender: String,
    var weight: Double,
    var imagePath: String? = null
) : Serializable
