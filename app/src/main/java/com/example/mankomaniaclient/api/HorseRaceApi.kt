package com.example.mankomaniaclient.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HorseRaceApi {
    @GET("horses")
    suspend fun getHorses(): List<Horse>

    @POST("select")
    suspend fun selectHorse(@Body request: HorseSelectionRequest): Response<Unit>

    @GET("race/result")
    suspend fun getRaceResult(): RaceResult
}
