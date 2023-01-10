package com.example.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetZipActivity : AppCompatActivity() {

    private lateinit var zip : EditText
    private lateinit var submit : Button
    private lateinit var error: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_zip)

        zip = findViewById(R.id.et_zip_code)
        submit = findViewById(R.id.btn_submit)
        error = findViewById(R.id.tv_error_zip_msg)

        // first we get our API Client
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        val intent = Intent(this, MainActivity::class.java)

        submit.setOnClickListener {
            val code = zip.text.toString()
            if (code != ""){
                apiInterface?.getData(zip = code)?.enqueue(object : Callback<WeatherX> {
                    override fun onResponse(call: Call<WeatherX>, response: Response<WeatherX>) {
                        // we use a try block to make sure that our app doesn't crash if the data is incomplete
                        try {
                            // now we have access to all weather from the JSON file, we will only use the first car in this demo (index value 0)
                            if (response.isSuccessful){
                                intent.putExtra("zip",code)
                                startActivity(intent)
                                error.visibility = View.GONE
                                finish()
                            }else{
                                error.visibility = View.VISIBLE
                            }

                        } catch (e: Exception) {
                            Log.d("MAIN", "ISSUE: $e")
                        }
                    }
                    override fun onFailure(call: Call<WeatherX>, t: Throwable) {
                        Log.d("MAIN", "Unable to get data")
                        error.visibility = View.VISIBLE
                    }

                })
            }else{
                Toast.makeText(this, "Please fill the zip !!", Toast.LENGTH_SHORT).show()
            }

            }

    }
}