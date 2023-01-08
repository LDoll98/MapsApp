package de.hhn.ws2022.ai.mapsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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

    private lateinit var spinner: Spinner
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

        spinner = findViewById(R.id.spinner)
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

    override fun onClick(p0: View?) {}

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.d("LocationActivity", " Selected item: ${p0!!.adapter.getItem(p2)}")
        when (p0.adapter.getItem(p2)) {
            "New York" -> fillInPlace("New York", "40.730610", "-73.935242")
            "Sydney" -> fillInPlace("Sydney", "-33.865143", "151.209900")
            "London" -> fillInPlace("London", "51.509865", "-0.118092")
            "Clear" -> clearInputFields()
        }
    }

    private fun clearInputFields() {
        firstPlaceCity.text.clear()
        firstPlaceLatitude.text.clear()
        firstPlaceLongitude.text.clear()

        secondPlaceCity.text.clear()
        secondPlaceLatitude.text.clear()
        secondPlaceLongitude.text.clear()
    }

    private fun fillInPlace(cityName: String, latitude: String, longitude: String) {
        if (firstPlaceCity.text.isEmpty() && firstPlaceLatitude.text.isEmpty()
            && firstPlaceLongitude.text.isEmpty()) {
            if(cityName != secondPlaceCity.text.toString()) {
                firstPlaceCity.setText(cityName)
                firstPlaceLatitude.setText(latitude)
                firstPlaceLongitude.setText(longitude)
            } else {
                Toast.makeText(
                    this, "You have that city already place pick another one", Toast.LENGTH_LONG).show()
            }
        }
        else if (secondPlaceCity.text.isEmpty() && secondPlaceLatitude.text.isEmpty()
            && secondPlaceLongitude.text.isEmpty()) {
            if(cityName != firstPlaceCity.text.toString()) {
                secondPlaceCity.setText(cityName)
                secondPlaceLatitude.setText(latitude)
                secondPlaceLongitude.setText(longitude)
            } else {
                Toast.makeText(
                    this, "You have that city already place pick another one", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(
                this, "There is no free input field", Toast.LENGTH_LONG).show()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}