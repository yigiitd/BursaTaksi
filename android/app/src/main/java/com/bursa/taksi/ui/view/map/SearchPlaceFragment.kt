package com.bursa.taksi.ui.view.map

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bursa.taksi.R
import com.bursa.taksi.databinding.FragmentSearchPlaceBinding
import com.bursa.taksi.model.location.LocationAdapter
import com.bursa.taksi.model.location.LocationPrediction
import com.bursa.taksi.ui.viewmodel.SearchViewModel
import com.bursa.taksi.util.DataState
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SearchPlaceFragment : BottomSheetDialogFragment(), LocationAdapter.OnItemClickListener {
    private lateinit var placesClient: PlacesClient

    private var _binding: FragmentSearchPlaceBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()
    private val adapter by lazy { LocationAdapter(this) }

    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search_place, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchPlaceBinding.bind(view)
        placesClient = Places.createClient(requireContext())

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)

            destinationText.doOnTextChanged { text, _, _, _ ->
                countDownTimer?.cancel()
                countDownTimer = null
                countDownTimer = object : CountDownTimer(1000, 1000) {
                    override fun onTick(p0: Long) {}

                    override fun onFinish() {
                        val query = text.toString()
                        if (query.isNotEmpty()) {
                            viewModel.getLocationPredictions(query)
                        }
                    }
                }.start()
            }
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.taskResponse.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    dataState.data.addOnSuccessListener { response ->
                        val data = mutableListOf<LocationPrediction>()

                        for (l in response.autocompletePredictions) {
                            val location = LocationPrediction(
                                l.placeId,
                                l.getFullText(null).toString(),
                                l.getPrimaryText(null).toString(),
                                l.getSecondaryText(null).toString()
                            )
                            data.add(location)
                        }

                        if (data.size == 0) {
                            Snackbar.make(binding.root, getString(R.string.error_search_no_result), Snackbar.LENGTH_INDEFINITE)
                                .setAction(getString(R.string.ok), null).show()
                        }

                        binding.apply {
                            recyclerView.visibility = View.VISIBLE
                            errorText.visibility = View.GONE
                            buttonRetry.visibility = View.GONE
                            progressBar.visibility = View.GONE
                        }

                        adapter.setData(data)
                    }.addOnFailureListener {
                        println(it)
                    }
                }
                is DataState.Error -> {
                    binding.apply {
                        recyclerView.visibility = View.GONE
                        errorText.visibility = View.VISIBLE
                        buttonRetry.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                }
                is DataState.Loading -> {
                    binding.apply {
                        recyclerView.visibility = View.VISIBLE
                        errorText.visibility = View.GONE
                        buttonRetry.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(location: LocationPrediction) {
        val action = SearchPlaceFragmentDirections.actionSearchPlaceFragmentToMapsFragment(location)
        findNavController().navigate(action)
    }
}