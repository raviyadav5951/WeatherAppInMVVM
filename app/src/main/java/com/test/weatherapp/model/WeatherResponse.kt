package com.test.weatherapp.model

import com.google.gson.annotations.SerializedName

/**
 * All Model classes are here.
 */
data class WeatherResponse(
    val city : City,
    val cod : Int,
    val message : Double,
    val cnt : Int,
    @SerializedName("list")
    val weekList : List<WeekList>
)

data class City (
    val id : Int,
    val name : String,
    val coord : Coord,
    val country : String,
    val population : Int,
    val timezone : Int
)

data class Coord (
    val lon : Double,
    val lat : Double
)

data class WeekList (
    val dt : Int,
    val sunrise : Int,
    val sunset : Int,
    val temp : Temp,
    val feels_like : Feels_like,
    val pressure : Int,
    val humidity : Int,
    val weather : List<Weather>,
    val speed : Double,
    val deg : Int,
    val clouds : Int
)

data class Temp (
    val day : Double,
    val min : Double,
    val max : Double,
    val night : Double,
    val eve : Double,
    val morn : Double
)

data class Feels_like (
    val day : Double,
    val night : Double,
    val eve : Double,
    val morn : Double
)

data class Weather (
    val id : Int,
    val main : String,
    val description : String,
    val icon : String
)
data class Clouds(
    val all: Int
)

data class Main(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class CurrentLocationWeather(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Long,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val weather: List<Weather>,
    val wind: Wind

)

data class Wind(
    val deg: Int,
    val speed: Double
)
