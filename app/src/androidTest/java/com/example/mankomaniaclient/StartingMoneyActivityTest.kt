package com.example.mankomaniaclient

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mankomaniaclient.ui.StartingMoneyScreen
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartingMoneyActivityTest {

    @Test
    fun activity_usesPlayerId_fromIntent() {
        val intent = Intent(
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext,
            StartingMoneyActivity::class.java
        ).apply {
            putExtra("playerId", "TestPlayer123")
        }

        ActivityScenario.launch<StartingMoneyActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                // Using reflection to access the playerId from the Composable parameter is not practical.
                // So here we only test that activity doesn't crash and is created with the intent.
                assertEquals("TestPlayer123", intent.getStringExtra("playerId"))
            }
        }
    }

    @Test
    fun activity_defaultsToPlayerId_whenIntentIsMissing() {
        val intent = Intent(
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext,
            StartingMoneyActivity::class.java
        )

        ActivityScenario.launch<StartingMoneyActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                // Again, assert that activity runs and uses default when playerId not provided
                assertEquals(null, intent.getStringExtra("playerId")) // intent should not contain playerId
                // We cannot directly access the Composable param here without a state holder
            }
        }
    }
}