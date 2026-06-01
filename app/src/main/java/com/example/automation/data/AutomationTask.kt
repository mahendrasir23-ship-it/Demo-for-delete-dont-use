package com.example.automation.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDateTime

@Entity(tableName = "automation_tasks")
@TypeConverters(ActionListConverter::class)
data class AutomationTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val actions: List<AutomationAction> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isActive: Boolean = true
)

sealed class AutomationAction {
    data class ClickAction(
        val x: Int,
        val y: Int,
        val delayMs: Long = 500
    ) : AutomationAction()

    data class TextInputAction(
        val text: String,
        val delayMs: Long = 500
    ) : AutomationAction()

    data class WaitAction(
        val durationMs: Long
    ) : AutomationAction()

    data class SwipeAction(
        val startX: Int,
        val startY: Int,
        val endX: Int,
        val endY: Int,
        val delayMs: Long = 500
    ) : AutomationAction()

    data class PressKeyAction(
        val keyCode: Int,
        val delayMs: Long = 500
    ) : AutomationAction()
}

class ActionListConverter {
    @TypeConverter
    fun fromActionList(actions: List<AutomationAction>): String =
        actions.joinToString("|") { serializeAction(it) }

    @TypeConverter
    fun toActionList(value: String): List<AutomationAction> =
        if (value.isBlank()) emptyList()
        else value.split("|").mapNotNull { deserializeAction(it) }

    private fun serializeAction(action: AutomationAction): String = when (action) {
        is AutomationAction.ClickAction -> "CLICK:${action.x},${action.y},${action.delayMs}"
        is AutomationAction.TextInputAction -> "TEXT:${action.text.replace(":", "\\:")},${action.delayMs}"
        is AutomationAction.WaitAction -> "WAIT:${action.durationMs}"
        is AutomationAction.SwipeAction -> "SWIPE:${action.startX},${action.startY},${action.endX},${action.endY},${action.delayMs}"
        is AutomationAction.PressKeyAction -> "KEY:${action.keyCode},${action.delayMs}"
    }

    private fun deserializeAction(str: String): AutomationAction? {
        val parts = str.split(":", limit = 2)
        if (parts.size < 2) return null
        return when (parts[0]) {
            "CLICK" -> {
                val coords = parts[1].split(",")
                if (coords.size >= 3) AutomationAction.ClickAction(
                    coords[0].toInt(), coords[1].toInt(), coords[2].toLong()
                ) else null
            }
            "TEXT" -> {
                val content = parts[1].split(",")
                if (content.isNotEmpty()) AutomationAction.TextInputAction(
                    content[0].replace("\\:", ":"),
                    if (content.size > 1) content[1].toLong() else 500
                ) else null
            }
            "WAIT" -> {
                val duration = parts[1].toLongOrNull()
                if (duration != null) AutomationAction.WaitAction(duration) else null
            }
            "SWIPE" -> {
                val coords = parts[1].split(",")
                if (coords.size >= 5) AutomationAction.SwipeAction(
                    coords[0].toInt(), coords[1].toInt(),
                    coords[2].toInt(), coords[3].toInt(),
                    coords[4].toLong()
                ) else null
            }
            "KEY" -> {
                val content = parts[1].split(",")
                if (content.size >= 2) AutomationAction.PressKeyAction(
                    content[0].toInt(), content[1].toLong()
                ) else null
            }
            else -> null
        }
    }
}
