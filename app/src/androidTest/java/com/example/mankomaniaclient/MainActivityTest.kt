// app/src/androidTest/java/com/example/mankomaniaclient/MainActivityTest.kt
package com.example.mankomaniaclient

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun buttons_areVisible_andClickable() {
        // Prüft, dass die Buttons existieren und lässt sie anklicken.
        composeRule.onNodeWithText("Connect")
            .assertExists()
            .performClick()

        composeRule.onNodeWithText("Send Hello")
            .assertExists()
            .performClick()

        // Info‑Text ebenfalls vorhanden?
        composeRule.onNodeWithText("→ schau in Logcat nach D/WebSocket …")
            .assertExists()
    }
}
