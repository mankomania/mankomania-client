package com.example.mankomaniaclient

import com.example.mankomaniaclient.model.DiceResult
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class DiceResultTest {
    @Test
    fun testDiceSumCalculation() {
        val result = DiceResult(2, 5)
        assertEquals(7, result.sum)
    }
    @Test
    fun testSumFieldIsCorrect() {
        val result = DiceResult(3, 4)
        println("Sum = ${result.sum}")
        assertEquals(7, result.sum)
    }
}