package de.hhn.ws2022.ai.mapsapp

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
    private lateinit var currentLatLng: LatLng
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

        firstPlaceName = intent.getStringExtra("firstPlaceCity").toString()
        secondPlaceName = intent.getStringExtra("secondPlaceCity").toString()

        firstPlaceLatitude = intent.getStringExtra("firstPlaceLatitude").toString()
        firstPlaceLongitude = intent.getStringExtra("firstPlaceLongitude").toString()
        secondPlaceLatitude = intent.getStringExtra("secondPlaceLatitude").toString()
        secondPlaceLongitude = intent.getStringExtra("secondPlaceLongitude").toString()


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

        val firstLocationLatitude = firstPlaceLatitude.toDouble()
        val firstLocationLongitude = firstPlaceLongitude.toDouble()
        val secondLocationLatitude = secondPlaceLatitude.toDouble()
        val secondLocationLongitude = secondPlaceLongitude.toDouble()

        addMarker(mMap, firstLocationLatitude, firstLocationLongitude, firstPlaceName)
        addMarker(mMap, secondLocationLatitude, secondLocationLongitude, secondPlaceName)

        val latLngFirstLocation = LatLng(firstLocationLatitude, firstLocationLongitude)
        val latLngSecondLocation = LatLng(secondLocationLatitude, secondLocationLongitude)
        fetchLocationAndShowDistanceDialog(latLngFirstLocation, latLngSecondLocation)

    }


    private fun fetchLocationAndShowDistanceDialog(firstLatLng: LatLng, secondLatLng: LatLng) {
        var locationArray = arrayOf(firstLatLng, secondLatLng)

        if (!checkLocationPermission()) {
            Log.d(TAG, "Permissions for location are denied")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )

            Toast.makeText(
                this,
                getString(R.string.no_permission_for_location_dialog_message),
                Toast.LENGTH_LONG
            ).show()
        }
        mMap.isMyLocationEnabled = true
        val task = fusedLocationProvideClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Log.d(
                    TAG, "Current location: Latitude = ${currentLocation.latitude}, " +
                            "Longitude = ${currentLocation.longitude}"
                )
                addMarker(mMap, location.latitude, location.longitude, "$currentLatLng")
                locationArray += LatLng(location.latitude, location.longitude)

                val message = getString(R.string.current_location_latitude_dialog_message) +
                        "${currentLocation.latitude}" +
                        getString(R.string.current_location_longitude_dialog_message) +
                        "${currentLocation.longitude}"

                val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
                toast.show()

                createAndShowDialog(
                    "Which distance you want to kow? \n Choose distance between",
                    locationArray
                )
                zoomToMarker(mMap, locationArray)
            } else {
                Log.d(TAG, "No current location found!")

                Toast.makeText(
                    this,
                    getString(R.string.no_current_location_found_dialog_message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun createAndShowDialog(title: String, latLngArr: Array<LatLng>) {
        val distanceChoice =
            arrayOf("First and second place", "First and my place", "Second and my place")
        val selectedItems = mutableListOf(*distanceChoice)
        val distanceCheckedItem = BooleanArray(3)
        distanceCheckedItem[0] = true
        distanceCheckedItem[1] = false
        distanceCheckedItem[2] = false

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        var map = mutableMapOf<String, Boolean>(
            distanceChoice[0] to distanceCheckedItem[0],
            distanceChoice[1] to distanceCheckedItem[1],
            distanceChoice[2] to distanceCheckedItem[2]
        )
        builder.setMultiChoiceItems(distanceChoice, distanceCheckedItem) { _, which, isChecked ->
            map[distanceChoice[which]] = isChecked
        }

        var dialogStringMessage = ""
        builder.setPositiveButton("OK") { _, _ ->
            dialogStringMessage = createStringMessage(map, latLngArr)

            // create dialog
            val distanceDialog = AlertDialog.Builder(this)
            distanceDialog.setTitle("Distances")
            distanceDialog.setMessage(dialogStringMessage)
            distanceDialog.setPositiveButton("SHOW MAP", null)

            distanceDialog.create().show()
        }

        builder.create().show()
    }

    private fun createStringMessage(map: MutableMap<String, Boolean>, latLngArr: Array<LatLng>): String {
        val message = StringBuilder()

        if(latLngArr.size == 3) {
            Log.d(TAG, "3 locations are found")
            if (map.keys.contains("First and my place") && map.getValue("First and my place")) {
                addDistanceMessage(message, firstPlaceName, "your location",
                    latLngArr[0], latLngArr[2])
            }
            if (map.keys.contains("Second and my place") && map.getValue("Second and my place")) {
                addDistanceMessage(message, secondPlaceName, "your location",
                    latLngArr[1], latLngArr[2])
            }
            if (map.keys.contains("First and second place") && map.getValue("First and second place")) {
                addDistanceMessage(message, secondPlaceName, secondPlaceName,
                    latLngArr[0], latLngArr[1])
            }
        } else if (latLngArr.size == 2) {
            if (map.keys.contains("First and second place") && map.getValue("First and second place")) {
                addDistanceMessage(message, secondPlaceName, secondPlaceName,
                    latLngArr[0], latLngArr[1])
            }
            // make toast that current location not found
        }
        return message.toString()
    }

    private fun addDistanceMessage(
        message: StringBuilder,
        firstPlaceString: String, secondPlaceString: String,
        firstPlaceLatLng: LatLng, secondPlaceLatLng: LatLng
    ) {
        val distanceFirstSecondPlace = MapUtil.calculateDistance(
            firstPlaceLatLng,secondPlaceLatLng, firstPlaceString, secondPlaceString
        )
        message.append(
            buildMessage(distanceFirstSecondPlace, firstPlaceString, secondPlaceString)
        ).append("\n")
        Log.d(TAG, "Distance from First to my place = $distanceFirstSecondPlace")
    }

    private fun buildMessage(distance: Float, firstDistancePlace: String, secondDistancePlace: String): String {
        return buildString {
            append(getString(R.string.dialog_message_part_1)).append(" ")
                .append(firstDistancePlace).append(" ")
                .append(getString(R.string.dialog_message_and)).append(" ")
                .append(secondDistancePlace).append(" ")
                .append(getString(R.string.dialog_message_is)).append(" ")
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
        for (location in locations) {
            polyOptions.add(location)
        }

        mMap.addPolyline(polyOptions)
    }

    private fun addMarker(mMap: GoogleMap, lat: Double, lng: Double, name: String) {
        val location = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(location).title(name))
    }


}