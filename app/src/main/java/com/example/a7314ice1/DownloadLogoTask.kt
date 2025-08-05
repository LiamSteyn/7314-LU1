package com.example.a7314ice1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.net.URL

class DownloadLogoTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

    private val httpClient = WeatherHttpClient()
    private val parser = WeatherDataParser()

    override fun doInBackground(vararg params: String): Bitmap? {
        try {
            val apiUrl = params[0] // URL to get JSON weather data
            val json = httpClient.getWeatherData(apiUrl)
            val iconCode = parser.getWeatherIconCode(json)
            val iconStr = iconCode.toString().padStart(2, '0')
            val logoUrl = "https://developer.accuweather.com/sites/default/files/${iconStr}-s.png"

            val stream = URL(logoUrl).openStream()
            return BitmapFactory.decodeStream(stream)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        result?.let {
            imageView.setImageBitmap(it)
        }
    }
}
