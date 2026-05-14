package com.example.petcare.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "feeding_schedules",
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FeedingSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    var scheduleType: String, // Morning, Afternoon, Evening
    var feedingTime: String, // HH:mm
    var isEnabled: Boolean = true
) : Serializable
