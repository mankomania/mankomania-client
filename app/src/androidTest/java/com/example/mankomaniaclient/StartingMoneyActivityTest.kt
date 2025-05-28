// File: app/src/androidTest/java/com/example/mankomaniaclient/StartingMoneyActivityTest.kt
package com.example.mankomaniaclient

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartingMoneyActivityTest {

    @Test
    fun activity_starts_withIntentExtra() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), StartingMoneyActivity::class.java)
        intent.putExtra("playerId", "test-player-id")

        val scenario = ActivityScenario.launch<StartingMoneyActivity>(intent)


        scenario.close()
    }
    @Test
    fun activity_starts_withDefaultPlayerId() {
        val scenario = ActivityScenario.launch(StartingMoneyActivity::class.java)
        // Test passes if the activity launches and renders without crashing
        scenario.close()
    }
}

