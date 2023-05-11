package com.bursa.taksi.di

import com.bursa.taksi.retrofit.StationRetrofit
import com.bursa.taksi.util.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson =
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(Constants.FIREBASE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))

    @Singleton
    @Provides
    fun provideStationService(retrofit: Retrofit.Builder): StationRetrofit =
        retrofit.build()
            .create(StationRetrofit::class.java)
}