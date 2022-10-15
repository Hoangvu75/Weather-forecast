package com.example.weatherapplication.models

class Weather {
    private var city: String = ""
    private var time: String = ""
    private var temp: String = ""
    private var weatherDescription: String = ""
    private var main: String = ""

    constructor()

    constructor(
        city: String,
        time: String,
        temp: String,
        weatherDescription: String,
        main: String) {
        this.city = city
        this.time = time
        this.temp = temp
        this.weatherDescription = weatherDescription
        this.main = main
    }

    fun getCity(): String {
        return city
    }

    fun getTime(): String {
        return time
    }

    fun getTemp(): String {
        return temp
    }

    fun getWeatherDescription(): String {
        return weatherDescription
    }

    fun getMain(): String {
        return main
    }
}