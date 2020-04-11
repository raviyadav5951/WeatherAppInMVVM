package com.test.weatherapp.view

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.test.weatherapp.databinding.ActivityMainBinding
import com.test.weatherapp.db.WeatherResponse
import com.test.weatherapp.model.CurrentLocationWeather
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

    val CITY: String = "Pune,In"
    val API = "cc65d87afabccabcd3c47633ef7d504d"
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


        setupLocation()
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

                populateData(currentLocationData)
            }
        }

    private val twoWeekDataObserver =
        androidx.lifecycle.Observer<WeatherResponse> { twoWeekData ->
            twoWeekData?.let {
                realm.executeTransaction { realm ->
                    // Add a person
                    realm.insertOrUpdate(twoWeekData)

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

    private fun populateData(result: String?) {
        try {
            /* Extracting JSON returns from the API */
            val jsonObj = JSONObject(result)
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

            val updatedAt: Long = jsonObj.getLong("dt")
            val updatedAtText =
                "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )
            val temp = main.getString("temp") + "°C"
            val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
            val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")

            val sunrise: Long = sys.getLong("sunrise")
            val sunset: Long = sys.getLong("sunset")
            val windSpeed = wind.getString("speed")
            val weatherDescription = weather.getString("description")

            val address = jsonObj.getString("name") + ", " + sys.getString("country")

            binding.address.text = address
            binding.updatedAt.text = updatedAtText
            binding.status.text = weatherDescription.capitalize()
            binding.temp.text = temp
            binding.tempMin.text = tempMin
            binding.tempMax.text = tempMax
            binding.sunrise.text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
            binding.sunset.text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
            binding.wind.text = windSpeed
            binding.pressure.text = pressure
            binding.humidity.text = humidity

            binding.loadingView.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE

        } catch (e: Exception) {
            binding.loadingView.visibility = View.GONE
            binding.listError.visibility = View.VISIBLE
            e.printStackTrace()
        }
    }

    private fun populateData(locationResponse: CurrentLocationWeather) {
        try {
            val main = locationResponse.main
            val sys = locationResponse.sys
            val wind = locationResponse.wind
            val weather = locationResponse.weather[0]

            val updatedAt: Long = locationResponse.dt
            val updatedAtText =
                "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )
            val temp = main.temp.toString() + "°C"
            val tempMin = "Min Temp: " + main.temp_min.toString() + "°C"
            val tempMax = "Max Temp: " + main.temp_max.toString() + "°C"
            val pressure = main.pressure
            val humidity = main.humidity

            val sunrise: Long = sys.sunrise
            val sunset: Long = sys.sunset
            val windSpeed = wind.speed
            val weatherDescription = weather.description

            val address = locationResponse.name + ", " + sys.country

            binding.address.text = address
            binding.updatedAt.text = updatedAtText
            binding.status.text = weatherDescription.capitalize()
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
