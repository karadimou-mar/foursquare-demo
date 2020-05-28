package com.example.venueapp.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.venueapp.R
import com.example.venueapp.models.main.Venue
import com.example.venueapp.utils.Constants.CATEGORY_ID
import com.example.venueapp.utils.Constants.DEFAULT_ZOOM
import com.example.venueapp.utils.Constants.ERROR_DIALOG_REQUEST
import com.example.venueapp.utils.Constants.INTENT
import com.example.venueapp.utils.Constants.LIMIT
import com.example.venueapp.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.example.venueapp.utils.Constants.PARAM_INTENT
import com.example.venueapp.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.example.venueapp.utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS
import com.example.venueapp.utils.Constants.RADIUS
import com.example.venueapp.utils.ViewWeightAnimationWrapper
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity(), OnMapReadyCallback, View.OnClickListener,
    GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnCameraIdleListener,
    GoogleMap.OnMarkerClickListener{

    private var locationPermissionGranted: Boolean = false
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var address: String = ""
    private lateinit var geocoder: Geocoder
    private var currentLocationList: MutableList<LatLng> = ArrayList()
    private var markersList: MutableList<MarkerOptions> = ArrayList()
    private var isCameraIdle = false
    private var venueId: String = ""

    //ui components
    private lateinit var tvCurrentAddress: TextView
    private lateinit var mapContainer: ConstraintLayout
    private lateinit var searchLayout: RelativeLayout
    private lateinit var tvName: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvCategory: TextView
    private lateinit var btnReset: ImageButton
    private lateinit var image: CircleImageView

    private lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        getLocationPermission()

        //showProgressBar(true)

        mainViewModel = ViewModelProviders.of(this)[MainViewModel::class.java]

    }

    private fun subscribeObserverSearch(latlng: String, categoryId: String, radius: Int, intent: String, limit: Int) {
        mainViewModel.searchByCategory(latlng, categoryId, radius, intent, limit).observe(this, androidx.lifecycle.Observer {
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

                    venueId = it[i].id
                    Log.d(TAG, "venueId: $venueId")

                    setUpWidgets(it,i)
                }
                Log.d(TAG, "markersList: $markersList")
                //    currentLocationList.add(latlng)
            } else if (it.isNullOrEmpty()) {
                Log.d(TAG, "An empty or null list was returned.")
            }
        })
    }

    private fun subscribeObserverDetails(venueId: String){
        mainViewModel.getDetails(venueId).observe(this, androidx.lifecycle.Observer {
            if (it != null){
                    val url =
                        "${it.photos.groups[0].items[0].prefix}110x110${it.photos.groups[0].items[0].suffix}"
                Log.d(TAG, "photo url: $url")
                setUpPhoto(url)
            }else {
                Log.d(TAG, "A empty or null object wea returned.")
                setUpPhoto("${R.drawable.ic_launcher_foreground}")
            }
        })
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        if (searchLayout.visibility == View.GONE) {
            tvCurrentAddress.visibility = View.GONE
            searchLayout.visibility = View.VISIBLE
            contractMapAnimation()
            subscribeObserverDetails(venueId)
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v){
            btnReset -> {
                tvCurrentAddress.visibility = View.VISIBLE
                searchLayout.visibility = View.GONE
                expandMapAnimation()
                map.clear()
            }
            searchLayout -> {
                createIntent()
            }
        }
    }



    override fun onCameraIdle() {
        Log.d(TAG, "onCameraIdle: called")
        val center: LatLng = map.cameraPosition.target
        Log.d(TAG, "onCameraIdle: center: $center")


        val latLngStr = center.latitude.toString() + "," + center.longitude.toString()

        //TODO needs improvement

            for (i in currentLocationList.indices) {
                if (center != currentLocationList[0]) {
                    subscribeObserverSearch(latLngStr, CATEGORY_ID, RADIUS, INTENT, LIMIT)
                }
            }

        currentLocationList.add(center)
        Log.d(TAG, "onCameraIdle: currentLocationList: $currentLocationList")
}


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

    private fun createIntent(){
        Log.d(TAG, "onViewClicked: called")
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(PARAM_INTENT, venueId)
        startActivity(intent)
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
                        tvCurrentAddress.visibility = View.VISIBLE
                        tvCurrentAddress.text = getAddressString(currentLocation.latitude, currentLocation.longitude)

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

    private fun setUpWidgets(venue: List<Venue>, i: Int){
        tvName.text = venue[i].name
        tvAddress.text = venue[i].location.address
        tvCategory.text = venue[i].categories[0].name
    }

    private fun setUpPhoto(url: String){
        Picasso.get()
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            //.resize(0, image.height)
            .into(image)
    }

    private fun expandMapAnimation(){
        val mapAnimationWrapper = ViewWeightAnimationWrapper(mapContainer)
        val mapAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            mapAnimationWrapper,
            "weight",
            50f,100f
        )
        mapAnimation.duration = 800

        val searchAnimationWrapper = ViewWeightAnimationWrapper(searchLayout)
        val searchAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            searchAnimationWrapper,
            "weight",
            50f,0f
            )
        searchAnimation.duration = 800

        mapAnimation.start()
        searchAnimation.start()
    }

    private fun contractMapAnimation(){
        val mapAnimationWrapper = ViewWeightAnimationWrapper(mapContainer)
        val mapAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            mapAnimationWrapper,
            "weight",
            100f,80f
        )
        mapAnimation.duration = 800

        val searchAnimationWrapper = ViewWeightAnimationWrapper(searchLayout)
        val searchAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            searchAnimationWrapper,
            "weight",
            0f,50f
        )
        searchAnimation.duration = 800

        mapAnimation.start()
        searchAnimation.start()
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
        tvCurrentAddress = findViewById(R.id.textview_current_address)
        mapContainer = findViewById(R.id.map_container)
       searchLayout = findViewById(R.id.search_layout)
        tvName = findViewById(R.id.textview_name)
        tvAddress = findViewById(R.id.textview_address)
        tvCategory = findViewById(R.id.textview_category)
        btnReset = findViewById(R.id.btn_reset)
        image = findViewById(R.id.image)

    }

    private fun setListeners() {
        map.setOnCameraIdleListener(this)
        map.setOnMarkerClickListener(this)
        btnReset.setOnClickListener(this)
        searchLayout.setOnClickListener(this)
    }


    companion object {
        private const val TAG = "MainActivity"
    }




}
