package com.example.a7314ice1.network


import com.example.a7314ice1.models.DailyForecastsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    @GET("forecasts/v1/daily/5day/{locationKey}")
    fun getFiveDayForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("metric") metric: Boolean = true

    ): Call<DailyForecastsResponse>
}
