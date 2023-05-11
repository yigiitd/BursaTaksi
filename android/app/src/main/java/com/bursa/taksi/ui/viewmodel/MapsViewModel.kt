package com.bursa.taksi.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bursa.taksi.model.Station
import com.bursa.taksi.model.location.LocationPrediction
import com.bursa.taksi.repository.MainRepository
import com.bursa.taksi.util.DataState
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MapsViewModel
@Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {
    private val _stationDataState: MutableLiveData<DataState<List<Station>>> = MutableLiveData()
    val stationDataState: LiveData<DataState<List<Station>>>
        get() = _stationDataState

    private val _destinationDataState: MutableLiveData<DataState<Task<FetchPlaceResponse>>> = MutableLiveData()
    val destinationDataState: LiveData<DataState<Task<FetchPlaceResponse>>>
        get() = _destinationDataState

    fun getStations() {
        viewModelScope.launch {
            mainRepository.getStations().onEach { dataState -> _stationDataState.value = dataState }
                .launchIn(viewModelScope)
        }
    }

    fun getLocationByPlaceId(placeId: String, fields: List<Place.Field>) {
        viewModelScope.launch {
            mainRepository.getLocationByPlaceId(placeId, fields)
                .onEach { dataState -> _destinationDataState.value = dataState }
                .launchIn(viewModelScope)
        }
    }
}