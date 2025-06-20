package com.example.mankomaniaclient.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HorseRaceVisual(winner: HorseColor?) {
    val horses = HorseColor.values()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        horses.forEach { horse ->
            val isWinner = horse == winner
            val backgroundColor = when (horse) {
                HorseColor.RED -> Color.Red
                HorseColor.BLUE -> Color.Blue
                HorseColor.YELLOW -> Color.Yellow
                HorseColor.GREEN -> Color.Green
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        color = backgroundColor.copy(alpha = if (isWinner) 1f else 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = horse.displayName,
                    fontSize = 18.sp,
                    color = if (isWinner) Color.White else Color.Black
                )
            }
        }
    }
}