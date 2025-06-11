package com.example.mankomaniaclient

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HorseRaceActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(HorseRaceActivity::class.java)

    @Test
    fun activityLaunchesSuccessfully() {
        // Verifica che il testo "Horse Race" sia visibile
        onView(withText("Horse Race")).check(matches(isDisplayed()))
        // Verifica che il pulsante "Spin Roulette" sia visibile
        onView(withText("Spin Roulette")).check(matches(isDisplayed()))
    }
}