package com.example.petcare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "appointments",
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    var clinicName: String,
    var dateTime: Long,
    var purpose: String,
    var notes: String,
    var isDone: Boolean = false
) : Serializable
