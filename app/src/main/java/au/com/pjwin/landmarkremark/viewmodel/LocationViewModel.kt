package au.com.pjwin.landmarkremark.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import au.com.pjwin.commonlib.util.Pref
import au.com.pjwin.commonlib.util.Util
import au.com.pjwin.commonlib.util.get
import au.com.pjwin.landmarkremark.util.PrefKey
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {

    internal val locationData = MutableLiveData<LatLng?>()

    private var locationPermissionGranted: Boolean = Pref.SHARED_PREF[PrefKey.HAS_LOCATION_PERM]
            ?: false

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(Util.context())
    }

    internal fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener {
                            locationData.value = LatLng(it.latitude, it.longitude)
                        }
                        .addOnFailureListener {
                            clearLocation()
                        }
            }

        } catch (e: SecurityException) {
            clearLocation()
        }
    }

    internal fun clearLocation() {
        locationData.value = null
    }
}