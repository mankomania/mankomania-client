package com.example.mankomaniaclient

import com.example.mankomaniaclient.api.StompManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StompManagerTest {

    @Test
    fun testSendRollRequestDoesNotThrow() = runTest {
        StompManager.sendRollRequest("testPlayer")
        assertTrue(true)
    }

    @Test
    fun testConnectAndSubscribeDoesNotThrow() = runTest {
        StompManager.connectAndSubscribe("testPlayer")
        assertTrue(true)
    }
}