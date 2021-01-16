package com.homework.ninedt.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NineDTMovesService {
    @GET("/production/{moves}")
    suspend fun getPlayer2Move(@Path("moves") moves: Array<Int>): Call<Array<Int>>
}
