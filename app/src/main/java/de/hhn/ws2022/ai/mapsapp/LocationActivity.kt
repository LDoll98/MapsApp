package de.hhn.ws2022.ai.mapsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog

class LocationActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var firstPlaceCity: EditText
    private lateinit var firstPlaceLatitude: EditText
    private lateinit var firstPlaceLongitude: EditText
    private lateinit var secondPlaceCity: EditText
    private lateinit var secondPlaceLatitude: EditText
    private lateinit var secondPlaceLongitude: EditText

    private lateinit var startMapButton: Button
    private lateinit var showMyLocationButton: Button

    private val permissionCode = 101
    private var permissionWasDenied = false

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

        permissionWasDenied = intent.getBooleanExtra("permissionWasDenied", false)

        val spinner: Spinner = findViewById(R.id.spinner)
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(this, R.array.city_names,
            android.R.layout.simple_spinner_dropdown_item).also { adapterItem ->
            adapterItem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapterItem
        }
    }

    override fun onResume() {
        super.onResume()

        val showMyLocationFlag = false

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


            if (firstPlaceLatLngIsValid && secondPlaceLatLngIsValid) {

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
            }
        }
    }

    override fun onClick(p0: View?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}