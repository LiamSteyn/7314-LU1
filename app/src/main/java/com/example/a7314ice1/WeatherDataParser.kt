package com.example.a7314ice1

import org.json.JSONArray
import org.json.JSONObject

data class DayForecast(val date: String, val minTemp: Double, val maxTemp: Double, val iconCode: Int)

class WeatherDataParser {

    fun getWeatherIconCode(data: String): Int {
        val root = JSONObject(data)
        val dailyArray = root.getJSONArray("DailyForecasts")
        val obj = dailyArray.getJSONObject(0) // First day's forecast
        return obj.getJSONObject("Day").getInt("Icon")
    }

    fun getFiveDayForecast(data: String): List<DayForecast> {
        val root = JSONObject(data)
        val dailyArray = root.getJSONArray("DailyForecasts")
        val list = mutableListOf<DayForecast>()
        for (i in 0 until dailyArray.length()) {
            val dayObj = dailyArray.getJSONObject(i)
            val date = dayObj.getString("Date")
            val temperature = dayObj.getJSONObject("Temperature")
            val min = temperature.getJSONObject("Minimum").getDouble("Value")
            val max = temperature.getJSONObject("Maximum").getDouble("Value")
            val iconCode = dayObj.getJSONObject("Day").getInt("Icon")
            list.add(DayForecast(date, min, max, iconCode))
        }
        return list
    }
}
