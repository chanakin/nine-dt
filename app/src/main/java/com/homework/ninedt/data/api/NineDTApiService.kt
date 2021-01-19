package com.homework.ninedt.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Path

//class NetworkService {
//    private val retrofit =
//    val nineDTMovesService: NineDTApiService =
//        retrofit.create(NineDTApiService::class.java)
//
//    suspend fun getNextMove(moves: Array<Int>): Array<Int> = withContext(Dispatchers.Default) {
//        nineDTMovesService.getNextMove(moves)
//    }
//
//    companion object {
//
//    }
//}

interface NineDTApiService {
    @GET("/production/{moves}")
    suspend fun getNextMove(@Path("moves") moves: Array<Int>): Array<Int>
}
