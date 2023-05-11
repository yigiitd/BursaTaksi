package com.bursa.taksi.di

import android.content.Context
import com.bursa.taksi.util.Constants
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlacesClientModule {

    @Singleton
    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        Places.initialize(context, Constants.MAPS_API_KEY)
        return Places.createClient(context)
    }
}