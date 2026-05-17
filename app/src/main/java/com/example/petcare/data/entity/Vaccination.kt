package com.example.petcare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "vaccinations",
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Vaccination(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    var vaccineName: String,
    var dateGiven: Long,
    var nextDate: Long,
    var notes: String,
    var isDone: Boolean = false
) : Serializable
