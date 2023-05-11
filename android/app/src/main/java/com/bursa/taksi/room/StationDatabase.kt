package com.bursa.taksi.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bursa.taksi.model.Station

@Database(entities = [Station::class], version = 1)
abstract class StationDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
}