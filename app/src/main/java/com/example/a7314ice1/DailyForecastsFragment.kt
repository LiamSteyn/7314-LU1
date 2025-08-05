package com.example.a7314ice1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a7314ice1.R.id.forecastRecyclerView
import com.example.a7314ice1.models.DailyForecast

class DailyForecastsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var forecastAdapter: ForecastAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daily_forecasts, container, false)

        recyclerView = view.findViewById(forecastRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    fun setDailyForecasts(forecasts: List<DailyForecast>) {
        forecastAdapter = ForecastAdapter(forecasts)
        recyclerView.adapter = forecastAdapter
    }
}
