package com.example.venueapp.ui

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProviders
import com.example.venueapp.R
import com.example.venueapp.utils.Constants.CATEGORY_ID
import com.example.venueapp.utils.Constants.CLIENT_ID
import com.example.venueapp.utils.Constants.CLIENT_SECRET
import com.example.venueapp.utils.Constants.DEFAULT_ZOOM
import com.example.venueapp.utils.Constants.ERROR_DIALOG_REQUEST
import com.example.venueapp.utils.Constants.INTENT
import com.example.venueapp.utils.Constants.LIMIT
import com.example.venueapp.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.example.venueapp.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.example.venueapp.utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS
import com.example.venueapp.utils.Constants.RADIUS
import com.example.venueapp.utils.Constants.VERSION
import com.example.venueapp.viewmodels.MainViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnCameraIdleListener {

    private var locationPermissionGranted: Boolean = false
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var address: String = ""
    private lateinit var geocoder: Geocoder
    private var currentLocationList: MutableList<LatLng> = ArrayList()
    private var markersList: MutableList<MarkerOptions> = ArrayList()
    private var isCameraIdle = false
    private var isCameraIdleHandler = Handler()


    //ui components
    private lateinit var textViewAddress: TextView

    private lateinit var mainViewModel: MainViewModel

//    val latLng = LatLng(38.026506, 23.752280)
//    val latLngStr = latLng.latitude.toString() + "," + latLng.longitude.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        getLocationPermission()

        mainViewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        //subscribeObservers(latLngStr, CATEGORY_ID, RADIUS, INTENT, LIMIT, CLIENT_ID, CLIENT_SECRET, VERSION)
    }


    private fun subscribeObservers(
        latlng: String,
        categoryId: String,
        radius: Int,
        intent: String,
        limit: Int,
        clientId: String,
        client_secret: String,
        v: String
    ) {
        mainViewModel.searchByCategory(
            latlng,
            categoryId,
            radius,
            intent,
            limit,
            clientId,
            client_secret,
            v
        ).observe(this, androidx.lifecycle.Observer {
            if (it.isNotEmpty()) {
               markersList.clear()
                map.clear()
                Log.d(TAG, "markersList clear: $markersList")
                for (i in it.indices) {
                    Log.d(TAG, "$i: ${it[i]}\n")

                    val responseLatLng: LatLng = LatLng(it[i].location.lat, it[i].location.lng)
                    Log.d(TAG, "currentLatLng: $responseLatLng")

                    val marker = MarkerOptions()
                        .position(responseLatLng)
                        .title(it[i].name)
                    map.addMarker(marker)

                    markersList.add(marker)

                }
                Log.d(TAG, "markersList: $markersList")
                //    currentLocationList.add(latlng)
            } else if (it.isNullOrEmpty()) {
                Log.d(TAG, "An empty or null list was returned.")
            }
        })


    }

    override fun onCameraIdle() {
        Log.d(TAG, "onCameraIdle: called")
        val center: LatLng = map.cameraPosition.target
        Log.d(TAG, "onCameraIdle: center: $center")


        val latLngStr = center.latitude.toString() + "," + center.longitude.toString()

        //TODO needs improvement
        for (i in currentLocationList.indices){
            if (center != currentLocationList[0]){
                subscribeObservers(latLngStr, CATEGORY_ID, RADIUS, INTENT, LIMIT, CLIENT_ID, CLIENT_SECRET, VERSION)
            }
        }
        currentLocationList.add(center)
        Log.d(TAG, "onCameraIdle: currentLocationList: $currentLocationList")




}



//    fun getNewPosition(latLng: LatLng){
//        currentLocationList.add(latLng)
//        map.isMyLocationEnabled = true
//    }


    override fun onMapReady(googleMap: GoogleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show()
        map = googleMap

        if (isPermissionGranted()) {
            getDeviceLocation()
            setListeners()

            map.isMyLocationEnabled = true
            map.apply {
                uiSettings.isMyLocationButtonEnabled = true
                uiSettings.isZoomControlsEnabled = true
            }
        }
    }


    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: called")
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                initMap()
            }
        }
    }

    /**
     *  Gets the SupportMapFragment and request notification when map is ready to be used
     */
    private fun initMap() {
        Log.d(TAG, "initMap: called")
        val mapFragment: SupportMapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Returns a String address with reverse geocoding
     *
     */
    private fun getAddressString(lat: Double, long: Double): String {
        geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addressList: List<Address>? = geocoder.getFromLocation(lat, long, 1)
            addressList?.let { list ->
                val currentAddress: Address = list[0]
                val strCurrentAddress: StringBuilder = StringBuilder("")

                for (i in 0..currentAddress.maxAddressLineIndex) {
                    strCurrentAddress.append(currentAddress.getAddressLine(i))
                }
                address = strCurrentAddress.toString()
                Log.d(TAG, "My location: $address")
            }?.run {
                Log.d(TAG, "My location: No address found.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, e.message!!)
        }
        return address
    }

    /**
     *  Gets the current device location
     */
    private fun getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting current location")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            if (isPermissionGranted()) {
                val location = fusedLocationClient.lastLocation
                location.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentLocation = task.result as Location
                        Log.d(TAG, "onComplete: found current location: $currentLocation")

                        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                        moveCamera(latLng, DEFAULT_ZOOM, getString(R.string.my_location))

                        getAddressString(currentLocation.latitude, currentLocation.longitude)
                        textViewAddress.visibility = View.VISIBLE
                        textViewAddress.text = getAddressString(currentLocation.latitude, currentLocation.longitude)

//                        currentLocationList.add(LatLng(currentLocation.latitude, currentLocation.longitude))
//                        Log.d(TAG, "current position list: $currentLocationList")

                    } else {
                        Log.d(TAG, "onComplete: current location not found")
                        Toast.makeText(
                            this,
                            "unable to get current location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                isCameraIdle = true
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "getDeviceLocation: SecurityException: ${e.message!!}")
        }

    }

    /**
     *  Moves the camera to the current location
     */
    private fun moveCamera(latLng: LatLng, zoom: Float, title: String) {
        Log.d(
            TAG,
            "moveCamera: moving camera to: lat: ${latLng.latitude} , long: ${latLng.longitude}"
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))

        if (title != getString(R.string.my_location)) {
            val options = MarkerOptions()
                .position(latLng)
                .title(title)
            map.addMarker(options)

        }
    }

    /**
     *  Returns true if permission is granted
     */
    private fun isPermissionGranted(): Boolean {
        Log.d(TAG, "isPermissionGranted: called")
        return (ContextCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    /**
     *  Alert dialog when GPS is not enabled.
     */

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(this)

        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val enableGpsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS)
            }
        val alert = builder.create()
        alert.show()
    }

    /**
     *  Returns true is GPS is enabled.
     */
    private fun isGpsEnabled(): Boolean {

        Log.d(TAG, "checking gps")

        val manager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "gps is not enabled")
            buildAlertMessageNoGps()
            return false
        }
        Log.d(TAG, "gps is enabled")
        return true
    }

    /**
     *  Location permission for getting the location of the device.
     */
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            initMap()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     *  Makes sure that google services is installed on the device
     */
    private fun isServicesOK(): Boolean {

        Log.d(TAG, "isServicesOK: checking google services version")

        val available: Int =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@MainActivity)

        when {
            available == ConnectionResult.SUCCESS -> {
                Log.d(TAG, "isServicesOK: Google Play Services is working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                Log.d(TAG, "isServicesOK: an error occurred")

                val dialog: Dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(this@MainActivity, available, ERROR_DIALOG_REQUEST)
                dialog.show()
            }
            else -> {
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    private fun checkMap(): Boolean {
        if (isServicesOK()) {
            if (isGpsEnabled()) {
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()

        if (checkMap()) {
            if (!locationPermissionGranted) {
                getLocationPermission()
            }

        }
    }

    private fun initComponents() {
        textViewAddress = findViewById(R.id.textview_address)
    }

    private fun setListeners() {
        map.setOnCameraIdleListener(this)
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}
