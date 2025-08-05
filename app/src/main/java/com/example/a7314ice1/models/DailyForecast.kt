package com.example.a7314ice1.models

data class DailyForecastsResponse(
    val DailyForecasts: List<DailyForecast>
)

data class DailyForecast(
    val Date: String,
    val Temperature: Temperature,
    val Day: DayNight
)

data class Temperature(
    val Minimum: TempValue,
    val Maximum: TempValue
)

data class TempValue(
    val Value: Double,
    val Unit: String
)

data class DayNight(
    val Icon: Int,
    val IconPhrase: String
)