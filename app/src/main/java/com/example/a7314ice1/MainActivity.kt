package com.example.a7314ice1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.a7314ice1.models.DailyForecast
import com.example.a7314ice1.models.DayNight
import com.example.a7314ice1.models.TempValue
import com.example.a7314ice1.models.Temperature
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : AppCompatActivity() {


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val apiKey = "YOUR_API_KEY_HERE"

    private var allForecasts: List<DailyForecast> = emptyList()

    private var currentLat: Double? = null
    private var currentLon: Double? = null


    private lateinit var todayContainer: FrameLayout
    private lateinit var fiveDayContainer: FrameLayout
    private lateinit var cityContainer: FrameLayout
    private lateinit var todayForecastContainer: LinearLayout
    private lateinit var forecastContainer: LinearLayout
    private lateinit var cityNameView: TextView
    private lateinit var cityWeatherResult: TextView





    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todayContainer = findViewById(R.id.todayContainer)
        fiveDayContainer = findViewById(R.id.fiveDayContainer)
        cityContainer = findViewById(R.id.cityContainer)
        todayForecastContainer = findViewById(R.id.todayForecastContainer)
        forecastContainer = findViewById(R.id.forecastContainer)

        cityNameView = findViewById(R.id.cityNameView)
        cityWeatherResult = findViewById(R.id.cityWeatherResult)


        val btnToday = findViewById<Button>(R.id.btnToday)
        val btnFiveDay = findViewById<Button>(R.id.btnFiveDay)
        val btnCity = findViewById<Button>(R.id.btnCity)

        btnToday.setOnClickListener { showSection("today") }
        btnFiveDay.setOnClickListener { showSection("fiveDay") }
        btnCity.setOnClickListener { showSection("city") }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocationWeather() // automatically load "today"
    }

    private fun getCurrentLocationWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                val lat = location?.latitude ?: -33.9249  // fallback Cape Town
                val lon = location?.longitude ?: 18.4241

                // 🔹 Save globally
                currentLat = lat
                currentLon = lon

                fetchWeather(lat, lon)
            }
        }
    }



    private fun fetchWeather(lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Step 1: Get locationKey from AccuWeather
                val locationUrl = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=$apiKey&q=$lat,$lon"
                val locationJson = WeatherHttpClient().getWeatherData(locationUrl)
                val locationKey = JSONObject(locationJson).getString("Key")

                // Step 2: Get 5-day forecast
                val forecastUrl = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/$locationKey?apikey=$apiKey&metric=true"
                val forecastJson = WeatherHttpClient().getWeatherData(forecastUrl)

                val locationObj = JSONObject(locationJson)
                val cityName = locationObj.getString("LocalizedName")

                // Step 3: Get current conditions for the city
                val currentUrl = "https://dataservice.accuweather.com/currentconditions/v1/$locationKey?apikey=$apiKey&metric=true"
                val currentJson = WeatherHttpClient().getWeatherData(currentUrl)
                val currentArray = JSONObject("{\"data\":$currentJson}").getJSONArray("data")
                val currentObj = currentArray.getJSONObject(0)
                val tempObj = currentObj.getJSONObject("Temperature").getJSONObject("Metric")
                val currentTemp = tempObj.getDouble("Value")
                val weatherText = currentObj.getString("WeatherText")


                val forecastList = parseForecasts(forecastJson)
                allForecasts = forecastList // store globally
                withContext(Dispatchers.Main) {
                    displayForecasts(forecastList, forecastContainer)
                }


                withContext(Dispatchers.Main) {
                    displayForecasts(allForecasts, forecastContainer) // show 5 days initially
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showToast("Failed to load forecast: ${e.message}")
                }
            }
        }
    }

    private fun loadCityWeather() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lat = currentLat ?: -33.9249  // fallback Cape Town
                val lon = currentLon ?: 18.4241

                val url = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=$apiKey&q=$lat,$lon"
                val locationJson = WeatherHttpClient().getWeatherData(url)
                val locationObj = JSONObject(locationJson)
                val cityName = locationObj.getString("LocalizedName")
                val locationKey = locationObj.getString("Key")

                // Now fetch current conditions
                val currentUrl = "https://dataservice.accuweather.com/currentconditions/v1/$locationKey?apikey=$apiKey&metric=true"
                val currentJson = WeatherHttpClient().getWeatherData(currentUrl)
                val currentArray = JSONObject("{\"data\":$currentJson}").getJSONArray("data")
                val currentObj = currentArray.getJSONObject(0)
                val tempObj = currentObj.getJSONObject("Temperature").getJSONObject("Metric")
                val currentTemp = tempObj.getDouble("Value")
                val weatherText = currentObj.getString("WeatherText")

                withContext(Dispatchers.Main) {
                    cityNameView.visibility = View.VISIBLE
                    cityNameView.text = cityName
                    cityWeatherResult.text = "🌡️ ${currentTemp.toInt()}°C\n$weatherText"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    cityWeatherResult.text = "Error: ${e.message}"
                }
            }
        }
    }


    private fun parseForecasts(jsonString: String): List<DailyForecast> {
        val list = mutableListOf<DailyForecast>()
        val root = JSONObject(jsonString)
        val dailyArray = root.getJSONArray("DailyForecasts")

        for (i in 0 until dailyArray.length()) {
            val item = dailyArray.getJSONObject(i)
            val date = item.getString("Date")

            val tempObj = item.getJSONObject("Temperature")
            val minObj = tempObj.getJSONObject("Minimum")
            val maxObj = tempObj.getJSONObject("Maximum")
            val min = TempValue(Value = minObj.getDouble("Value"), Unit = minObj.getString("Unit"))
            val max = TempValue(Value = maxObj.getDouble("Value"), Unit = maxObj.getString("Unit"))
            val temperature = Temperature(Minimum = min, Maximum = max)

            val dayObj = item.getJSONObject("Day")
            val day = DayNight(Icon = dayObj.getInt("Icon"), IconPhrase = dayObj.getString("IconPhrase"))

            list.add(DailyForecast(Date = date, Temperature = temperature, Day = day))
        }

        return list
    }

    private fun showSection(section: String) {
        // Show/hide containers
        todayContainer.visibility = if (section == "today") View.VISIBLE else View.GONE
        fiveDayContainer.visibility = if (section == "fiveDay") View.VISIBLE else View.GONE
        cityContainer.visibility = if (section == "city") View.VISIBLE else View.GONE

        when (section) {
            "today" -> {
                if (allForecasts.isNotEmpty()) {
                    // Use the new display function for today
                    displayTodayForecast(allForecasts[0])
                } else {
                    showToast("Weather data not loaded yet")
                }
            }
            "fiveDay" -> {
                if (allForecasts.isNotEmpty()) {
                    displayForecasts(allForecasts, forecastContainer)
                } else {
                    showToast("Weather data not loaded yet")
                }
            }
            "city" -> {
                if (cityNameView.text.isNullOrEmpty()) {
                    cityNameView.text = "Loading..."
                    cityWeatherResult.text = ""
                }
                loadCityWeather()
            }
        }
    }



    private fun displayForecasts(forecastList: List<DailyForecast>, container: LinearLayout) {
        container.removeAllViews() // remove previous children

        for (forecast in forecastList) {
            val dayLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(16, 16, 16, 16) }
                setPadding(24, 24, 24, 24)
                setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
            }

            val dateView = TextView(this).apply {
                text = "📅 ${forecast.Date.substring(0, 10)}"
                textSize = 18f
            }

            val tempView = TextView(this).apply {
                text = "❄️ Min: ${forecast.Temperature.Minimum.Value}°${forecast.Temperature.Minimum.Unit}   ☀️ Max: ${forecast.Temperature.Maximum.Value}°${forecast.Temperature.Maximum.Unit}"
                textSize = 16f
                setPadding(0, 8, 0, 0)
            }

            val dayPhrase = TextView(this).apply {
                text = "🌤️ ${forecast.Day.IconPhrase}"
                textSize = 16f
                setPadding(0, 4, 0, 0)
            }

            dayLayout.addView(dateView)
            dayLayout.addView(tempView)
            dayLayout.addView(dayPhrase)

            container.addView(dayLayout)
        }
    }

    private fun displayTodayForecast(forecast: DailyForecast) {
        todayForecastContainer.removeAllViews() // clear previous

        // Outer container
        val outerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(32, 32, 32, 32)
            gravity = android.view.Gravity.CENTER
        }

        // Weather Icon
        val iconView = ImageView(this).apply {

            layoutParams = LinearLayout.LayoutParams(200, 200).apply { gravity = android.view.Gravity.CENTER }
        }

        // Main Temperature
        val mainTempView = TextView(this).apply {
            text = "${forecast.Temperature.Maximum.Value.toInt()}°${forecast.Temperature.Maximum.Unit}"
            textSize = 48f
            setPadding(0, 16, 0, 8)
            gravity = android.view.Gravity.CENTER
        }

        // Weather phrase
        val phraseView = TextView(this).apply {
            text = forecast.Day.IconPhrase
            textSize = 20f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }

        // Min/Max temps horizontal
        val minMaxLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
        }

        val minTempView = TextView(this).apply {
            text = "❄️ Min ${forecast.Temperature.Minimum.Value.toInt()}°"
            textSize = 18f
            setPadding(16, 0, 16, 0)
        }

        val maxTempView = TextView(this).apply {
            text = "☀️ Max ${forecast.Temperature.Maximum.Value.toInt()}°"
            textSize = 18f
            setPadding(16, 0, 16, 0)
        }

        minMaxLayout.addView(minTempView)
        minMaxLayout.addView(maxTempView)

        // Add all views to outer container
        outerLayout.addView(iconView)
        outerLayout.addView(mainTempView)
        outerLayout.addView(phraseView)
        outerLayout.addView(minMaxLayout)

        todayForecastContainer.addView(outerLayout)
    }




    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationWeather()
        } else {
            showToast("Location permission denied. Using default location.")
            fetchWeather(-33.9249, 18.4241) // fallback: Cape Town
        }
    }
}
