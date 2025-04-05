package com.example.mankomaniaclient

object NumberChecker {
    fun describeNumber(n: Int): String {
        return when {
            n == 0 -> "zero"
            n < 0 -> "negative"
            else -> "positive"
        }
    }

