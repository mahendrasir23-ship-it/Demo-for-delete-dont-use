package com.example.automation.data

import kotlinx.coroutines.flow.Flow

class AutomationRepository(private val dao: AutomationDao) {

    val tasks: Flow<List<AutomationTask>> = dao.observeAll()

    suspend fun getTask(id: Long): AutomationTask? = dao.getById(id)

    suspend fun createTask(name: String, description: String = ""): Long {
        return dao.insert(AutomationTask(name = name.trim(), description = description.trim()))
    }

    suspend fun updateTask(task: AutomationTask) = dao.update(task)

    suspend fun deleteTask(task: AutomationTask) = dao.delete(task)

    suspend fun addAction(taskId: Long, action: AutomationAction) {
        dao.getById(taskId)?.let { task ->
            dao.update(task.copy(actions = task.actions + action))
        }
    }

    suspend fun removeLastAction(taskId: Long) {
        dao.getById(taskId)?.let { task ->
            if (task.actions.isNotEmpty()) {
                dao.update(task.copy(actions = task.actions.dropLast(1)))
            }
        }
    }

    suspend fun clearActions(taskId: Long) {
        dao.getById(taskId)?.let { task ->
            dao.update(task.copy(actions = emptyList()))
        }
    }
}
