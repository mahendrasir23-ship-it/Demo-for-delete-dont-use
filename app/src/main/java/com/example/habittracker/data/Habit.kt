package com.example.habittracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate

/**
 * A single habit the user wants to build, along with the set of dates on which
 * it has been completed. Completion history is what powers streaks.
 */
@Entity(tableName = "habits")
@TypeConverters(DateSetConverter::class)
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String = "✅",
    val completedDates: Set<LocalDate> = emptySet(),
    val createdAt: LocalDate = LocalDate.now()
) {
    /** Whether this habit has been marked done for [date] (defaults to today). */
    fun isCompletedOn(date: LocalDate = LocalDate.now()): Boolean =
        date in completedDates

    /**
     * Current consecutive-day streak counting back from today. If today is not
     * yet completed the streak is measured from yesterday so an unchecked
     * morning doesn't zero out a long run.
     */
    fun currentStreak(today: LocalDate = LocalDate.now()): Int {
        if (completedDates.isEmpty()) return 0
        var cursor = if (today in completedDates) today else today.minusDays(1)
        var streak = 0
        while (cursor in completedDates) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}

/** Stores a Set<LocalDate> as a comma-separated list of ISO date strings. */
class DateSetConverter {
    @TypeConverter
    fun fromDates(dates: Set<LocalDate>): String =
        dates.joinToString(",") { it.toString() }

    @TypeConverter
    fun toDates(value: String): Set<LocalDate> =
        if (value.isBlank()) emptySet()
        else value.split(",").map { LocalDate.parse(it) }.toSet()
}
