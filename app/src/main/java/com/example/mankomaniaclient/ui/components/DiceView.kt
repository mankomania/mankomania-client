package com.example.mankomaniaclient.ui.components

/**
 * @file DiceView.kt
 * @author eles17
 * @since 3.5.2025
 * @description
 * Composable UI component to display the result of a dice roll.
 * It shows both dice values and their total sum.
 */

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mankomaniaclient.viewmodel.DiceResult


/**
 * Displays the dice roll result.
 *
 * @param result The result of the dice roll, or null if no roll has occurred.
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