package com.example.habittracker

import android.app.Application
import com.example.habittracker.data.HabitDatabase
import com.example.habittracker.data.HabitRepository

/**
 * Owns the singletons (database + repository) for the app's lifetime so we don't
 * need a DI framework for a sample of this size.
 */
class HabitApplication : Application() {
    val repository: HabitRepository by lazy {
        HabitRepository(HabitDatabase.getInstance(this).habitDao())
    }
}
