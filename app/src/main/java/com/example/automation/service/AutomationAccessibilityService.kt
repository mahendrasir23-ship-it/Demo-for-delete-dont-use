package com.example.automation.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.example.automation.data.AutomationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * AccessibilityService for executing automation tasks. This is the proper Android way
 * to automate app interactions.
 */
class AutomationAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var isRunning = false

    companion object {
        var instance: AutomationAccessibilityService? = null
            private set

        const val ACTION_EXECUTE_TASK = "com.example.automation.EXECUTE_TASK"
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_ACTIONS = "actions"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_EXECUTE_TASK) {
            val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
            if (taskId != -1L) {
                startAutomation(taskId)
            }
        }
        return START_STICKY
    }

    private fun startAutomation(taskId: Long) {
        scope.launch {
            isRunning = true
        }
    }

    fun executeActions(actions: List<AutomationAction>) {
        scope.launch {
            isRunning = true
            for (action in actions) {
                if (!isRunning) break
                try {
                    when (action) {
                        is AutomationAction.ClickAction -> {
                            performClick(action.x, action.y)
                            Thread.sleep(action.delayMs)
                        }
                        is AutomationAction.TextInputAction -> {
                            inputText(action.text)
                            Thread.sleep(action.delayMs)
                        }
                        is AutomationAction.WaitAction -> {
                            Thread.sleep(action.durationMs)
                        }
                        is AutomationAction.SwipeAction -> {
                            performSwipe(
                                action.startX, action.startY,
                                action.endX, action.endY
                            )
                            Thread.sleep(action.delayMs)
                        }
                        is AutomationAction.PressKeyAction -> {
                            performGlobalAction(action.keyCode)
                            Thread.sleep(action.delayMs)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            isRunning = false
        }
    }

    private fun performClick(x: Int, y: Int) {
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()
        dispatchGesture(gesture, null, null)
    }

    private fun performSwipe(startX: Int, startY: Int, endX: Int, endY: Int) {
        val path = Path().apply {
            moveTo(startX.toFloat(), startY.toFloat())
            lineTo(endX.toFloat(), endY.toFloat())
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()
        dispatchGesture(gesture, null, null)
    }

    private fun inputText(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val arguments = android.os.Bundle()
            arguments.putString(
                android.accessibilityservice.AccessibilityService.ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
            rootInActiveWindow?.performAction(
                android.view.accessibility.AccessibilityNodeInfo.ACTION_SET_TEXT,
                arguments
            )
        }
    }

    fun stopAutomation() {
        isRunning = false
    }
}
