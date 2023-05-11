package com.bursa.taksi.repository

import com.bursa.taksi.model.Station
import com.bursa.taksi.retrofit.StationRetrofit
import com.bursa.taksi.room.StationDao
import com.bursa.taksi.util.Constants
import com.bursa.taksi.util.Constants.BURSA_BOUNDS
import com.bursa.taksi.util.DataState
import com.bursa.taksi.util.Preferences
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class MainRepository
constructor(
    private val stationRetrofit: StationRetrofit,
    private val stationDao: StationDao,
    private val placesClient: PlacesClient,
    private val preferences: Preferences
) {
    suspend fun getStations(): Flow<DataState<List<Station>>> = flow {
        emit(DataState.Loading)
        try {
            val isSaved = preferences.getIsSaved()
            if (isSaved != null) {
                if (isSaved) {
                    val stations = stationDao.getStations()
                    emit(DataState.Success(stations))
                } else {
                    val stations = stationRetrofit.getStations()
                    saveToDatabase(stations)
                    emit(DataState.Success(stations))
                }
            } else {
                val stations = stationRetrofit.getStations()
                saveToDatabase(stations)
                emit(DataState.Success(stations))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    suspend fun getAutocompleteLocationPredictions(query: String): Flow<DataState<Task<FindAutocompletePredictionsResponse>>> =
        flow {
            emit(DataState.Loading)
            try {
                val token = AutocompleteSessionToken.newInstance()
                val request =
                    FindAutocompletePredictionsRequest.builder()
                        .setLocationBias(BURSA_BOUNDS)
                        .setOrigin(Constants.BURSA_LAT_LONG)
                        .setCountries("TR")
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(query)
                        .build()

                val taskResponse = placesClient.findAutocompletePredictions(request)
                emit(DataState.Success(taskResponse))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }

    suspend fun getLocationByPlaceId(placeId: String, placeFields: List<Place.Field>): Flow<DataState<Task<FetchPlaceResponse>>> = flow {
        emit(DataState.Loading)
        try {
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            val taskResponse = placesClient.fetchPlace(request)
            emit(DataState.Success(taskResponse))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    private suspend fun saveToDatabase(stations: List<Station>) {
        try {
            stationDao.insertStations(*stations.toTypedArray())
            preferences.saveIsSaved()
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
    }
}