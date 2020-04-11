package com.test.weatherapp.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class WeatherResponse(
    var city: City?=null,
    var cod: Int = 0,
    var message: Double=0.0,
    var cnt: Int = 0,
    var list: RealmList<WeekList> = RealmList()
) : RealmObject()

open class City(
    var id: Int = 0,
    var name: String?=null,
    var coord: Coord?=null,
    var country: String?=null,
    var population: Int = 0,
    var timezone: Int=0
) : RealmObject()

open class Coord(
    var lon: Double=0.0,
    var lat: Double=0.0
) : RealmObject()

open class WeekList(
    @PrimaryKey
    var dt: Int = 0,
    var sunrise: Int = 0,
    var sunset: Int = 0,
    var temp: Temp?=null,
    var feels_like: Feels_like?=null,
    var pressure: Int = 0,
    var humidity: Int = 0,
    var weather: RealmList<Weather> = RealmList(),
    var speed: Double=0.0,
    var deg: Int = 0,
    var clouds: Int=0
) : RealmObject()

open class Temp(
    var day: Double=0.0,
    var min: Double=0.0,
    var max: Double=0.0,
    var night: Double=0.0,
    var eve: Double=0.0,
    var morn: Double=0.0
) : RealmObject()

open class Feels_like(
    var day: Double=0.0,
    var night: Double=0.0,
    var eve: Double=0.0,
    var morn: Double=0.0
) : RealmObject()

open class Weather(
    @PrimaryKey
    var id: Int = 0,
    var main: String?=null,
    var description: String?=null,
    var icon: String?=null
) : RealmObject()


