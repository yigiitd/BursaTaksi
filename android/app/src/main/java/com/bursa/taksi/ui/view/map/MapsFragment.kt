package com.bursa.taksi.ui.view.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bursa.taksi.R
import com.bursa.taksi.databinding.FragmentMapsBinding
import com.bursa.taksi.model.TaxiCallModel
import com.bursa.taksi.ui.viewmodel.MapsViewModel
import com.bursa.taksi.util.Constants.BURSA_LAT_LONG
import com.bursa.taksi.util.Constants.PLACE_FIELDS
import com.bursa.taksi.util.DataState
import com.bursa.taksi.util.getNearestStationIdByLatLng
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MapsFragment : Fragment() {
    private lateinit var fusedLocationService: FusedLocationProviderClient

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<MapsFragmentArgs>()
    private val viewModel by viewModels<MapsViewModel>()

    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private var isMapReady: Boolean = false

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private var destination: Place? = null
    private var lastLocation: Location? = null

    private val locationPermissionRequest =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    updateMarkers()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    makeToastMessage(R.string.warning_access_fine_location_permission)
                }
                else -> {}
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        try {
            MapsInitializer.initialize(requireContext().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync { mMap ->
            try {
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.style_json
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }

            googleMap = mMap
            googleMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        BURSA_LAT_LONG, 10f
                    )
                )
            )
            isMapReady = true
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapsBinding.bind(view)
        initialize()

        val argsDestination = args.destination

        binding.apply {
            buttonUpdateLocation.setOnClickListener {
                if (lastLocation != null) {
                    updateMarkers()
                } else {
                    makeToastMessage(R.string.error_location)
                    getLastLocation()
                }
            }

            buttonSettingsMenu.setOnClickListener {
                val action =
                    MapsFragmentDirections.actionMapsFragmentToSettingsFragment()
                findNavController().navigate(action)
            }

            destinationText.setOnClickListener {
                val action = MapsFragmentDirections.actionMapsFragmentToSearchPlaceFragment()
                findNavController().navigate(action)
            }

            buttonCallTaxi.setOnClickListener {
                callTaxi()
            }
        }

        subscribeObservers()
        requestLocationPermission()

        viewModel.getStations()

        argsDestination?.let {
            viewModel.getLocationByPlaceId(
                it.placeId,
                PLACE_FIELDS
            )
        }
    }

    private fun initialize() {
        fusedLocationService = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (auth.currentUser == null) {
            makeToastMessage(R.string.error_general)
        }

        getLastLocation()
    }

    private fun subscribeObservers() {
        viewModel.stationDataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {}
                is DataState.Error -> {
                    makeToastMessage(R.string.error_taxi_stations)
                    viewModel.getStations()
                    println(dataState.exception)
                }
                is DataState.Loading -> {}
            }
        }

        viewModel.destinationDataState.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    dataState.data.addOnSuccessListener { data ->
                        if (data.place.name != null && data.place.address != null && data.place.id != null && data.place.latLng != null) {
                            binding.destinationText.setText(data.place.name)
                            destination = data.place
                            updateMarkers()
                        }
                    }.addOnFailureListener {
                        it.printStackTrace()
                    }
                }

                is DataState.Error -> {
                    makeToastMessage(R.string.error_place_info)
                    println(dataState.exception)
                }
                is DataState.Loading -> {}
            }
        }
    }

    private fun updateMarkers() {
        if (isMapReady) {
            googleMap.clear()
        } else return

        if (lastLocation != null) {
            val latlng = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
            googleMap.addMarker(
                MarkerOptions().position(latlng)
                    .title(getString(R.string.text_your_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 11f))
        } else getLastLocation()

        destination?.let {
            if (it.latLng != null) {
                googleMap.addMarker(
                    MarkerOptions().position(it.latLng!!)
                        .title(getString(R.string.text_destination))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                )
            }
        }
    }

    private fun callTaxi() {
        if (auth.currentUser != null) {
            val user = auth.currentUser!!
            val stationsDataState = viewModel.stationDataState.value

            if (destination == null) {
                makeToastMessage(R.string.warning_select_place)
                return
            }

            if (stationsDataState is DataState.Success) {
                if (lastLocation != null) {
                        val location = lastLocation!!
                        val destination = destination!!
                        val time = Timestamp(Calendar.getInstance().time)

                        val stations = stationsDataState.data
                        val nearestStation = getNearestStationIdByLatLng(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ), stations
                        )

                        if (destination.id != null && destination.address != null && destination.latLng != null && destination.name != null) {
                            val taxiCallModel =
                                TaxiCallModel(
                                    destination.id!!,
                                    destination.address!!,
                                    destination.latLng!!.latitude,
                                    destination.latLng!!.longitude,
                                    destination.name!!,
                                    time,
                                    user.uid,
                                    user.displayName,
                                    user.phoneNumber,
                                    location.latitude,
                                    location.longitude,
                                    nearestStation
                                )

                            db.collection("taxi_calls").add(taxiCallModel).addOnSuccessListener {
                                makeToastMessage(R.string.text_taxi_call_successfull)
                                updateMarkers()
                            }.addOnFailureListener {
                                it.printStackTrace()
                                makeToastMessage(R.string.error_taxi_call)
                            }
                        } else {
                            makeToastMessage(R.string.error_place_info)
                            return
                        }
                } else {
                    makeToastMessage(R.string.error_location)
                    getLastLocation()
                    return
                }
            } else {
                makeToastMessage(R.string.error_taxi_stations)
                viewModel.getStations()
                return
            }
        } else {
            makeToastMessage(R.string.error_general)
            return
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            val cts = CancellationTokenSource()
            fusedLocationService.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cts.token
            ).addOnSuccessListener {
                lastLocation = it
            }.addOnFailureListener {
                lastLocation = null
            }
        }
    }

    private fun requestLocationPermission() {
        when {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                updateMarkers()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.text_location_permission))
                    .setMessage(getString(R.string.text_location_permission_rationale))
                    .setNegativeButton(getString(R.string.no)) { _, _ -> }
                    .setPositiveButton(getString(R.string.give)) { _, _ ->
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    }
                    .show()
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun makeToastMessage(resId: Int) {
        Toast.makeText(requireContext(), getString(resId), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        if (this::mapView.isInitialized) {
            mapView.onStart()
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::mapView.isInitialized) {
            mapView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::mapView.isInitialized) {
            mapView.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (this::mapView.isInitialized) {
            mapView.onStop()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (this::mapView.isInitialized) {
            mapView.onLowMemory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mapView.isInitialized) {
            mapView.onDestroy()
        }
    }
}