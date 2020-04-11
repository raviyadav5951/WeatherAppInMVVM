package com.test.weatherapp.view

import androidx.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration

class WeatherApplication :MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()

        //Realm Config
        Realm.init(this)
        val config = RealmConfiguration.Builder().name("myrealm.realm").build()
        Realm.setDefaultConfiguration(config)
    }
}