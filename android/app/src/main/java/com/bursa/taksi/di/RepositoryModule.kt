package com.bursa.taksi.di

import com.bursa.taksi.repository.MainRepository
import com.bursa.taksi.retrofit.StationRetrofit
import com.bursa.taksi.room.StationDao
import com.bursa.taksi.util.Preferences
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        stationRetrofit: StationRetrofit,
        stationDao: StationDao,
        placesClient: PlacesClient,
        preferences: Preferences
    ): MainRepository = MainRepository(
        stationRetrofit,
        stationDao,
        placesClient,
        preferences
    )
}