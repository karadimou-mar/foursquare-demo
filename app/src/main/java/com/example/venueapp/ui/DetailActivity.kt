package com.example.venueapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.venueapp.R
import com.example.venueapp.models.details.DetailVenue
import com.example.venueapp.utils.Constants.PARAM_INTENT
import com.example.venueapp.utils.Constants.PERMISSIONS_REQUEST_PHONE_CALL
import com.example.venueapp.viewmodels.DetailViewModel

class DetailActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val TAG = "DetailActivity"
    }

    // Ui components
    private lateinit var layout: ConstraintLayout
    private lateinit var detailName: TextView
    private lateinit var detailRating: TextView
    private lateinit var detailReview: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var detailHours: TextView

    //private lateinit var detailCategory: TextView
    private lateinit var detailAddress: TextView
    private lateinit var detailDescription: TextView
    private lateinit var website: TextView
    private lateinit var phone: TextView

    private lateinit var detailViewModel: DetailViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initComponents()
        setListeners()
        showProgressBar(true)
        detailViewModel = ViewModelProviders.of(this)[DetailViewModel::class.java]
        getIncomingIntent()
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra(PARAM_INTENT)) {
            val venueId: String = intent.getStringExtra(PARAM_INTENT)!!
            Log.d(TAG, "getIncomingIntent: $venueId")
            subscribeObservers(venueId)

        }
    }

    private fun subscribeObservers(venueId: String) {
        detailViewModel.getDetails(venueId).observe(this, Observer {
            it?.let {
                showProgressBar(false)
                setLayoutVisibility()
                Log.d(TAG, "getDetails for $venueId: called")
                setUpWidgets(it)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            phone -> {
                makePhoneCall(phone.text.toString())
            }
        }
    }

    private fun setUpWidgets(venue: DetailVenue?) {
        detailName.text = if (venue?.name.isNullOrEmpty()) "No name is available." else venue?.name
        detailRating.text = if (venue?.rating.toString()
                .isNullOrEmpty()
        ) "No rating is available" else venue?.rating.toString()
        detailReview.text = """(${venue?.ratingSignals.toString()})"""
        ratingBar.rating =
            if (venue?.rating.toString().isNullOrEmpty()) 0f else ((venue?.rating?.times(5)
                ?.div(10))!!.toFloat())
        detailDescription.text =
            if (venue?.description.isNullOrEmpty()) "No description is available." else venue?.description
        detailAddress.text =
            if (venue?.location?.address.isNullOrEmpty()) "No address is available." else venue?.location?.address
        detailHours.text =
            if (venue?.hours?.status.isNullOrEmpty()) "No working hours are available." else venue?.hours?.status
        website.text =
            if (venue?.canonicalUrl.isNullOrEmpty()) "No website is available." else venue?.canonicalUrl
        phone.text = setUpPhone(venue)
    }

    private fun setUpPhone(venue: DetailVenue?): String {
        val contact = venue?.contact
        return contact?.formattedPhone ?: "No phone number is available."
    }

    private fun setLayoutVisibility() {
        if (layout.visibility == View.GONE) {
            layout.visibility = View.VISIBLE
        } else {
            layout.visibility = View.VISIBLE
        }
    }

    private fun initComponents() {
        detailName = findViewById(R.id.detail_name)
        detailRating = findViewById(R.id.detail_rating)
        detailReview = findViewById(R.id.detail_reviews)
        ratingBar = findViewById(R.id.details_rating_bar)
        detailHours = findViewById(R.id.open_hour)
        detailAddress = findViewById(R.id.detail_address)
        detailDescription = findViewById(R.id.detail_description)
        website = findViewById(R.id.detail_web)
        phone = findViewById(R.id.detail_phone)
        layout = findViewById(R.id.detail_layout)

    }

    /**
     *  Make a phone call through an intent.
     */
    private fun makePhoneCall(phoneNumber: String) {
        if (phoneNumber != "No phone number is available.") {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            checkForPermission()
            startActivity(intent)
        }
    }

    /**
     *  Checking if CALL_PHONE permission is granted
     *  If it is granted -> perform call
     *  If it is not granted -> ask for permission
     */
    private fun checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CALL_PHONE
                )
            ) {
                //Todo show an explanation
            }
            else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CALL_PHONE),
                    PERMISSIONS_REQUEST_PHONE_CALL
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_PHONE_CALL) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ) {
                makePhoneCall(phone.text.toString())
            }

        } else {
            phone.isEnabled = false
        }
    }


    private fun setListeners() {
        phone.setOnClickListener(this)
    }

}