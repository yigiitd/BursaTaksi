package com.bursa.taksi.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsRetrofit {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("destination") destination: String,
        @Query("origin") origin: String
    )
}