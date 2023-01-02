package de.hhn.ws2022.ai.mapsapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class LocationActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var firstPlaceCity: EditText
    private lateinit var firstPlaceLatitude: EditText
    private lateinit var firstPlaceLongitude: EditText
    private lateinit var secondPlaceCity: EditText
    private lateinit var secondPlaceLatitude: EditText
    private lateinit var secondPlaceLongitude: EditText
    private lateinit var startMapButton: Button
    private lateinit var showMyLocationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        firstPlaceCity = findViewById(R.id.firstPlaceEditText)
        firstPlaceLatitude = findViewById(R.id.firstPlaceLatitudeEditText)
        firstPlaceLongitude = findViewById(R.id.firstPlaceLongitudeEditText)
        secondPlaceCity = findViewById(R.id.secondPlaceEditText)
        secondPlaceLatitude = findViewById(R.id.secondPlaceLatitudeEditText)
        secondPlaceLongitude = findViewById(R.id.secondPlaceLongitudeEditText)

        startMapButton = findViewById(R.id.startMapButton)
        showMyLocationButton = findViewById(R.id.showMyLocationButton)
    }

    override fun onResume() {
        super.onResume()

        // Use cases
        // input fields are empty -> Toast: Please type in latitude and longitude for first and second place when using start map button
        // comma separated digits -> Toast: Please separate with dot
        // check if lat/lng is land
        //

        // Add On
        // Add more than two places -> distance have to be chooseable
        // Show current location always
        // Go back from MapsActivity to LocationActivity
        // Show distance in MapsActivity after dialog
        // Provide a list with famous places by name


        var showMyLocationFlag = false
        showMyLocationButton.setOnClickListener {
            showMyLocationFlag = true
            val intent = Intent(this, MapsActivity::class.java).apply {
                putExtra("showMyLocationFlag", showMyLocationFlag)
            }
            startActivity(intent)
        }

        startMapButton.setOnClickListener {
            val firstPlaceCityString = firstPlaceCity.text.toString()
            val secondPlaceCityString = secondPlaceCity.text.toString()

            val firstPlaceLatString = firstPlaceLatitude.text.toString()
            val firstPlaceLngString = firstPlaceLongitude.text.toString()
            val secondPlaceLatString = secondPlaceLatitude.text.toString()
            val secondPlaceLngString = secondPlaceLongitude.text.toString()

            val firstPlaceLatLngIsValid =
                MapUtil.checkIfLatLongValid(firstPlaceLatString, firstPlaceLngString)
            val secondPlaceLatLngIsValid =
                MapUtil.checkIfLatLongValid(secondPlaceLatString, secondPlaceLngString)


            if(firstPlaceLatLngIsValid && secondPlaceLatLngIsValid) {
                val intent = Intent(this, MapsActivity::class.java).apply {
                    putExtra("showMyLocationFlag", showMyLocationFlag)
                    putExtra("firstPlaceCity", firstPlaceCityString)
                    putExtra("secondPlaceCity", secondPlaceCityString)

                    putExtra("firstPlaceLatitude", firstPlaceLatString)
                    putExtra("firstPlaceLongitude", firstPlaceLngString)
                    putExtra("secondPlaceLatitude", secondPlaceLatString)
                    putExtra("secondPlaceLongitude", secondPlaceLngString)
                }
                startActivity(intent)
            } else {
                // AlertDialog
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Wrong input")
                builder.setMessage("Please type in valid digits.\nLatitude between -90.0 and 90.00\nLongitude between -180.0 and 180.0")
                builder.setPositiveButton("OK", null)
                builder.show()

                // Toast
                /*
                val message = R.string.wrongNumberFormatForLatLng
                val toast = Toast.makeText(applicationContext, "Please type in valid digits. Latitude between -90.0 and 90.00; Longitude between -180.0 and 180.0.", Toast.LENGTH_LONG)
                toast.show()

                 */
            }
        }
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}