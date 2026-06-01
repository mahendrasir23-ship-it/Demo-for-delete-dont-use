package com.example.habittracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.habittracker.HabitApplication
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    val habits: StateFlow<List<Habit>> = repository.habits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addHabit(name: String, emoji: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.addHabit(name, emoji) }
    }

    fun toggleToday(habit: Habit) {
        viewModelScope.launch { repository.toggleToday(habit) }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch { repository.deleteHabit(habit) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HabitApplication
                return HabitViewModel(app.repository) as T
            }
        }
    }
}
