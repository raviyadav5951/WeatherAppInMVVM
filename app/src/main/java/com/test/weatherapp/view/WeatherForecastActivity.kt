package com.test.weatherapp.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.weatherapp.databinding.ActiivtyForecastBinding
import com.test.weatherapp.db.WeekList
import io.realm.Realm
import io.realm.RealmResults


class WeatherForecastActivity : AppCompatActivity() {

    private lateinit var binding: ActiivtyForecastBinding
    private lateinit var realm: Realm
    private lateinit var adapter: WeatherListAdapter
    private var list: RealmResults<WeekList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActiivtyForecastBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()

        setUpRecyclerView()

        readFromDatabase()
    }

    private fun setUpRecyclerView() {
        binding.rvWeatherList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    }


    private fun readFromDatabase() {
        list = realm.where(WeekList::class.java).findAll()
        Log.e("list size", "lis=" + list?.size)
        list?.let {
            adapter = WeatherListAdapter(list)
            binding.rvWeatherList.adapter = adapter

        }
        binding.loadingView.visibility = View.GONE
        if (list.isNullOrEmpty()) {
            binding.listError.visibility = View.VISIBLE
            binding.rvWeatherList.visibility = View.GONE
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // Remember to close Realm when done.
    }
}