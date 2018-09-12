package au.com.pjwin.landmarkremark.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import au.com.pjwin.commonlib.ui.BaseActivity
import au.com.pjwin.commonlib.util.Pref
import au.com.pjwin.commonlib.util.Util
import au.com.pjwin.commonlib.util.get
import au.com.pjwin.landmarkremark.BuildConfig
import au.com.pjwin.landmarkremark.R
import au.com.pjwin.landmarkremark.databinding.ActivityMainBinding
import au.com.pjwin.landmarkremark.model.Note
import au.com.pjwin.landmarkremark.model.NoteResponse
import au.com.pjwin.landmarkremark.util.PrefKey
import au.com.pjwin.landmarkremark.viewmodel.LocationViewModel
import au.com.pjwin.landmarkremark.viewmodel.NoteViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
private const val DEFAULT_ZOOM = 12.0f

class MainActivity : BaseActivity<NoteResponse, NoteViewModel, ActivityMainBinding>(), OnMapReadyCallback {

    private val DEFAULT_LOCATION = LatLng(-33.852, 151.211)
    private var MAP_ZOOM_MAX: Float? = null
    private var MAP_ZOOM_MIN: Float? = null

    private var locationPermissionGranted: Boolean = Pref.SHARED_PREF[PrefKey.HAS_LOCATION_PERM]
            ?: false

    private var googleMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mapViewModel: LocationViewModel

    override fun layoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMapViewMode()

        if (savedInstanceState == null) {
            loadingInline(true)
            mapFragment = SupportMapFragment.newInstance()
            showFragment(R.id.map_container, mapFragment)
            mapFragment.getMapAsync(this)
        }
    }

    private fun setupMapViewMode() {
        mapViewModel = ViewModelProviders.of(this)[LocationViewModel::class.java]
        mapViewModel.locationData.observe(this, Observer { updateLocation(it) })
    }

    override fun onData(data: NoteResponse?) {
        data?.let { resp ->
            val markList = resp.noteList

            //todo show different color base on user
            //var colourHue = 0f
            markList?.forEach {
                it.userId
                val latLng = LatLng(it.lat.toDouble(), it.lng.toDouble())
                val marker = googleMap?.addMarker(MarkerOptions().position(latLng)
                        .title(it.description)
                        //.icon(BitmapDescriptorFactory.defaultMarker(colourHue))
                )
                marker?.tag = it.id
                //colourHue += 10
            }

            googleMap?.setOnMarkerClickListener { it ->
                if (BuildConfig.DEBUG) {
                    Snackbar.make(mapFragment.view!!, "the id is $it.tag", Snackbar.LENGTH_SHORT).show()
                }
                //todo update note
                //initSaveDialog(it.position)
                false
            }
        }
    }

    private fun addMarker(noteId: String, desc: String, latLng: LatLng) {
        val marker = googleMap?.addMarker(MarkerOptions().position(latLng)
                .title(desc)
        )
        marker?.tag = noteId
    }

    override fun onError(throwable: Throwable?) {
        super.onError(throwable)

        //todo error handling in DataView
        Snackbar.make(mapFragment.view!!, "Error loading data", Snackbar.LENGTH_SHORT).show()
        Log.e(TAG, "Error loading data", throwable)
    }

    private fun updateMapUI() {
        googleMap?.let {
            try {
                if (locationPermissionGranted) {
                    enableMyLocation(true)
                    it.setOnMapClickListener { point ->
                        initSaveDialog(point)
                    }

                } else {
                    enableMyLocation(false)
                    mapViewModel.clearLocation()
                    getLocationPermission()
                }

            } catch (e: SecurityException) {
                Log.e(TAG, "Failed to request permission", e)
            }
        }
    }

    @Throws(SecurityException::class)
    private fun enableMyLocation(enabled: Boolean) {
        googleMap?.let {
            it.isMyLocationEnabled = enabled
            it.uiSettings.isMyLocationButtonEnabled = enabled
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            Pref[PrefKey.HAS_LOCATION_PERM] = locationPermissionGranted
            updateMapUI()

        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        Pref[PrefKey.HAS_LOCATION_PERM] = locationPermissionGranted

        updateMapUI()
        mapViewModel.getDeviceLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        loadingInline(false)
        this.googleMap = googleMap

        MAP_ZOOM_MAX = googleMap.maxZoomLevel
        MAP_ZOOM_MIN = googleMap.minZoomLevel

        relocateMyLocationButton()
        updateMapUI()
        mapViewModel.getDeviceLocation()
    }

    /**
     * a hack to reposition the my location button
     */
    private fun relocateMyLocationButton() {
        mapFragment.view?.let {
            val locationButton = (it.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
            val layoutParams = locationButton.layoutParams as (RelativeLayout.LayoutParams)
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, 30)
        }
    }

    private fun initSaveDialog(point: LatLng) {
        showBasicInputDialog(
                R.string.note_save_dialog_title,
                { input ->
                    saveNote(input, point)
                }
        )
    }

    private fun saveNote(desc: String, point: LatLng) {
        viewModel.saveNote(desc, point)
    }

    private fun updateLocation(latLng: LatLng?) {
        googleMap?.let {
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng
                    ?: DEFAULT_LOCATION, DEFAULT_ZOOM))

            if (latLng == null) {
                enableMyLocation(false)
            }
        }

        viewModel.getNotesByUser()
    }

    private fun addMarkers() {
        val markList = mutableListOf(
                Note("1", "note 1 user 1", "1", "-33.852", "151.211"),
                Note("2", "note 2 user 2", "2", "-33.863", "151.222"),
                Note("3", "note 3 user 2", "2", "-33.873", "151.232"),
                Note("4", "note 4 user 1", "1", "-33.880", "151.240"))

        var colourHue = 0f
        markList.forEach {
            val latLng = LatLng(it.lat.toDouble(), it.lng.toDouble())
            val marker = googleMap?.addMarker(MarkerOptions().position(latLng)
                    .title(it.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(colourHue))
            )
            marker?.tag = it.id
            colourHue += 10
        }

        googleMap?.setOnMarkerClickListener { it ->
            if (BuildConfig.DEBUG) {
                Toast.makeText(Util.context(), "the id is " + it.tag, Toast.LENGTH_SHORT).show()
            }
            initSaveDialog(it.position)
            false
        }
    }
}
