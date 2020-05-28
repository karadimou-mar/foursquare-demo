package com.example.venueapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.venueapp.api.RetrofitBuilder
import com.example.venueapp.models.ApiGeneralResponse
import com.example.venueapp.models.details.DetailResponse
import com.example.venueapp.models.details.DetailVenue
import com.example.venueapp.models.main.SearchResponse
import com.example.venueapp.models.main.Venue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel : ViewModel() {

    companion object {
        const val TAG = "MainViewModel"
    }

    private var searchResult: MutableLiveData<List<Venue>> = MutableLiveData()
    private var detailsResult: MutableLiveData<DetailVenue> = MutableLiveData()

    fun searchByCategory(latLng: String): LiveData<List<Venue>> {

        RetrofitBuilder.apiService.searchByCategory(latLng).enqueue(
            object : Callback<ApiGeneralResponse<SearchResponse>> {
                override fun onFailure(call: Call<ApiGeneralResponse<SearchResponse>>, t: Throwable) {
                    //TODO("Not yet implemented")
                    Log.e(TAG, "searchByCategory: onFailure: ${t.message!!}")
                }
                override fun onResponse(
                    call: Call<ApiGeneralResponse<SearchResponse>>,
                    response: Response<ApiGeneralResponse<SearchResponse>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Log.d(TAG, "searchByCategory: onResponse: ${response.raw().request().url()}")

                            response.body()?.let {
                                val list: List<Venue> = response.body()!!.response.venues
                                for (i in list.indices) {
                                    Log.d(TAG, "name $i: ${list[i].name}")
                                }
                                searchResult.postValue(list)
                            }
                        }
                    }else {
                        onFailure(
                            call,
                            Throwable("Unsuccessful search for specific category: ${response.code()}")
                        )
                    }
                }
            }
        )
        return searchResult
    }

    fun getDetails(venueId: String) : LiveData<DetailVenue>{
        RetrofitBuilder.apiService.getDetails(venueId).enqueue(
            object : Callback<ApiGeneralResponse<DetailResponse>>{
                override fun onFailure(call: Call<ApiGeneralResponse<DetailResponse>>, t: Throwable) {
                    //TODO("Not yet implemented")
                    Log.e(TAG, "getDetails: onFailure: ${t.message!!}")
                }

                override fun onResponse(
                    call: Call<ApiGeneralResponse<DetailResponse>>,
                    response: Response<ApiGeneralResponse<DetailResponse>>
                ) {
                    if (response.isSuccessful){
                        if (response.body() != null){
                            Log.d(TAG, "getDetails: onResponse: ${response.raw().request().url()}")

                            response.body()!!.response.venue.let {
                                detailsResult.postValue(it)
                            }
                        }
                    }else{
                        onFailure(call, Throwable("Unsuccessful search for details: ${response.code()}"))
                    }
                }
            }
        )
        return detailsResult
    }



}