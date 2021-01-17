package com.homework.ninedt.data.api

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NineDTMovesService {
    @GET("/production/{moves}")
    fun getPlayer2Move(@Path("moves") moves: Array<Int>): LiveData<ApiResponse<Array<Int>>>
}
