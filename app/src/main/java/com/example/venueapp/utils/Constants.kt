package com.example.venueapp.utils

import com.example.venueapp.BuildConfig

object Constants {

    const val ERROR_DIALOG_REQUEST = 9001
    const val PERMISSIONS_REQUEST_ENABLE_GPS = 9002
    const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003

    const val LOCATION_PERMISSION_REQUEST_CODE = 1234
    const val DEFAULT_ZOOM = 15f

    const val BASE_URL = "https://api.foursquare.com/v2/"
    const val CLIENT_ID = BuildConfig.CLIENT_ID
    const val CLIENT_SECRET = BuildConfig.CLIENT_SECRET
    const val VERSION = "20200526"
    const val COMMON_PARAMS = "?client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&v=$VERSION"
    const val LIMIT = 20
    const val INTENT = "browse"
    const val CATEGORY_ID = "4bf58dd8d48988d16d941735"
    const val RADIUS = 2000

    const val PARAM_INTENT = "details"

}