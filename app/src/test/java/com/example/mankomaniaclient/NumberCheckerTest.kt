package com.example.mankomaniaclient

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class NumberCheckerTest {

    @Test
    fun testDescribeNumber() {
        assertEquals("zero", NumberChecker.describeNumber(0))
        assertEquals("positive", NumberChecker.describeNumber(5))
        assertEquals("negative", NumberChecker.describeNumber(-2))
    }

}