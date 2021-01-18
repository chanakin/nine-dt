package com.homework.ninedt.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

class NetworkService {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .build()

    val nineDTMovesService: NineDTMovesService =
        retrofit.create(NineDTMovesService::class.java)

    suspend fun getNextMove(moves: Array<Int>): Array<Int> = withContext(Dispatchers.Default) {
        nineDTMovesService.getNextMove(moves)
    }

    companion object {
        private const val BASE_URL = "https://w0ayb2ph1k.execute-api.us-west-2.amazonaws.com"
    }
}

interface NineDTMovesService {
    @GET("/production/{moves}")
    suspend fun getNextMove(@Path("moves") moves: Array<Int>): Array<Int>
}
