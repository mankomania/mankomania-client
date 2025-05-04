package com.example.mankomaniaclient.model
/**
 * @file GameViewModel.kt
 * @author eles17
 * @since 3.5.2025
 * @description
 * Data class representing the result of a dice roll.
 * Holds both dice values and calculates the sum.
 **/

data class DiceResult(val die1: Int, val die2: Int) {
    val sum: Int get() = die1 + die2
}