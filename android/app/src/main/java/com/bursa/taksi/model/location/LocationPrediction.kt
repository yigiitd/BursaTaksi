package com.bursa.taksi.model.location

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationPrediction (
    val placeId: String,
    val fullText: String,
    val primaryText: String,
    val secondaryText: String
    ): Parcelable