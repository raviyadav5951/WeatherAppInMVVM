package com.test.weatherapp.view

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.test.weatherapp.databinding.ActivityMainBinding
import com.test.weatherapp.db.CurrentLocationWeather
import com.test.weatherapp.db.WeatherResponse
import com.test.weatherapp.db.WeekList
import com.test.weatherapp.retrofit.Utils
import com.test.weatherapp.viewmodel.MainActivityViewModel
import io.realm.Realm
import mumayank.com.airlocationlibrary.AirLocation
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var airLocation: AirLocation? = null
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()
        //creating viewmodel
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        //observer assignment
        viewModel.currentLocationData.observe(this, currentLocationDataObserver)
        viewModel.twoWeekData.observe(this, twoWeekDataObserver)
        viewModel.loading.observe(this, loadingLiveDataObserver)
        viewModel.loadError.observe(this, loadingErrorDataObserver)

        if(!Utils.isNetworkAvailable(this)){
            Toast.makeText(this,"You are offline.Please switch on the internet connection",Toast.LENGTH_LONG).show()
        }

        setupLocation()


        binding.llViewForecast.setOnClickListener {
            val forecastActivityIntent=Intent(this,WeatherForecastActivity::class.java)
            startActivity(forecastActivityIntent)
        }
        //for displaying old data if user is in offline mode
        populateData()
    }

    private val loadingErrorDataObserver = androidx.lifecycle.Observer<Boolean> { isError ->
        binding.listError.visibility = if (isError) View.VISIBLE else View.GONE
    }

    private val loadingLiveDataObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) {
            binding.listError.visibility = View.GONE
        }
    }

    private val currentLocationDataObserver =
        androidx.lifecycle.Observer<CurrentLocationWeather> { currentLocationData ->
            currentLocationData?.let {
                realm.executeTransaction { realm ->
                    // Add a person
                    realm.insertOrUpdate(currentLocationData)
                }

            }
        }

    private val twoWeekDataObserver =
        androidx.lifecycle.Observer<WeatherResponse> { twoWeekData ->
            twoWeekData?.let {
                realm.executeTransaction { realm ->
                    // Add a person
                    realm.insertOrUpdate(twoWeekData)
                    Log.e("last","afterapi data inserted")
                    populateData()
                }
            }


        }


    private fun setupLocation() {

        airLocation = AirLocation(this, true, true, object : AirLocation.Callbacks {
            override fun onSuccess(location: Location) {
                // location fetched successfully, proceed with it

                Log.e("loc", "lat=${location.latitude}")
                Log.e("loc", "long=${location.longitude}")

                viewModel.callWeatherApis(
                    latitude = location.latitude.toString(),
                    longitude = location.longitude.toString()
                )

            }

            override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                // couldn't fetch location due to reason available in locationFailedEnum
                // you may optionally do something to inform the user, even though the reason may be obvious

                Toast.makeText(applicationContext,"Location permission is required to proceed ahead in the application.",Toast.LENGTH_LONG).show()

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airLocation?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        airLocation?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun populateData() {
        try {
            Log.e("api","pop data called==")
            val locationResponse=realm.where(CurrentLocationWeather::class.java).findFirst()
            val main = locationResponse?.main
            val sys = locationResponse?.sys
            val wind = locationResponse?.wind
            val weather = locationResponse?.weather?.get(0)

            val updatedAt: Long = locationResponse?.dt!!
            val updatedAtText =
                "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )
            val temp = main?.temp.toString() + "°C"
            val tempMin = "Min Temp: " + main?.feels_like.toString() + "°C"
            val tempMax = "Max Temp: " + main?.temp_max.toString() + "°C"
            val pressure = main?.pressure
            val humidity = main?.humidity

            val sunrise: Long = sys!!.sunrise
            val sunset: Long = sys!!.sunset
            val windSpeed = wind?.speed
            val weatherDescription = weather?.description

            val address = locationResponse.name + ", " + sys?.country

            binding.address.text = address
            binding.updatedAt.text = updatedAtText
            binding.status.text = weatherDescription?.capitalize()
            binding.temp.text = temp
            binding.tempMin.text = tempMin
            binding.tempMax.text = tempMax
            binding.sunrise.text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
            binding.sunset.text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
            binding.wind.text = windSpeed.toString()
            binding.pressure.text = pressure.toString()
            binding.humidity.text = humidity.toString()

            binding.loadingView.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE

        } catch (e: Exception) {
            binding.loadingView.visibility = View.GONE
            binding.listError.visibility = View.VISIBLE
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // Remember to close Realm when done.
    }

}
