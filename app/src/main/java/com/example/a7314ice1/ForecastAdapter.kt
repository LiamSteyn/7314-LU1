package com.example.a7314ice1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a7314ice1.models.DailyForecast

class ForecastAdapter(private val forecastList: List<DailyForecast>) :
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconImageView: ImageView = view.findViewById(R.id.iconImageView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val tempTextView: TextView = view.findViewById(R.id.tempTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_item, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecastList[position]

        // Format icon number to always have 2 digits (e.g., 01, 02,...10)
        val iconNumber = forecast.Day.Icon.toString().padStart(2, '0')
        val iconUrl = "https://developer.accuweather.com/sites/default/files/${iconNumber}-s.png"

        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.iconImageView)

        holder.dateTextView.text = forecast.Date.substring(0, 10)

        val min = forecast.Temperature.Minimum.Value
        val max = forecast.Temperature.Maximum.Value
        holder.tempTextView.text = "Min: $min°C / Max: $max°C"
    }

    override fun getItemCount(): Int = forecastList.size
}
