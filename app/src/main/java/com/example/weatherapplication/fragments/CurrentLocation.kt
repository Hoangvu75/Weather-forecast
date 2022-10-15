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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapplication.MainActivity
import com.example.weatherapplication.R
import com.example.weatherapplication.adapter.WeatherAdapter
import com.example.weatherapplication.getDeviceLocation.getLocationFunctions.*
import com.example.weatherapplication.models.Weather
import kotlinx.android.synthetic.main.fragment_current_location.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong


class CurrentLocation : Fragment() {

    private var currentLocationFragmentCityName: TextView? = null
    private var currentLocationFragmentTemperature: TextView? = null
    private var currentLocationFragmentWeatherDescription: TextView? = null
    private var currentLocationFragmentTime: TextView? = null

    private var currentLocationFragmentClouds: TextView? = null
    private var currentLocationFragmentVisibility: TextView? = null
    private var currentLocationFragmentHumidity: TextView? = null
    private var currentLocationFragmentTempMin: TextView? = null
    private var currentLocationFragmentTempMax: TextView? = null
    private var currentLocationFragmentWindSpeed: TextView? = null
    private var currentLocationFragmentWindDegree: TextView? = null

    private var currentLocationFragmentLatitude: TextView? = null
    private var currentLocationFragmentLongitude: TextView? = null
    private var currentLocationFragmentLocation: TextView? = null

    private var userLatitude: Double? = null
    private var userLongitude: Double? = null
    private var userLocation: String? = null

    private var weatherAdapter: WeatherAdapter? = null
    private var mWeathers: List<Weather>? = null
    private var recyclerView: RecyclerView? = null

    private var currentLocationFragmentViewMore: TextView? = null

    private var loadingDialog: Dialog? = null

    private var currentLocationFragmentSwipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_current_location, container, false)

        currentLocationFragmentCityName = view.findViewById(R.id.currentLocationFragmentCityName)
        currentLocationFragmentCityName?.setOnClickListener {
            getCityOnMap()
        }

        currentLocationFragmentTemperature = view.findViewById(R.id.currentLocationFragmentTemperature)
        currentLocationFragmentWeatherDescription = view.findViewById(R.id.currentLocationFragmentWeatherDescription)
        currentLocationFragmentTime = view.findViewById(R.id.currentLocationFragmentTime)

        currentLocationFragmentClouds = view.findViewById(R.id.currentLocationFragmentClouds)
        currentLocationFragmentVisibility = view.findViewById(R.id.currentLocationFragmentVisibility)
        currentLocationFragmentHumidity = view.findViewById(R.id.currentLocationFragmentHumidity)
        currentLocationFragmentTempMin = view.findViewById(R.id.currentLocationFragmentTempMin)
        currentLocationFragmentTempMax = view.findViewById(R.id.currentLocationFragmentTempMax)
        currentLocationFragmentWindSpeed = view.findViewById(R.id.currentLocationFragmentWindSpeed)
        currentLocationFragmentWindDegree = view.findViewById(R.id.currentLocationFragmentWindDegree)

        currentLocationFragmentLatitude = view.findViewById(R.id.currentLocationFragmentLatitude)
        currentLocationFragmentLongitude = view.findViewById(R.id.currentLocationFragmentLongitude)
        currentLocationFragmentLocation = view.findViewById(R.id.currentLocationFragmentLocation)
        currentLocationFragmentLocation?.setOnClickListener {
            getLocationOnMap()
        }

        recyclerView = view.findViewById(R.id.currentLocationFragmentRvWeather)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        mWeathers = ArrayList<Weather>()

        currentLocationFragmentViewMore = view.findViewById(R.id.currentLocationFragmentViewMore)
        currentLocationFragmentViewMore?.setOnClickListener {
            viewWholeRecyclerView()
            currentLocationFragmentViewMore?.visibility = View.GONE
        }

        loadingDialog = Dialog(requireContext())
        loadingDialog!!.setContentView(R.layout.dialog_custom_progress)
        loadingDialog!!.setCancelable(false)
        loadingDialog!!.show()

        currentLocationFragmentSwipeRefreshLayout = view.findViewById(R.id.currentLocationFragmentLayout)
        currentLocationFragmentSwipeRefreshLayout!!.setOnRefreshListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("refresh", true)
            startActivity(intent)
            requireActivity().finish()
            currentLocationFragmentSwipeRefreshLayout!!.isRefreshing = false
        }

        return view
    }

    private fun getLocationOnMap() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Get More Info")
        builder.setMessage("Do you want to get this location on map?")
        builder.setPositiveButton("Yes") { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$userLatitude,$userLongitude"))
            startActivity(intent)
        }.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    private fun getCityOnMap() {
        var location = currentLocationFragmentCityName?.text.toString()
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
        val url2 = "https://api.openweathermap.org/data/2.5/forecast?lat=$userLatitude&lon=$userLongitude&appid=${apiKey2}"
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userLatitude = getLatitude()
        userLongitude = getLongitude()
        userLocation = getAddress()

        val queue = Volley.newRequestQueue(context)
        val apiKey = "9a458b2af24d091772fa6fc0fb52c1d9"
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$userLatitude&lon=$userLongitude&appid=${apiKey}"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                val main = response.getJSONObject("main")
                val tempF = main.getDouble("temp")
                val tempC = ((tempF - 273.15) * 100.0).roundToLong() / 100.0
                var name = response.getString("name")
                if (name == "Thong Tay Hoi") {
                    name = "Ho Chi Minh City"
                }
                val country = response.getJSONObject("sys").getString("country")
                val weather = response.getJSONArray("weather")
                val weatherDescription = weather.getJSONObject(0).getString("description")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

                val clouds = response.getJSONObject("clouds").getInt("all")
                val visibility = response.getString("visibility")
                val humidity = response.getJSONObject("main").getDouble("humidity")
                val tempMin = main.getDouble("temp_min")
                val tempMax = main.getDouble("temp_max")
                val windSpeed = response.getJSONObject("wind").getDouble("speed")
                val windDegree = response.getJSONObject("wind").getDouble("deg")

                when (weather.getJSONObject(0).getString("main")) {
                    "Thunderstorm" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.thunderstorm)
                    "Drizzle" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.drizzle)
                    "Rain" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.rain)
                    "Snow" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.snow)
                    "Mist" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.mist)
                    "Smoke" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.smoke)
                    "Haze" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.haze)
                    "Dust" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.dust)
                    "Fog" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.fog)
                    "Sand" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.sand)
                    "Ash" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.ash)
                    "Squall" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.squall)
                    "Tornado" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.tornado)
                    "Clear" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.clear)
                    "Clouds" -> currentLocationFragmentLayout.setBackgroundResource(R.drawable.clouds)
                }

                currentLocationFragmentCityName?.text = Html.fromHtml("<u>$name - $country</u>")
                currentLocationFragmentTemperature?.text = "${tempC}°C  -  ${tempF}°F"
                currentLocationFragmentWeatherDescription?.text = weatherDescription

                currentLocationFragmentClouds?.text = "$clouds%"
                currentLocationFragmentVisibility?.text = "$visibility m"
                currentLocationFragmentHumidity?.text = "${humidity}%"
                currentLocationFragmentTempMin?.text = "Min: ${((tempMin - 273.15) * 100.0).roundToLong() / 100.0}°C - $tempMin°F"
                currentLocationFragmentTempMax?.text = "Max: ${((tempMax - 273.15) * 100.0).roundToLong() / 100.0}°C - $tempMax°F"
                currentLocationFragmentWindSpeed?.text = "Spd: $windSpeed m/s"
                currentLocationFragmentWindDegree?.text = "Deg: ${windDegree}°F"

                currentLocationFragmentLatitude?.text = Html.fromHtml("<b>Lat:</b> $userLatitude")
                currentLocationFragmentLongitude?.text = Html.fromHtml("<b>Lon:</b> $userLongitude")
                currentLocationFragmentLocation?.text = Html.fromHtml("<b>Location:</b> <u>$userLocation</u>")

                val handler = Handler()
                val runnable = object : Runnable {
                    override fun run() {
                        currentLocationFragmentTime?.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        handler.postDelayed(this, 1000)
                    }
                }
                handler.postDelayed(runnable, 1000)
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
        queue.add(jsonObjectRequest)

        val queue2 = Volley.newRequestQueue(context)
        val apiKey2 = "9a458b2af24d091772fa6fc0fb52c1d9"
        val url2 = "https://api.openweathermap.org/data/2.5/forecast?lat=$userLatitude&lon=$userLongitude&appid=${apiKey2}"
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
            }, { error ->
                val errorDialog = AlertDialog.Builder(context)
                errorDialog.setTitle("Error")
                errorDialog.setMessage("Something went wrong. Please try again later.\n\n${error.message}")
                errorDialog.setPositiveButton("OK") { _, _ ->
                    errorDialog.create().dismiss()
                }.show()
            }
        )
        queue2.add(jsonObjectRequest2)
    }
}