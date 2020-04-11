package com.test.weatherapp.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.test.weatherapp.R
import com.test.weatherapp.db.WeekList
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_weather_day.view.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherListAdapter(val list: RealmResults<WeekList>?) :
    RecyclerView.Adapter<WeatherListAdapter.ChildViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherListAdapter.ChildViewHolder {

        return ChildViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_weather_day, parent, false)
        )
    }


    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun getItemCount(): Int {
        Log.e("item", "count=" + list?.size)
        return list!!.size
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val weekList = list?.get(position)
        bindChild(holder, weekList)

    }

    private fun bindChild(vh2: ChildViewHolder, weekObject: WeekList?) {
        val updatedAt: Long = weekObject!!.dt
        vh2.itemView.tvDate.text =
            SimpleDateFormat("dd/MM", Locale.ENGLISH).format(Date(updatedAt * 1000))
        vh2.itemView.tvPressure.text = weekObject.pressure.toString()
        vh2.itemView.tvHumidity.text = weekObject.humidity.toString()
        vh2.itemView.tvWind.text = weekObject.speed.toString()
        vh2.itemView.tvTemp.text =
            weekObject.temp?.min.toString() + "°C to " + weekObject.temp?.max.toString() + "°C"
        vh2.itemView.imgWeather.load("https://openweathermap.org/img/wn/${weekObject.weather[0]?.icon}.png")

    }


}