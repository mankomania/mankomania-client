package com.example.mankomaniaclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.mankomaniaclient.ui.screen.HorseRaceScreen
import com.example.mankomaniaclient.viewmodel.HorseRaceViewModel

class HorseRaceActivity : ComponentActivity() {

    private lateinit var viewModel: HorseRaceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[HorseRaceViewModel::class.java]

        setContent {
            HorseRaceScreen(viewModel)
        }
    }
}