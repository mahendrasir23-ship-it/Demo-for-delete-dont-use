package com.example.habittracker.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Thin layer over [HabitDao] that exposes domain operations to the ViewModel and
 * keeps Room details out of the UI.
 */
class HabitRepository(private val dao: HabitDao) {

    val habits: Flow<List<Habit>> = dao.observeAll()

    suspend fun addHabit(name: String, emoji: String) {
        dao.insert(Habit(name = name.trim(), emoji = emoji))
    }

    suspend fun deleteHabit(habit: Habit) = dao.delete(habit)

    /** Toggle today's completion for the given habit. */
    suspend fun toggleToday(habit: Habit, today: LocalDate = LocalDate.now()) {
        val updated = if (today in habit.completedDates) {
            habit.copy(completedDates = habit.completedDates - today)
        } else {
            habit.copy(completedDates = habit.completedDates + today)
        }
        dao.update(updated)
    }
}
