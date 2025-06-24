package com.example.mankomaniaclient

import com.example.mankomaniaclient.model.Player
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
class PlayerTest {

    @Test
    fun `player model should correctly store properties`() {
        val player = Player(
            id = "123",
            name = "Valentina",
            money = 1000000
        )

        assertEquals("123", player.id)
        assertEquals("Valentina", player.name)
        assertEquals(1000000, player.money)
    }
}