package com.bursa.taksi.room

import androidx.room.*
import com.bursa.taksi.model.Station

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(vararg stations: Station)

    @Query("SELECT * FROM stations")
    suspend fun getStations(): List<Station>
}