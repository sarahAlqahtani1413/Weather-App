package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.*

interface APIInterface {
    @GET("weather")
    fun getData(
        @Query("zip") zip : String = "36925,us",
        @Query("appid") appid : String = "7cd2660e5eda282dc691a89c94141b61"
    ): Call<WeatherX>
}