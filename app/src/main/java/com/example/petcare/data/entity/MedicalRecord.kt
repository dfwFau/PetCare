package com.example.petcare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "medical_records",
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MedicalRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    var illness: String,
    var treatment: String,
    var medicine: String,
    var date: Long,
    var notes: String
) : Serializable
