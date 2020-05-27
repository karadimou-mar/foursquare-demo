package com.example.venueapp.api

import com.example.venueapp.models.ApiGeneralResponse
import com.example.venueapp.models.details.DetailResponse
import com.example.venueapp.models.main.SearchResponse
import com.example.venueapp.utils.Constants.COMMON_PARAMS
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


// cafe = 4bf58dd8d48988d16d941735

interface ApiService {

    @GET("venues/search${COMMON_PARAMS}")
    fun searchByCategory(
        @Query("ll") latlng: String,
        @Query("categoryId") category: String,
        @Query("radius") radius: Int,
        @Query("intent") intent: String,
        @Query("limit") limit: Int
    ): Call<ApiGeneralResponse<SearchResponse>>
    //LiveData<GenericApiResponse<List<SearchResult>>>

    @GET("venues/{VENUE_ID}/${COMMON_PARAMS}")
    fun getDetails(
        @Path("VENUE_ID") venueId: String
    ): Call<ApiGeneralResponse<DetailResponse>>

}