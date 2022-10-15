package com.example.weatherapplication.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapplication.R
import com.example.weatherapplication.models.Weather
import kotlinx.android.synthetic.main.fragment_current_location.*
import java.util.*
import kotlin.math.roundToLong

class WeatherAdapter(private val mContext: Context, private val mWeathers: List<Weather>): RecyclerView.Adapter<WeatherAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.weather_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = mWeathers[position]
        holder.weatherItemCityName.text = weather.getCity()
        holder.weatherItemTime.text = weather.getTime()
        holder.weatherItemTemperature.text = weather.getTemp()
        holder.weatherItemWeatherDescription.text = weather.getWeatherDescription()

        when (weather.getMain()) {
            "Thunderstorm" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.thunderstorm)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_thunderstorm)
            }
            "Drizzle" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.drizzle)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_drizzle)
            }
            "Rain" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.rain)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_rain)
            }
            "Snow" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.snow)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_snow)
            }
            "Mist" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.mist)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_atmosphere)
            }
            "Smoke" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.smoke)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_atmosphere)
            }
            "Haze" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.haze)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_atmosphere)
            }
            "Dust" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.dust)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_atmosphere)
            }
            "Fog" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.fog)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_atmosphere)
            }
            "Sand" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.sand)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_atmosphere)
            }
            "Ash" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.ash)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_atmosphere)
            }
            "Squall" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.squall)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_atmosphere)
            }
            "Tornado" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.tornado)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_atmosphere)
            }
            "Clear" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.clear)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_clear)
            }
            "Clouds" -> {
                holder.weatherItemLayout.setBackgroundResource(R.drawable.clouds)
                holder.weatherItemWeatherIcon.setBackgroundResource(R.drawable.ic_clouds)
            }
        }

        holder.weatherItemCardView.setOnClickListener {
            val loadingDialog = Dialog(mContext)
            loadingDialog.setContentView(R.layout.dialog_custom_progress)
            loadingDialog.setCancelable(false)
            loadingDialog.show()

            val queue = Volley.newRequestQueue(mContext)
            val apiKey = "9a458b2af24d091772fa6fc0fb52c1d9"
            val url = "https://api.openweathermap.org/data/2.5/forecast?q=${weather.getCity()}&appid=${apiKey}"
            val jsonObjectRequest2 = JsonObjectRequest(
                Request.Method.GET, url, null, { response ->
                    val listObject = response.getJSONArray("list").getJSONObject(position)

                    val clouds = listObject.getJSONObject("clouds").getInt("all")
                    val visibility = listObject.getString("visibility")
                    val humidity = listObject.getJSONObject("main").getDouble("humidity")
                    val tempMin = listObject.getJSONObject("main").getDouble("temp_min")
                    val tempMax = listObject.getJSONObject("main").getDouble("temp_max")
                    val windSpeed = listObject.getJSONObject("wind").getDouble("speed")
                    val windDegree = listObject.getJSONObject("wind").getDouble("deg")
                    val time = listObject.getString("dt_txt")

                    val dialog = Dialog(mContext)
                    dialog.setContentView(R.layout.weather_info)
                    dialog.setCancelable(true)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.window?.setGravity(Gravity.CENTER)
                    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.findViewById<TextView>(R.id.weatherInfoCityName).text = weather.getCity()
                    dialog.findViewById<TextView>(R.id.weatherInfoTemperature).text = weather.getTemp()
                    dialog.findViewById<TextView>(R.id.weatherInfoWeatherDescription).text = weather.getWeatherDescription()
                    dialog.findViewById<TextView>(R.id.weatherInfoClouds).text = "$clouds%"
                    dialog.findViewById<TextView>(R.id.weatherInfoVisibility).text = "$visibility m"
                    dialog.findViewById<TextView>(R.id.weatherInfoHumidity).text = "${humidity}%"
                    dialog.findViewById<TextView>(R.id.weatherInfoTempMin).text = "Min: ${((tempMin - 273.15) * 100.0).roundToLong() / 100.0}°C - $tempMin°F"
                    dialog.findViewById<TextView>(R.id.weatherInfoTempMax).text = "Max: ${((tempMax - 273.15) * 100.0).roundToLong() / 100.0}°C - $tempMax°F"
                    dialog.findViewById<TextView>(R.id.weatherInfoWindSpeed).text = "Spd: $windSpeed m/s"
                    dialog.findViewById<TextView>(R.id.weatherInfoWindDegree).text = "Deg: ${windDegree}°F"
                    dialog.findViewById<TextView>(R.id.weatherInfoTime).text = time

                    when (listObject.getJSONArray("weather").getJSONObject(0).getString("main")) {
                        "Thunderstorm" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.thunderstorm)
                        "Drizzle" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.drizzle)
                        "Rain" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.rain)
                        "Snow" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.snow)
                        "Mist" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.mist)
                        "Smoke" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.smoke)
                        "Haze" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.haze)
                        "Dust" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.dust)
                        "Fog" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.fog)
                        "Sand" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.sand)
                        "Ash" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.ash)
                        "Squall" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.squall)
                        "Tornado" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.tornado)
                        "Clear" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.clear)
                        "Clouds" -> dialog.findViewById<LinearLayout>(R.id.weatherInfoLayout).setBackgroundResource(R.drawable.clouds)
                    }

                    dialog.show()
                    loadingDialog.dismiss()
                }, { error ->
                    val errorDialog = AlertDialog.Builder(mContext)
                    errorDialog.setTitle("Error")
                    errorDialog.setMessage("Something went wrong. Please try again later.\n\n${error.message}")
                    errorDialog.setPositiveButton("OK") { _, _ ->
                        errorDialog.create().dismiss()
                    }.show()
                }
            )
            queue.add(jsonObjectRequest2)
        }
    }

    override fun getItemCount(): Int {
        return mWeathers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weatherItemCityName: TextView = itemView.findViewById(R.id.weatherItemCityName)
        val weatherItemTime: TextView = itemView.findViewById(R.id.weatherItemTime)
        val weatherItemTemperature: TextView = itemView.findViewById(R.id.weatherItemTemperature)
        val weatherItemWeatherDescription: TextView = itemView.findViewById(R.id.weatherItemWeatherDescription)
        val weatherItemLayout: ConstraintLayout = itemView.findViewById(R.id.weatherItemLayout)
        val weatherItemWeatherIcon: TextView = itemView.findViewById(R.id.weatherItemWeatherIcon)
        val weatherItemCardView: CardView = itemView.findViewById(R.id.weatherItemCardView)
    }
}
