package com.bursa.taksi.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bursa.taksi.model.location.LocationPrediction
import com.bursa.taksi.repository.MainRepository
import com.bursa.taksi.util.DataState
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SearchViewModel
@Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {
    private val _taskResponse: MutableLiveData<DataState<Task<FindAutocompletePredictionsResponse>>> =
        MutableLiveData()
    val taskResponse: LiveData<DataState<Task<FindAutocompletePredictionsResponse>>>
        get() = _taskResponse

    fun getLocationPredictions(query: String) {
        viewModelScope.launch {
            mainRepository.getAutocompleteLocationPredictions(query)
                .onEach { dataState -> _taskResponse.value = dataState }
                .launchIn(viewModelScope)
        }
    }
}