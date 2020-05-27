package com.example.venueapp.api

import com.example.venueapp.models.main.ApiGeneralResponse
import com.example.venueapp.models.main.SearchResponse
import com.example.venueapp.models.main.Venue
import com.example.venueapp.utils.Constants.COMMON_PARAMS
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


// cafe = 4bf58dd8d48988d16d941735

interface ApiService {

    @GET("venues/search")
    fun searchByCategory(
        @Query("ll") latlng: String,
        @Query("categoryId") category: String,
        @Query("radius") radius: Int,
        @Query("intent") intent: String,
        @Query("limit") limit: Int,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("v") v: String
    ): Call<ApiGeneralResponse>
    //LiveData<GenericApiResponse<List<SearchResult>>>

}