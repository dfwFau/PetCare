package com.example.petcare.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.petcare.data.entity.FeedingSchedule

@Dao
interface FeedingDao {
    @Query("SELECT * FROM feeding_schedules WHERE petId = :petId")
    fun getFeedingSchedulesForPet(petId: Long): LiveData<List<FeedingSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedingSchedule(schedule: FeedingSchedule)

    @Update
    suspend fun updateFeedingSchedule(schedule: FeedingSchedule)

    @Delete
    suspend fun deleteFeedingSchedule(schedule: FeedingSchedule)
}
