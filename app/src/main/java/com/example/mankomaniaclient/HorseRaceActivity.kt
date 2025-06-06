package com.example.mankomaniaclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mankomaniaclient.ui.screen.HorseRaceScreen
import com.example.mankomaniaclient.ui.viewmodel.HorseRaceViewModel

class HorseRaceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HorseRaceScreen(viewModel = HorseRaceViewModel())
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, HorseRaceActivity::class.java)
        }
    }
}