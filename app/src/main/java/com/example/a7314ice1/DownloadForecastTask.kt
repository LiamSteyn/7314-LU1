package com.example.a7314ice1

import android.os.AsyncTask

class DownloadForecastTask(
    private val callback: (List<DayForecast>?) -> Unit
) : AsyncTask<String, Void, List<DayForecast>?>() {

    private val httpClient = WeatherHttpClient()
    private val parser = WeatherDataParser()

    override fun doInBackground(vararg params: String): List<DayForecast>? {
        return try {
            val json = httpClient.getWeatherData(params[0])
            parser.getFiveDayForecast(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: List<DayForecast>?) {
        callback(result)
    }
}
