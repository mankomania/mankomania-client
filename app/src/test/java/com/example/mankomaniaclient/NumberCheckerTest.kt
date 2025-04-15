package com.example.mankomaniaclient

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class NumberCheckerTest {

    @Test
    fun testDescribeNumber() {
        assertEquals("zero", NumberChecker.describeNumber(0))
        assertEquals("positive", NumberChecker.describeNumber(5))
        assertEquals("negative", NumberChecker.describeNumber(-2))
    }

    @Test
    fun `should return true for even number`() {
        val result = NumberChecker.NumberUtils.isEven(4)
        assertTrue(result)
    }

    @Test
    fun `should return false for odd number`() {
        val result = NumberChecker.NumberUtils.isEven(5)
        assertFalse(result)
    }

}
