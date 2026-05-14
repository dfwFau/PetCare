package com.example.petcare.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.petcare.data.dao.*
import com.example.petcare.data.entity.*

@Database(
    entities = [
        Pet::class,
        Vaccination::class,
        FeedingSchedule::class,
        Appointment::class,
        MedicalRecord::class,
        User::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun vaccinationDao(): VaccinationDao
    abstract fun feedingDao(): FeedingDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun medicalRecordDao(): MedicalRecordDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pet_care_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
