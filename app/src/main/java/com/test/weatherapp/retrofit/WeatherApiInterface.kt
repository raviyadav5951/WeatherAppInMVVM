package com.test.weatherapp.retrofit

import com.test.weatherapp.db.WeatherResponse
import com.test.weatherapp.model.CurrentLocationWeather
import io.reactivex.Single
import retrofit2.http.Query
import retrofit2.http.GET
interface WeatherApiInterface {

    //We have to pass lat,long
    @GET("weather?")
    fun getCurrentLocationWeather(@Query("lat") latitude:String?,
                                  @Query("lon") longitude:String?,
                                  @Query("units") units:String="metric",
                                  @Query("appid") appid:String):Single<CurrentLocationWeather>

    //we have to pass lat,long and countofdays
    @GET("forecast/daily")
    fun getTwoWeekData(@Query("lat") latitude:String?,
                       @Query("lon") longitude:String?,
                       @Query("units") units:String="metric",
                       @Query("cnt") countOfDays:Int,
                       @Query("appid") appid:String):Single<WeatherResponse>
}