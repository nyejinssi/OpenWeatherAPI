package com.ilsa1000ri.openweather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject // 추가

object OpenApi {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    private const val API_KEY = "당신의 API"
    private const val LAT = "35.1541"
    private const val LON = "128.0985"

    suspend fun getOpenIndex(): Double? { // 반환 타입 수정
        return withContext(Dispatchers.IO) {
            val urlBuilder = StringBuilder(OpenApi.BASE_URL) /* URL */
            urlBuilder.append("?lat=${OpenApi.LAT}")
            urlBuilder.append("&lon=${OpenApi.LON}")
            urlBuilder.append("&appid=${OpenApi.API_KEY}")

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

                // JSON 파싱하여 "feels_like" 값 추출
                val jsonObject = JSONObject(response)
                val mainObject = jsonObject.getJSONObject("main")
                mainObject.optDouble("feels_like") // "feels_like" 값 반환
            } else {
                // Error handling if necessary
                null
            }
        }
    }
}
