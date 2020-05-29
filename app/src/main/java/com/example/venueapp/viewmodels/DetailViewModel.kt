package com.example.venueapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.venueapp.api.RetrofitBuilder
import com.example.venueapp.models.ApiGeneralResponse
import com.example.venueapp.models.details.DetailResponse
import com.example.venueapp.models.details.DetailVenue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    companion object {
        const val TAG = "DetailViewModel"
    }

    private var detailsResult: MutableLiveData<DetailVenue> = MutableLiveData()

    fun getDetails(venueId: String): LiveData<DetailVenue> {
        RetrofitBuilder.apiService.getDetails(venueId).enqueue(
            object : Callback<ApiGeneralResponse<DetailResponse>> {
                override fun onFailure(
                    call: Call<ApiGeneralResponse<DetailResponse>>,
                    t: Throwable
                ) {
                    Log.e(MainViewModel.TAG, "getDetails: onFailure: ${t.message!!}")
                }

                override fun onResponse(
                    call: Call<ApiGeneralResponse<DetailResponse>>,
                    response: Response<ApiGeneralResponse<DetailResponse>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Log.d(
                                MainViewModel.TAG,
                                "getDetails: onResponse: ${response.raw().request().url()}"
                            )

                            response.body()!!.response.venue.let {
                                detailsResult.postValue(it)

                            }
                        }
                    } else {
                        onFailure(
                            call,
                            Throwable("Unsuccessful search for details: ${response.code()}")
                        )
                    }
                }
            }
        )
        return detailsResult
    }

}