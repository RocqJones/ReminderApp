package com.rocqjones.reminderapp

import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.rocqjones.reminderapp.ui.theme.ReminderAppTheme
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Set up your Compose UI here
        composeTestRule.setContent {
            ReminderAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListItems()
                }
            }
        }
    }

    @Test
    fun testComposableList() {
        composeTestRule.onNodeWithTag("list_items")
    }

    @Test
    fun testComposableCard() {
        composeTestRule.onNodeWithTag("composeCard_0")
    }

    @Test
    fun testOpenReminderDialog() {
        composeTestRule.onNodeWithTag(
            "list_items"
        ).assertExists().onChildren()[0].assertExists().performClick()

        // Verify that the ReminderDialog is displayed
        composeTestRule.onNodeWithTag("reminderDialog").assertExists()

        val schedules = listOf(
            "5 seconds" to 5000L,
            "8 minutes" to 8 * 60 * 1000L,
            "1 day" to 24 * 60 * 60 * 1000L,
            "1 week" to 7 * 24 * 60 * 60 * 1000L
        )

        composeTestRule.onNodeWithTag("reminderDialog")
            .assertExists()

        for ((scheduleText, _) in schedules) {
            composeTestRule.onNodeWithText(scheduleText)
                .assertExists()
        }

        composeTestRule.onNodeWithText("5 seconds").assertExists().performClick()

        // Wait for 5 seconds
        Thread.sleep(TimeUnit.SECONDS.toMillis(5))

        // Check if the notification appears
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val notificationExists = device.wait(
            Until.hasObject(By.pkg("com.rocqjones.reminderapp").depth(0)),
            TimeUnit.SECONDS.toMillis(10)
        )
        Assert.assertTrue("Notification should appear", notificationExists != null)

        // Click the notification
        val notification = device.findObject(By.text("Reminder App."))
        notification?.click()

        // Verify that the MainActivity is launched
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        val currentActivity = instrumentation.targetContext.packageManager.getLaunchIntentForPackage(
            instrumentation.targetContext.packageName
        )?.component?.className
        Assert.assertEquals(
            "MainActivity should be launched",
            MainActivity::class.java.name,
            currentActivity
        )

        // Clear the notification
        val notificationManager = instrumentation.targetContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.cancel(17)
    }
}



