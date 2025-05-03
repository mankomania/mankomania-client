package com.example.mankomaniaclient.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mankomaniaclient.viewmodel.DiceResult


/**
 * # DiceView
 *
 * Zeigt einen Würfel und erlaubt den Benutzer, ihn zu werfen.
 *
 * @author
 * @since
 * @description Composable-Funktion für Würfelwurf-Interaktion.
 */
@Composable
fun DiceView(result: DiceResult?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        if(result != null){
            Text("Die 1: ${result.die1}", fontSize = 20.sp)
            Text("Die 2: ${result.die2}", fontSize = 20.sp)
            Text("Total: ${result.sum}", fontSize = 22.sp)
        } else {
            Text("No roll yet", fontSize = 20.sp)
        }
    }
}