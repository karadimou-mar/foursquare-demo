package com.example.venueapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.venueapp.api.RetrofitBuilder
import com.example.venueapp.models.main.ApiGeneralResponse
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

    fun searchByCategory(
        latLng: String, categoryId: String,
        radius: Int, intent: String, limit: Int, clientId: String, client_secret: String, v: String
    ): LiveData<List<Venue>> {

//        val lat = latLng.latitude
//        val long = latLng.longitude

        RetrofitBuilder.apiService.searchByCategory(
            latLng,
            categoryId,
            radius,
            intent,
            limit,
            clientId,
            client_secret,
            v
        ).enqueue(
            object : Callback<ApiGeneralResponse> {
                override fun onFailure(call: Call<ApiGeneralResponse>, t: Throwable) {
                    //TODO("Not yet implemented")
                    Log.e(TAG, "onFailure: ${t.message!!}")
                }

                override fun onResponse(
                    call: Call<ApiGeneralResponse>,
                    response: Response<ApiGeneralResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Log.d(TAG, "onResponse: ${response.raw().request().url()}")

                            response.body()?.let {
//                               val name = it.response.venues[0].name
//                               Log.d(TAG, "cafe name that was found: $name")

                                val list: List<Venue> = response.body()!!.response.venues
                                for (i in list.indices) {
                                    Log.d(TAG, "name $i: ${list[i].name}")
                                }
                                searchResult.postValue(list)
                            }


                        } else {
                            onFailure(
                                call,
                                Throwable("Unsuccessful search for specific category: ${response.code()}")
                            )
                        }
                    }

                }
            }
        )
        return searchResult
    }

    /**
     *  Gets a list of venues names.
     *  Returns an empty list when no results.
     *
     */
    private fun getResults(apiGeneralResponse: ApiGeneralResponse): MutableList<Venue> {

        apiGeneralResponse?.let {
            val venuesList: MutableList<Venue> = ArrayList()
            it.response.venues.forEach {
                venuesList.add(it)
            }
            for (i in 0 until venuesList.size) {
                Log.d(TAG, venuesList[i].name)
            }
            return venuesList
        }
    }
}