package com.example.a7314ice1

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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

    /* --- LOAD LOGO ICON INTO imageViewLogo USING GLIDE ---
     val iconCode = forecastList[0].Day.Icon.toString().padStart(2, '0')
    val logoUrl = "https://developer.accuweather.com/sites/default/files/${iconCode}-s.png"

    val imageViewLogo = findViewById<ImageView>(R.id.imageViewLogo)
    Glide.with(this)
        .load(logoUrl)
        .into(imageViewLogo)
    */

    // --- DISPLAY FORECAST CARDS ---
    for (forecast in forecastList) {
        val dayLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
            setPadding(24, 24, 24, 24)
            setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
        }

        val date = forecast.Date.substring(0, 10)
        val min = forecast.Temperature.Minimum
        val max = forecast.Temperature.Maximum

        val dateView = TextView(this).apply {
            text = "📅 $date"
            textSize = 18f
        }

        val tempView = TextView(this).apply {
            text = "❄️ Min: ${min.Value}°C   ☀️ Max: ${max.Value}°C"
            textSize = 16f
            setPadding(0, 8, 0, 0)
        }


        dayLayout.addView(dateView)
        dayLayout.addView(tempView)

        forecastContainer.addView(dayLayout)
        }
    }



    private fun showToast(message: String) {
    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }
}
