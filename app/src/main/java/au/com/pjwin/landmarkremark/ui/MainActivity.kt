package au.com.pjwin.landmarkremark.ui

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import au.com.pjwin.commonlib.ui.BaseActivity
import au.com.pjwin.commonlib.viewmodel.VoidViewModel
import au.com.pjwin.landmarkremark.R
import au.com.pjwin.landmarkremark.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


class MainActivity : BaseActivity<Void, VoidViewModel, ActivityMainBinding>(), OnMapReadyCallback {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val DEFAULT_ZOOM = 12.0f
        private val TAG = MainActivity::class.java.simpleName

        // Used to load the 'native-lib' library on application startup.
        /*init {
            System.loadLibrary("native-lib")
        }*/
    }

    protected var locationPermissionGranted = false

    private lateinit var geoDataClient: GeoDataClient
    private lateinit var placeDetectionClient: PlaceDetectionClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var googleMap: GoogleMap? = null
    private val mDefaultLocation = LatLng(-33.852, 151.211)
    private var lastKnownLocation: Location? = null

    private var MAP_ZOOM_MAX: Float? = null
    private var MAP_ZOOM_MIN: Float? = null

    override fun layoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        geoDataClient = Places.getGeoDataClient(applicationContext)
        placeDetectionClient = Places.getPlaceDetectionClient(applicationContext)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        if (savedInstanceState == null) {
            val mapFragment = SupportMapFragment.newInstance()
            showFragment(R.id.map_container, mapFragment)
            mapFragment.getMapAsync(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        updateMapUI()
        getDeviceLocation()
    }

    private fun updateMapUI() {
        googleMap?.let {
            try {
                if (locationPermissionGranted) {
                    it.isMyLocationEnabled = true
                    it.uiSettings.isMyLocationButtonEnabled = true

                } else {
                    it.isMyLocationEnabled = false
                    it.uiSettings.isMyLocationButtonEnabled = false
                    lastKnownLocation = null
                    getLocationPermission()
                }

            } catch (e: SecurityException) {
                Log.e(TAG, "Failed to request permission", e)
            }
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            updateMapUI()

        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //val latLng = LatLng(-33.852, 151.211)
        this.googleMap = googleMap
        /*googleMap.addMarker(MarkerOptions().position(latLng)
                .title("Sydney marker"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))*/
        MAP_ZOOM_MAX = googleMap.maxZoomLevel
        MAP_ZOOM_MIN = googleMap.minZoomLevel

        updateMapUI()
        getDeviceLocation()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //external fun stringFromJNI(): String

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            lastKnownLocation = location
                            if (lastKnownLocation != null) {
                                lastKnownLocation?.let {
                                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            LatLng(it.latitude, it.longitude), DEFAULT_ZOOM))
                                }

                            } else {
                                moveToDefault()
                            }
                        }
                        .addOnFailureListener {
                            moveToDefault()
                        }
            }

        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to request permission", e)
        }
    }

    private fun moveToDefault() {
        googleMap?.let {
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))
            it.uiSettings.isMyLocationButtonEnabled = false
        }
    }
}
