package com.example.mankomaniaclient

import com.example.mankomaniaclient.model.DiceResult
import org.junit.Assert.assertEquals
import org.junit.Test

class DiceResultTest {
    @Test
    fun testDiceSumCalculation() {
        val result = DiceResult(2, 5)
        assertEquals(7, result.sum)
    }
}