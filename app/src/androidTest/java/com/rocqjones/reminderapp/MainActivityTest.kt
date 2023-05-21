package com.rocqjones.reminderapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rocqjones.reminderapp.ui.theme.ReminderAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Set up your Compose UI here
        composeTestRule.setContent {
            MainActivity()
        }
    }

    @Test
    fun testComposableList() {
        composeTestRule.onNodeWithTag("list_items")
    }

    @Test
    fun testComposableCard() {
        composeTestRule.onNodeWithTag("composeCard")
    }

    @Test
    fun testComposableCardClick() {
        composeTestRule.onNodeWithTag("list_items")
            .onChildren()
            .assertAny(hasTestTag("composeCard"))[0].assertHasClickAction().performClick()
    }


    /*@get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Wait for the UI to be visible
        composeTestRule.setContent {
            ReminderAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListItems(modifier = Modifier.testTag("list_items"))
                }
            }
        }
    }

    @Test
    fun testReminderDialogNotificationAndNavigation() {
        // Click on the first card
        composeTestRule.onNodeWithTag("list_items").onChildren()[0].performClick()

        // Wait for the ReminderDialog to be displayed
        onView(withText(R.string.title_reminder))
            .check(matches(isDisplayed()))

        // Click on the "5 seconds" schedule
        onView(withText(R.string.schedule_5_seconds))
            .perform(click())

        // Wait for 5 seconds
        Thread.sleep(5000)

        // Check if the notification appears
        onView(withText("Reminder App."))
            .check(matches(isDisplayed()))

        // Click on the notification
        onView(withText("Reminder App."))
            .perform(click())

        // Check if the defined activity is opened
        composeTestRule.onNodeWithText("Reminder App")
            .assertIsDisplayed()
    }*/
}



