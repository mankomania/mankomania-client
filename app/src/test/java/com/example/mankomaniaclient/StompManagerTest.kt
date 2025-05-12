package com.example.mankomaniaclient.api

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

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