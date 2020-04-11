package com.test.weatherapp.retrofit

import com.test.weatherapp.db.WeatherResponse
import com.test.weatherapp.model.CurrentLocationWeather
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * This class can also be termed as Repository in MVVM terminology
 */

class WeatherApiService {
    private val REQUEST_TIMEOUT_DURATION = 10
    private val DEBUG = true
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
//  private val API_KEY: String = "c484afd3fa0022bc7ab50ee22c4de97c"
    private val API_KEY: String = "cc65d87afabccabcd3c47633ef7d504d"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(createRequestInterceptorClient())
        .build()
        .create(WeatherApiInterface::class.java)

    //These two fun will call respective api for fetching current location weather data and 16 days historic data respectively

    fun getCurrentLocation(latitude: String?, longitude: String?): Single<CurrentLocationWeather> =
        api.getCurrentLocationWeather(latitude, longitude, appid = API_KEY)

    fun getTwoWeekData(
        latitude: String?,
        longitude: String?,
        countOfDays: Int
    ): Single<WeatherResponse> = api.getTwoWeekData(
        latitude = latitude,
        longitude = longitude,
        countOfDays = countOfDays,
        appid = API_KEY
    )


    //here we have added the interceptor to display logs when api is called.
    private fun createRequestInterceptorClient(): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        return if (DEBUG) {
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
                .build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)
                .build()
        }
    }
}