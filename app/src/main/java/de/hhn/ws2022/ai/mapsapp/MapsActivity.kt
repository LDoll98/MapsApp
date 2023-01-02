package de.hhn.ws2022.ai.mapsapp

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import de.hhn.ws2022.ai.mapsapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentLocation: Location
    private val permissionCode = 101
    private lateinit var fusedLocationProvideClient: FusedLocationProviderClient

    private var showMyLocationFlag = false
    private lateinit var firstPlaceName: String
    private lateinit var secondPlaceName: String

    private lateinit var firstPlaceLatitude: String
    private lateinit var firstPlaceLongitude: String
    private lateinit var secondPlaceLatitude: String
    private lateinit var secondPlaceLongitude: String

    private var TAG = "MapsActivity Location Permission"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showMyLocationFlag = intent.getBooleanExtra("showMyLocationFlag", false)
        if (!showMyLocationFlag) {
            firstPlaceName = intent.getStringExtra("firstPlaceCity").toString()
            secondPlaceName = intent.getStringExtra("secondPlaceCity").toString()

            firstPlaceLatitude = intent.getStringExtra("firstPlaceLatitude").toString()
            firstPlaceLongitude = intent.getStringExtra("firstPlaceLongitude").toString()
            secondPlaceLatitude = intent.getStringExtra("secondPlaceLatitude").toString()
            secondPlaceLongitude = intent.getStringExtra("secondPlaceLongitude").toString()
        }

        fusedLocationProvideClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (showMyLocationFlag) {
            // show current location
            fetchLocation()
        } else {
            // Add marker
            val firstLocationLatitude = firstPlaceLatitude.toDouble()
            val firstLocationLongitude = firstPlaceLongitude.toDouble()
            val secondLocationLatitude = secondPlaceLatitude.toDouble()
            val secondLocationLongitude = secondPlaceLongitude.toDouble()

            addMarker(mMap, firstLocationLatitude, firstLocationLongitude, firstPlaceName)
            addMarker(mMap, secondLocationLatitude, secondLocationLongitude, secondPlaceName)

            // Calculate distance
            val latLngFirstLocation = LatLng(firstLocationLatitude, firstLocationLongitude)
            val latLngSecondLocation = LatLng(secondLocationLatitude, secondLocationLongitude)

            val distance =
                MapUtil.calculateDistance(latLngFirstLocation, latLngSecondLocation,
                    firstPlaceName, secondPlaceName)

            // Zoom to BOTH Marker
            zoomToMarker(mMap, arrayOf(latLngFirstLocation, latLngSecondLocation))

            // Show dialog with distance
            showDistanceDialog(distance)
        }
    }


    private fun fetchLocation() {
        if (checkLocationPermission()) {
            Log.d(TAG, "Permissions for location are denied")
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            // fetchLocation()
            return
        }
        val task = fusedLocationProvideClient.lastLocation
        task.addOnSuccessListener {
            location ->
            if(location != null) {
                Log.d(TAG, "Current location: Latitude = ${currentLocation.latitude}, " +
                            "Longitude = ${currentLocation.longitude}")
                currentLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("$currentLatLng"))

                zoomToMarker(mMap, arrayOf(currentLatLng))
            } else {
                Log.d(TAG, "No current location found!")
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun showDistanceDialog(distance: Float) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Distance")
        builder.setMessage(buildMessage(distance))
        builder.setPositiveButton("OK", null)

        builder.show()
    }

    private fun buildMessage(distance: Float): String {
        return buildString { append(getString(R.string.dialogMessagePart1)).append(" ")
                .append(firstPlaceName).append(" ")
                .append(getString(R.string.dialogMessageAnd)).append(" ")
                .append(secondPlaceName).append(" ")
                .append(getString(R.string.dialogMessageIs)).append(" ")
                .append(distance.toString())
                .append(" km")
        }
    }

    private fun zoomToMarker(mMap: GoogleMap, locations: Array<LatLng>) {
        val zoomBuilder = LatLngBounds.Builder()
        for (location in locations) {
            zoomBuilder.include(location)
        }
        val bounds = zoomBuilder.build()
        val padding = 150 // offset from edges of the map in pixels
        val zoom = CameraUpdateFactory.newLatLngBounds(bounds, padding)

        createPolyline(locations)
        mMap.animateCamera(zoom)
    }

    private fun createPolyline(locations: Array<LatLng>) {
        val pattern = listOf(
            Dot(), Gap(10F), Dash(20F), Gap(10F)
        )
        val polyOptions = PolylineOptions()
            .width(2.0F)
            .color(Color.BLACK)
            .pattern(pattern)
        for(location in locations) {
            polyOptions.add(location)
        }

        mMap.addPolyline(polyOptions)
    }

    /*
    private fun zoomToMarker(mMap: GoogleMap, latLngFirstLocation: LatLng, latLngSecondLocation: LatLng, locations: Array<LatLng>) {
        val zoomBuilder = LatLngBounds.Builder()
        zoomBuilder.include(latLngFirstLocation)
        zoomBuilder.include(latLngSecondLocation)

        val bounds = zoomBuilder.build()
        val padding = 150 // offset from edges of the map in pixels
        val zoom = CameraUpdateFactory.newLatLngBounds(bounds, padding)

        val pattern = listOf(
            Dot(), Gap(10F), Dash(20F), Gap(10F)
        )
        val line = mMap.addPolyline(PolylineOptions()
            .add(latLngFirstLocation)
            .add(latLngSecondLocation)
            .width(2.0F)
            .color(Color.BLACK)
            .pattern(pattern))

        mMap.animateCamera(zoom)
    }

     */

    private fun addMarker(mMap: GoogleMap, lat: Double, lng: Double, name: String) {
        val location = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(location).title(name))
    }


}