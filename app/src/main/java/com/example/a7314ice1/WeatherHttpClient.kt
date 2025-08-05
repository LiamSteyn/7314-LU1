package com.example.a7314ice1

import java.net.HttpURLConnection
import java.net.URL

class WeatherHttpClient {
    fun getWeatherData(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()
        return connection.inputStream.bufferedReader().readText()
    }
}
