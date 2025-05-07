package com.example.mankomaniaclient.api

import com.example.mankomaniaclient.model.GameRules

interface GameRulesApi {
    suspend fun getGameRules(): Result<GameRules>
}