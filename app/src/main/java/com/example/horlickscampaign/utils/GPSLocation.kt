package com.example.horlickscampaign.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

open class GPSLocation(var context: Context) {
    var locationListener: LocationChangedListener? = null
    var presentLat = ""
    var presentLon = ""
    var presentAcc = ""
    var locationManager: LocationManager? = null
    var listener: GPSLocationListener? = null
    var previousBestLocation: Location? = null

    interface LocationChangedListener {
        fun locationChangeCallback(lat: String?, lon: String?, acc: String?)
    }

    fun setLocationChangedListener(locationChangedListener: LocationChangedListener?) {
        this.locationListener = locationChangedListener
    }

    fun GPS_Start() {
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            listener = GPSLocationListener()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                4000,
                0f,
                listener!!
            )
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                4000,
                0f,
                listener!!
            )
        } catch (ex: Exception) {
        }
    }

    protected fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }

        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val TWO_MINUTES = 1000 * 60
        val isSignificantlyNewer = timeDelta > TWO_MINUTES
        val isSignificantlyOlder = timeDelta < -TWO_MINUTES
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(
            location.provider,
            currentBestLocation.provider
        )

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }

    inner class GPSLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.i("**********", "Location changed")
            if (isBetterLocation(location, previousBestLocation)) {
                location.accuracy
                //location.setText(" " + loc.getAccuracy());
                presentLat = location.latitude.toString()
                presentLon = location.longitude.toString()
                presentAcc = location.accuracy.toString()
                locationListener!!.locationChangeCallback(presentLat, presentLon, presentAcc)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Toast.makeText(context, "Gps Disabled", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {
           // Toast.makeText(context, "Gps Enabled", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderDisabled(provider: String) {}
    }
}