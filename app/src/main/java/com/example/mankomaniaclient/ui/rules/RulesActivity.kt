package com.example.mankomaniaclient.ui.rules

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mankomaniaclient.ui.screens.RulesScreen
import com.example.mankomaniaclient.viewmodel.RulesViewModel

class RulesActivity : ComponentActivity() {
    private val viewModel: RulesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RulesScreen(
                        viewModel = viewModel,
                        onNavigateUp = { finish() }
                    )
                }
            }
        }
    }
}