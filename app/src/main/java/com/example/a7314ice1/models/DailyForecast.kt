package com.example.a7314ice1.models

data class DailyForecastsResponse(
    val DailyForecasts: List<DailyForecast>
)

data class DailyForecast(
    val Date: String,
    val Temperature: Temperature
)

data class Temperature(
    val Minimum: TempDetail,
    val Maximum: TempDetail
)

data class TempDetail(
    val Value: Double,
    val Unit: String
)
