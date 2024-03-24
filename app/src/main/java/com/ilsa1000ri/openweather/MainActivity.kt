package com.ilsa1000ri.openweather

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 텍스트뷰 참조 초기화
        val txtOpenIndex = findViewById<TextView>(R.id.txtOpenIndex)
        val txtDays5Index = findViewById<TextView>(R.id.txtDays5Index)
        val txtDaysWeatherIndex = findViewById<TextView>(R.id.txtDaysWeatherIndex)

        GlobalScope.launch(Dispatchers.Main) {
            val openIndex = OpenApi.getOpenIndex()
            val days5Data = Days5Api.getDays5Data()

            openIndex?.let {
                val celsius = kelvinToCelsius(it)
                val df = DecimalFormat("#.#")
                val feelsLikeText = df.format(celsius)
                txtOpenIndex.text = feelsLikeText
            }
            days5Data?.let {
                val stringBuilder = StringBuilder()
                val weatherMap = mutableMapOf<String, MutableList<String>>() // 날짜를 키로, 해당 날짜의 날씨를 값으로 가지는 맵

                for ((dtTxt, weatherDescription) in it) {
                    val date = dtTxt.substring(0, 10)
                    if (!weatherMap.containsKey(date)) {
                        weatherMap[date] = mutableListOf()
                    }
                    weatherMap[date]?.add(weatherDescription)
                }

                for ((dtTxt, weatherDescription) in it) {
                    stringBuilder.append("Date/Time: $dtTxt\nWeather: $weatherDescription\n\n")
                }
                txtDays5Index.text = stringBuilder.toString()

                val weatherStringBuilder = StringBuilder()
                weatherMap.forEach { (date, weatherList) ->
                    val weather = when {
                        "Snow" in weatherList -> "눈"
                        "Rain" in weatherList -> "비"
                        "Clouds" in weatherList -> "구름"
                        else -> "맑음"
                    }
                    weatherStringBuilder.append("$date $weather\n")
                }

                txtDaysWeatherIndex.text = weatherStringBuilder.toString()
            }
        }
    }

    private fun kelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }
}
