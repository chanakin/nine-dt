package com.homework.ninedt.data.api

import retrofit2.Retrofit

object ApiManager {

    private const val BASE_URL = "https://w0ayb2ph1k.execute-api.us-west-2.amazonaws.com"

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
    }

    val nineDTMovesService: NineDTMovesService =
        getRetrofit().create(NineDTMovesService::class.java)
}