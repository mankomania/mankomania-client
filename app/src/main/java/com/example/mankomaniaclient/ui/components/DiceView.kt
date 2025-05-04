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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mankomaniaclient.model.DiceResult


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
        DiceView(result = diceResult)
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewDiceView() {
    DiceView(result = DiceResult(3, 4))
}