package com.example.mankomaniaclient

import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class NumberCheckerTest {

    @Test
    fun testIsEven() {
        assertTrue(NumberChecker.isEven(4))
        assertFalse(NumberChecker.isEven(5))
    }

}