package com.example.weatherapplication.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapplication.R
import com.example.weatherapplication.adapter.WeatherAdapter
import com.example.weatherapplication.models.Weather
import kotlinx.android.synthetic.main.fragment_other_location.*
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToLong

class OtherLocation : Fragment() {

    private var otherLocationFragmentEtSearch: EditText? = null
    private var otherLocationFragmentTvSearchButton: TextView? = null

    private var otherLocationFragmentCityName: TextView? = null
    private var otherLocationFragmentTemperature: TextView? = null
    private var otherLocationFragmentWeatherDescription: TextView? = null

    private var otherLocationFragmentLlSearchResult: LinearLayout? = null
    private var otherLocationFragmentClouds: TextView? = null
    private var otherLocationFragmentVisibility: TextView? = null
    private var otherLocationFragmentHumidity: TextView? = null
    private var otherLocationFragmentTempMin: TextView? = null
    private var otherLocationFragmentTempMax: TextView? = null
    private var otherLocationFragmentWindSpeed: TextView? = null
    private var otherLocationFragmentWindDegree: TextView? = null

    private var otherLocationFragmentTime: TextView? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private var weatherAdapter: WeatherAdapter? = null
    private var mWeathers: List<Weather>? = null
    private var recyclerView: RecyclerView? = null

    private var otherLocationFragmentViewMore: TextView? = null

    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_other_location, container, false)

        otherLocationFragmentEtSearch = view.findViewById(R.id.otherLocationFragmentEtSearch)
        otherLocationFragmentTvSearchButton = view.findViewById(R.id.otherLocationFragmentTvSearchButton)

        otherLocationFragmentCityName = view.findViewById(R.id.otherLocationFragmentCityName)
        otherLocationFragmentCityName?.setOnClickListener {
            getCityOnMap()
        }

        otherLocationFragmentTemperature = view.findViewById(R.id.otherLocationFragmentTemperature)
        otherLocationFragmentWeatherDescription = view.findViewById(R.id.otherLocationFragmentWeatherDescription)

        otherLocationFragmentLlSearchResult = view.findViewById(R.id.otherLocationFragmentLlSearchResult)
        otherLocationFragmentLlSearchResult?.visibility = View.GONE

        otherLocationFragmentClouds = view.findViewById(R.id.otherLocationFragmentClouds)
        otherLocationFragmentVisibility = view.findViewById(R.id.otherLocationFragmentVisibility)
        otherLocationFragmentHumidity = view.findViewById(R.id.otherLocationFragmentHumidity)
        otherLocationFragmentTempMin = view.findViewById(R.id.otherLocationFragmentTempMin)
        otherLocationFragmentTempMax = view.findViewById(R.id.otherLocationFragmentTempMax)
        otherLocationFragmentWindSpeed = view.findViewById(R.id.otherLocationFragmentWindSpeed)
        otherLocationFragmentWindDegree = view.findViewById(R.id.otherLocationFragmentWindDegree)

        otherLocationFragmentTime = view.findViewById(R.id.otherLocationFragmentTime)

        recyclerView = view.findViewById(R.id.otherLocationFragmentRvWeather)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        mWeathers = ArrayList<Weather>()

        otherLocationFragmentViewMore = view.findViewById(R.id.otherLocationFragmentViewMore)

        otherLocationFragmentTvSearchButton?.setOnClickListener {
            handler?.removeCallbacks(runnable!!)
            searchForWeatherOfCity()
        }

        otherLocationFragmentViewMore?.setOnClickListener {
            viewWholeRecyclerView()
            otherLocationFragmentViewMore?.visibility = View.GONE
        }

        loadingDialog = Dialog(requireContext())
        loadingDialog!!.setContentView(R.layout.dialog_custom_progress)
        loadingDialog!!.setCancelable(false)

        return view
    }

    private fun getCityOnMap() {
        var location = otherLocationFragmentCityName?.text.toString()
        location = location.substring(0, location.indexOf("-"))

        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Get More Info")
        builder.setMessage("Do you want to get this city on map?")
        builder.setPositiveButton("Yes") { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$location"))
            startActivity(intent)
        }.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    private fun viewWholeRecyclerView() {
        loadingDialog!!.show()

        val queue2 = Volley.newRequestQueue(context)
        val apiKey2 = "9a458b2af24d091772fa6fc0fb52c1d9"
        val url2 = "https://api.openweathermap.org/data/2.5/forecast?q=${otherLocationFragmentEtSearch?.text}&appid=$apiKey2"
        val jsonObjectRequest2 = JsonObjectRequest(
            Request.Method.GET, url2, null, { response ->
                val city = response.getJSONObject("city")
                var name = city.getString("name")
                if (name == "Thong Tay Hoi") {
                    name = "Ho Chi Minh City"
                }
                val list = response.getJSONArray("list")
                (mWeathers as ArrayList<Weather>).clear()
                for (i in 0 until list.length()) {
                    val listObject = list.getJSONObject(i)
                    val main = listObject.getJSONObject("main")
                    val tempF = main.getDouble("temp")
                    val tempC = ((tempF - 273.15) * 100.0).roundToLong() / 100.0
                    val temp = "${tempC}°C  -  ${tempF}°F"
                    val weatherDescription = listObject.getJSONArray("weather").getJSONObject(0).getString("description")
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    val time = listObject.getString("dt_txt")
                    val weathers = Weather(name, time, temp, weatherDescription,
                        listObject.getJSONArray("weather").getJSONObject(0).getString("main"))
                    (mWeathers as ArrayList<Weather>).add(weathers)
                }
                weatherAdapter = WeatherAdapter(requireContext(), mWeathers!! as ArrayList<Weather>)
                recyclerView?.adapter = weatherAdapter
                loadingDialog!!.dismiss()
            }, { error ->
                val errorDialog = AlertDialog.Builder(context)
                errorDialog.setTitle("Error")
                errorDialog.setMessage("Something went wrong. Please try again later.\n\n${error.message}")
                errorDialog.setPositiveButton("OK") { _, _ ->
                    errorDialog.create().dismiss()
                }.show()
                loadingDialog!!.dismiss()
            }
        )
        queue2.add(jsonObjectRequest2)
    }

    @SuppressLint("SetTextI18n")
    private fun searchForWeatherOfCity() {
        loadingDialog!!.show()

        val queue = Volley.newRequestQueue(context)
        val apiKey = "9a458b2af24d091772fa6fc0fb52c1d9"
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${otherLocationFragmentEtSearch?.text}&appid=$apiKey"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                val weather = response.getJSONArray("weather")
                when (weather.getJSONObject(0).getString("main")) {
                    "Thunderstorm" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.thunderstorm)
                    "Drizzle" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.drizzle)
                    "Rain" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.rain)
                    "Snow" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.snow)
                    "Mist" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.mist)
                    "Smoke" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.smoke)
                    "Haze" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.haze)
                    "Dust" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.dust)
                    "Fog" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.fog)
                    "Sand" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.sand)
                    "Ash" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.ash)
                    "Squall" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.squall)
                    "Tornado" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.tornado)
                    "Clear" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.clear)
                    "Clouds" -> otherLocationFragmentLayout.setBackgroundResource(R.drawable.clouds)
                }

                var cityName = response.getString("name")
                if (cityName == "Thong Tay Hoi") {
                    cityName = "Ho Chi Minh City"
                }
                val country = response.getJSONObject("sys").getString("country")
                val tempF = response.getJSONObject("main").getDouble("temp")
                val tempC = ((tempF - 273.15) * 100.0).roundToLong() / 100.0
                val weatherDescription =
                    response.getJSONArray("weather").getJSONObject(0).getString("description")
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

                val clouds = response.getJSONObject("clouds").getInt("all")
                val visibility = response.getString("visibility")
                val humidity = response.getJSONObject("main").getDouble("humidity")
                val tempMin = response.getJSONObject("main").getDouble("temp_min")
                val tempMax = response.getJSONObject("main").getDouble("temp_max")
                val windSpeed = response.getJSONObject("wind").getDouble("speed")
                val windDegree = response.getJSONObject("wind").getDouble("deg")

                val timeZone = response.getInt("timezone")
                val utcTime = timeZone/3600
                var times = "${abs(timeZone/3600)}:${abs(timeZone%3600/60)}"
                if (abs(timeZone%3600/60) == 0) {
                    times = "${abs(timeZone/3600)}:${abs(timeZone%3600/60)}0"
                }
                if (utcTime >= 0) {
                    times = "+$times"
                } else if (utcTime < 0) {
                    times = "-$times"
                }

                otherLocationFragmentCityName?.text = Html.fromHtml("<u>$cityName - $country</u>")
                otherLocationFragmentTemperature?.text = "${tempC}°C  -  ${tempF}°F"
                otherLocationFragmentWeatherDescription?.text = weatherDescription

                otherLocationFragmentClouds?.text = "$clouds%"
                otherLocationFragmentVisibility?.text = "$visibility m"
                otherLocationFragmentHumidity?.text = "${humidity}%"
                otherLocationFragmentTempMin?.text = "Min: ${((tempMin - 273.15) * 100.0).roundToLong() / 100.0}°C - $tempMin°F"
                otherLocationFragmentTempMax?.text = "Max: ${((tempMax - 273.15) * 100.0).roundToLong() / 100.0}°C - $tempMax°F"
                otherLocationFragmentWindSpeed?.text = "Spd: $windSpeed m/s"
                otherLocationFragmentWindDegree?.text = "Deg: ${windDegree}°F"

                handler = Handler()
                runnable = object : Runnable {
                    override fun run() {
                        val tz = TimeZone.getTimeZone("GMT$times")
                        val calendar = Calendar.getInstance(tz)
                        val time = String.format(
                            "%d-%02d-%02d %02d:%02d:%02d",
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            calendar.get(Calendar.SECOND)
                        )
                        otherLocationFragmentTime?.text = time
                        handler!!.postDelayed(this, 1000)
                    }
                }
                handler!!.postDelayed(runnable as Runnable, 1000)

                otherLocationFragmentLlSearchResult?.visibility = View.VISIBLE
                loadingDialog!!.dismiss()
            }, {
                val errorDialog = AlertDialog.Builder(context)
                errorDialog.setTitle("Error")
                errorDialog.setMessage("Can't find this city")
                errorDialog.setPositiveButton("OK") { _, _ ->
                    errorDialog.create().dismiss()
                }.show()
                loadingDialog!!.dismiss()
            }
        )
        queue.add(jsonObjectRequest)

        val queue2 = Volley.newRequestQueue(context)
        val apiKey2 = "9a458b2af24d091772fa6fc0fb52c1d9"
        val url2 = "https://api.openweathermap.org/data/2.5/forecast?q=${otherLocationFragmentEtSearch?.text}&appid=$apiKey2"
        val jsonObjectRequest2 = JsonObjectRequest(
            Request.Method.GET, url2, null, { response ->
                val city = response.getJSONObject("city")
                var name = city.getString("name")
                if (name == "Thong Tay Hoi") {
                    name = "Ho Chi Minh City"
                }
                val list = response.getJSONArray("list")
                (mWeathers as ArrayList<Weather>).clear()
                for (i in 0 until 5) {
                    val listObject = list.getJSONObject(i)
                    val main = listObject.getJSONObject("main")
                    val tempF = main.getDouble("temp")
                    val tempC = ((tempF - 273.15) * 100.0).roundToLong() / 100.0
                    val temp = "${tempC}°C  -  ${tempF}°F"
                    val weatherDescription = listObject.getJSONArray("weather").getJSONObject(0).getString("description")
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    val time = listObject.getString("dt_txt")
                    val weathers = Weather(name, time, temp, weatherDescription,
                        listObject.getJSONArray("weather").getJSONObject(0).getString("main"))
                    (mWeathers as ArrayList<Weather>).add(weathers)
                }
                weatherAdapter = WeatherAdapter(requireContext(), mWeathers!! as ArrayList<Weather>)
                recyclerView?.adapter = weatherAdapter
            }, {
                Toast.makeText(context, "Can't find this city", Toast.LENGTH_LONG).show()
            }
        )
        queue2.add(jsonObjectRequest2)

        otherLocationFragmentViewMore?.visibility = View.VISIBLE
    }
}