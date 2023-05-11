package com.bursa.taksi.di

import android.content.Context
import androidx.room.Room
import com.bursa.taksi.room.StationDao
import com.bursa.taksi.room.StationDatabase
import com.bursa.taksi.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideStationDatabase(@ApplicationContext context: Context): StationDatabase =
        Room.databaseBuilder(
                context,
                StationDatabase::class.java,
                Constants.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideStationDao(stationDatabase: StationDatabase): StationDao =
        stationDatabase.stationDao()
}