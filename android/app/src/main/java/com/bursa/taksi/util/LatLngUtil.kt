package com.bursa.taksi.util

import com.bursa.taksi.model.Station
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs

fun getNearestStationIdByLatLng(userLatLng: LatLng, stations: List<Station>): Int {
    val userLat = userLatLng.latitude
    val userLong = userLatLng.longitude

    var nearestStation = stations[0]

    for (station in stations) {
        if (abs(userLat - station.latitude) < abs(userLat - nearestStation.latitude) &&
            abs(userLong - station.longitude) < abs(userLong - nearestStation.longitude)
        ) nearestStation = station
    }

    return nearestStation.uid
}