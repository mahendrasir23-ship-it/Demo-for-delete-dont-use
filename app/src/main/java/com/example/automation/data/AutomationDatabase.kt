package com.example.automation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AutomationTask::class], version = 1, exportSchema = false)
@TypeConverters(ActionListConverter::class)
abstract class AutomationDatabase : RoomDatabase() {

    abstract fun automationDao(): AutomationDao

    companion object {
        @Volatile
        private var INSTANCE: AutomationDatabase? = null

        fun getInstance(context: Context): AutomationDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AutomationDatabase::class.java,
                    "automation.db"
                ).build().also { INSTANCE = it }
            }
    }
}
