package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var countryName: TextView
    private lateinit var lastUpdate: TextView
    private lateinit var description: TextView
    private lateinit var temp: TextView
    private lateinit var tempLow: TextView
    private lateinit var tempHigh: TextView
    private lateinit var sunrise: TextView
    private lateinit var sunset: TextView
    private lateinit var wind: TextView
    private lateinit var pressure: TextView
    private lateinit var humidity: TextView
    private lateinit var reload: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // sure access to views
        initViewsValue()

        // first we get our API Client
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

        var zip = intent.getStringExtra("zip")
        if (zip == null)
            zip = "36925,us"
        else
            zip += ",us"


        getDataFromApi(apiInterface = apiInterface, zip = zip)

        reload.setOnClickListener {
            val intent = Intent(this, SetZipActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getDataFromApi(apiInterface: APIInterface?,zip: String){
        // enqueue gives us async functionality like coroutines, later we will replace this with coroutines
        apiInterface?.getData(zip = zip)?.enqueue(object : Callback<WeatherX> {
            override fun onResponse(call: Call<WeatherX>, response: Response<WeatherX>) {
                // we use a try block to make sure that our app doesn't crash if the data is incomplete
                try {
                    // now we have access to all weather from the JSON file, we will only use the first car in this demo (index value 0)
                    val data = response.body()!!
                    setDataIntoViews(
                        countryName = data.name,
                        lastUpdate = data.dt.toString(),
                        description = data.weather[0].description,
                        temp = data.main.temp,
                        tempLow = data.main.temp_min,
                        tempHigh = data.main.temp_max,
                        sunrise = data.sys.sunrise.toString(),
                        sunset = data.sys.sunset.toString(),
                        wind = data.wind.speed.toString(),
                        pressure = data.main.pressure.toString(),
                        humidity = data.main.humidity.toString(),
                    )
                } catch (e: Exception) {
                    Log.d("MAIN", "ISSUE: $e")
                }
            }

            override fun onFailure(call: Call<WeatherX>, t: Throwable) {
                Log.d("MAIN", "Unable to get data")
            }

        })

    }

    private fun initViewsValue() {
        countryName = findViewById(R.id.tv_country_name)
        lastUpdate = findViewById(R.id.tv_last_update)
        description = findViewById(R.id.tv_description)
        temp = findViewById(R.id.tv_temp)
        tempLow = findViewById(R.id.tv_temp_min)
        tempHigh = findViewById(R.id.tv_temp_max)
        sunrise = findViewById(R.id.tv_sunrise)
        sunset = findViewById(R.id.tv_sunset)
        wind = findViewById(R.id.tv_wind)
        pressure = findViewById(R.id.tv_pressure)
        humidity = findViewById(R.id.tv_humidity)
        reload = findViewById(R.id.ll_refresh)
    }

    @SuppressLint("SetTextI18n")
    private fun setDataIntoViews(
        countryName: String,
        lastUpdate: String,
        description: String,
        temp: Double,
        tempLow: Double,
        tempHigh: Double,
        sunrise: String,
        sunset: String,
        wind: String,
        pressure: String,
        humidity: String
    ) {
        this.countryName.text = countryName
        this.lastUpdate.text = getDateTime(lastUpdate)
        this.description.text = description
        this.temp.text = "${(temp - 273.15).toInt()}°C"
        this.tempLow.text = "${(tempLow - 273.15).toInt()}°C"
        this.tempHigh.text = "${(tempHigh - 273.15).toInt()}°C"
        this.sunrise.text = "${getSunTime(sunrise)} AM"
        this.sunset.text = "${getSunTime(sunset)} PM"
        this.wind.text = wind
        this.pressure.text = pressure
        this.humidity.text = humidity
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun getSunTime(s: String): String? {
        return try {
            val netDate = Date(s.toLong() * 1000)
            "${24 - netDate.hours}:${netDate.minutes}"
        } catch (e: Exception) {
            e.toString()
        }
    }
}