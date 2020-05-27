package com.example.venueapp.models.main

import com.example.venueapp.models.Meta

data class ApiGeneralResponse(
    val meta: Meta,
    val response: SearchResponse
)