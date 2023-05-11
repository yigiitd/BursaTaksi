package com.bursa.taksi.util

import com.bursa.taksi.model.menuitem.MenuItem
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds

object Constants {
    const val FIREBASE_BASE_URL = "https://firebasestorage.googleapis.com/v0/b/"
    const val DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/"
    const val DATABASE_NAME = "station_db"
    const val MAPS_API_KEY = "AIzaSyDVTS5u9uJH-_Ytb1MgX5Z6SCh0cT5M_Ik"

    val BURSA_LAT_LONG = LatLng(40.1955733, 29.058930)
    val BURSA_BOUNDS: RectangularBounds = RectangularBounds.newInstance(
        LatLng(39.8602172, 28.394164),
        LatLng(40.0567201, 29.066140)
    )

    val PLACE_FIELDS = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)

    val SETTINGS_MENU_ITEMS = listOf(
        MenuItem(MenuItem.MenuItemTypes.Account),
        MenuItem(MenuItem.MenuItemTypes.Options)
    )
}