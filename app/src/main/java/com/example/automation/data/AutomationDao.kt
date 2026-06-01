package com.example.automation.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationDao {

    @Query("SELECT * FROM automation_tasks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AutomationTask>>

    @Query("SELECT * FROM automation_tasks WHERE id = :id")
    suspend fun getById(id: Long): AutomationTask?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: AutomationTask): Long

    @Update
    suspend fun update(task: AutomationTask)

    @Delete
    suspend fun delete(task: AutomationTask)
}
