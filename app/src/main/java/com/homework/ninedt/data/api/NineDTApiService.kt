package com.homework.ninedt.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface NineDTApiService {
    @GET("/production")
    suspend fun getNextMove(@Query("moves", encoded = true) moves: String): Array<Int>
}
