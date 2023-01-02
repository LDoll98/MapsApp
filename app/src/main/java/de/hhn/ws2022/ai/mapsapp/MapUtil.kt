package de.hhn.ws2022.ai.mapsapp

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class MapUtil {

    companion object {
        fun calculateDistance(
            startLocation: LatLng, endLocation: LatLng,
            startName: String, endName: String
        ): Float {
            val startPoint = Location(startName)
            startPoint.latitude = startLocation.latitude
            startPoint.longitude = startLocation.longitude

            val endPoint = Location(endName)
            endPoint.latitude = endLocation.latitude
            endPoint.longitude = endLocation.longitude

            return startPoint.distanceTo(endPoint) / 1000
        }

        fun checkIfLatLongValid(latitudeString: String, longitudeString: String): Boolean {
            var latitude = 0.0
            var longitude = 0.0
            try {
                latitude = latitudeString.toDouble()
                longitude = longitudeString.toDouble()
            } catch (nfe: NumberFormatException) {
                Log.e("MapUtilValidLatLng", nfe.message.toString(), nfe)
                return false
            }
            return (latitude >= -90.0 && latitude <= 90
                    && longitude >= -180 && longitude <= 180)
        }
    }
}