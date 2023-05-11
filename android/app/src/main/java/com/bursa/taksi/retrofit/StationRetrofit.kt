package com.bursa.taksi.retrofit

import com.bursa.taksi.model.Station
import retrofit2.http.GET
import retrofit2.http.Query

interface StationRetrofit {
    @GET("bursa-taksi-development.appspot.com/o/station_data.json")
    suspend fun getStations(
        @Query("alt") query: String = "media",
    ): List<Station>
}