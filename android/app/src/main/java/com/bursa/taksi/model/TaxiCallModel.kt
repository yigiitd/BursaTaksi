package com.bursa.taksi.model

import com.google.firebase.Timestamp

data class TaxiCallModel(
    val destinationId: String,
    val destinationAddress: String,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val destinationName: String,

    val taxiCallDate: Timestamp,

    val userId: String,
    val userFullName: String?,
    val userPhoneNumber: String?,
    val userLatitude: Double,
    val userLongitude: Double,

    val nearestStationId: Int
)
