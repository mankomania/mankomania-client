package com.example.mankomaniaclient.ui.components

/**
 * @file DiceView.kt
 * @author eles17
 * @since 03.05.2025
 * @description
 * Composable UI component to display the result of a dice roll.
 * It shows both dice values and their total sum.
 */

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mankomaniaclient.model.DiceResult

/**
 * Displays the dice roll result.
 *
 * @param result The result of the dice roll, or null if no roll has occurred yet.
 */
@Composable
fun DiceView(result: DiceResult?) {
    if (result == null) {
        Text("No dice rolled yet.")
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Die 1: ${result.die1}", fontSize = 20.sp)
            Text("Die 2: ${result.die2}", fontSize = 20.sp)
            Text("Total: ${result.sum}", fontSize = 24.sp)
        }
    }
}

/**
 * Preview of the DiceView composable with a sample DiceResult.
 */
@Preview(showBackground = true)
@Composable
fun PreviewDiceView() {
    DiceView(result = DiceResult(3, 4))
}