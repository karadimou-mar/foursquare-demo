package com.example.venueapp.api

import com.example.venueapp.models.ApiGeneralResponse
import com.example.venueapp.models.details.DetailResponse
import com.example.venueapp.models.main.SearchResponse
import com.example.venueapp.utils.Constants.CATEGORY_ID
import com.example.venueapp.utils.Constants.COMMON_PARAMS
import com.example.venueapp.utils.Constants.INTENT
import com.example.venueapp.utils.Constants.LIMIT
import com.example.venueapp.utils.Constants.RADIUS
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    /**
     * GET request for cafes within 2000km
     * The result is limited up to 20 counts
     *
     * @param latlng
     * @return
     */
    @GET("venues/search?limit=$LIMIT&categoryId=$CATEGORY_ID&radius=$RADIUS&intent=$INTENT${COMMON_PARAMS}")
    fun searchByCategory(
        @Query("ll") latlng: String
    ): Call<ApiGeneralResponse<SearchResponse>>

    /**
     * GET request for details using specific id
     *
     * @param venueId
     * @return
     */
    @GET("venues/{VENUE_ID}/?${COMMON_PARAMS}")
    fun getDetails(
        @Path("VENUE_ID") venueId: String
    ): Call<ApiGeneralResponse<DetailResponse>>

}