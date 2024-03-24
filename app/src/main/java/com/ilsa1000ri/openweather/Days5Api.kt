package com.ilsa1000ri.openweather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object Days5Api {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/forecast"
    private const val API_KEY = "당신의 API"
    private const val LAT = "35.1541"
    private const val LON = "128.0985"

    suspend fun getDays5Data(): List<Pair<String, String>> { // 반환 타입 수정
        return withContext(Dispatchers.IO) {
            val urlBuilder = StringBuilder(Days5Api.BASE_URL) /* URL */
            urlBuilder.append("?lat=${Days5Api.LAT}")
            urlBuilder.append("&lon=${Days5Api.LON}")
            urlBuilder.append("&appid=${Days5Api.API_KEY}")

            val url = URL(urlBuilder.toString())
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Content-type", "application/json")

            val responseCode = conn.responseCode
            if (responseCode >= 200 && responseCode <= 300) {
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                val response = br.use { it.readText() } // JSON 문자열 전체를 읽어옴
                br.close()
                conn.disconnect()

                val jsonObject = JSONObject(response)
                val listArray = jsonObject.getJSONArray("list")

                val dataList = mutableListOf<Pair<String, String>>()

                for (i in 0 until listArray.length()) {
                    val listItem = listArray.getJSONObject(i)
                    val dt_txt = listItem.getString("dt_txt")
                    val weatherArray = listItem.getJSONArray("weather")
                    val weatherObj = weatherArray.getJSONObject(0)
                    val weatherDescription = weatherObj.getString("main")

                    val dataPair = Pair(dt_txt, weatherDescription)
                    dataList.add(dataPair)
                }

                dataList
            } else {
                // Error handling if necessary
                emptyList()
            }
        }
    }
}
