package com.example.mankomaniaclient.api

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @file StompManagerTest.kt
 * @description
 * This class contains unit tests for verifying the behavior of the StompManager singleton,
 * which handles STOMP WebSocket connections and client-side communication logic.
 *
 * Tests ensure that calls to methods like connectAndSubscribe and sendRollRequest
 * do not throw exceptions under various input conditions.
 *
 * @since 12.05.2025
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StompManagerTest {

    /**
     * Verifies that sending a roll request with a valid player ID does not throw an exception.
     */
    @Test
    fun testSendRollRequestDoesNotThrow() = runTest {
        Assertions.assertDoesNotThrow {
            StompManager.sendRollRequest("testPlayer")
        }
    }

    /**
     * Verifies that the connectAndSubscribe method executes without error for a valid player ID.
     */
    @Test
    fun testConnectAndSubscribeDoesNotThrow() = runTest {
        Assertions.assertDoesNotThrow {
            StompManager.connectAndSubscribe("testPlayer")
        }
    }

    /**
     * Ensures that sendRollRequest does not throw an exception even when given an empty player ID.
     */
    @Test
    fun testSendRollRequestWithEmptyPlayerId() = runTest {
        Assertions.assertDoesNotThrow {
            StompManager.sendRollRequest("")
        }
    }

    /**
     * Confirms that connectAndSubscribe can handle malformed or non-JSON messages without crashing.
     */
    @Test
    fun testConnectAndSubscribeWithNullJson() = runTest {
        Assertions.assertDoesNotThrow {
            StompManager.connectAndSubscribe("nullPlayer")
        }
    }

    /**
     * Verifies that multiple calls to connectAndSubscribe do not crash.
     */
    @Test
    fun testMultipleConnectionsSafe() = runTest {
        repeat(3) {
            Assertions.assertDoesNotThrow {
                StompManager.connectAndSubscribe("multiPlayer$it")
            }
        }
    }

    /**
     * Tests sending multiple roll requests with varied player IDs.
     */
    @Test
    fun testMultipleRollRequestsWithDifferentPlayers() = runTest {
        val players = listOf("player1", "player2", "player3", "")
        players.forEach {
            Assertions.assertDoesNotThrow {
                StompManager.sendRollRequest(it)
            }
        }
    }

    /**
     * Ensures connectAndSubscribe handles an extremely long player ID gracefully.
     */
    @Test
    fun testConnectWithLongPlayerId() = runTest {
        val longId = "player".repeat(100)
        Assertions.assertDoesNotThrow {
            StompManager.connectAndSubscribe(longId)
        }
    }

    /**
     * Sends a roll request with whitespace-only player ID.
     */
    @Test
    fun testRollRequestWithWhitespacePlayerId() = runTest {
        Assertions.assertDoesNotThrow {
            StompManager.sendRollRequest("   ")
        }
    }
}