package com.example.a7314ice1

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a7314ice1.models.DailyForecast
import com.example.a7314ice1.models.DailyForecastsResponse
import com.example.a7314ice1.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var forecastContainer: LinearLayout
    private val apiKey = "yWMJWb3NQpbex33HvYel0vmQYEpRknOg"
    private val locationKey = "305605" // Cape Town location key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        forecastContainer = findViewById(R.id.forecastContainer)

        getWeatherForecast()
    }

    private fun getWeatherForecast() {
        val call = RetrofitClient.instance.getFiveDayForecast(locationKey, apiKey, true)
        call.enqueue(object : Callback<DailyForecastsResponse> {
            override fun onResponse(
                call: Call<DailyForecastsResponse>,
                response: Response<DailyForecastsResponse>
            ) {
                if (response.isSuccessful) {
                    val forecastList = response.body()?.DailyForecasts ?: return
                    displayForecasts(forecastList)
                } else {
                    showToast("Failed to load forecast.")
                }
            }

            override fun onFailure(call: Call<DailyForecastsResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
            }
        })
    }

    private fun displayForecasts(forecastList: List<DailyForecast>) {
        forecastContainer.removeAllViews()
        for (forecast in forecastList) {
            val view = TextView(this)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val date = forecast.Date.substring(0, 10)
            val min = forecast.Temperature.Minimum
            val max = forecast.Temperature.Maximum
            view.text = "$date\nMin: ${min.Value}${min.Unit}, Max: ${max.Value}${max.Unit}"
            view.textSize = 18f
            view.setPadding(8, 16, 8, 16)
            forecastContainer.addView(view)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }
}
