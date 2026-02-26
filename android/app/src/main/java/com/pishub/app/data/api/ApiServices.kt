package com.pishub.app.data.api

import retrofit2.http.GET
import retrofit2.http.Query

// OpenWeatherMap API
interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "es"
    ): WeatherResponse
}

data class WeatherResponse(
    val main: MainWeather,
    val weather: List<WeatherDescription>,
    val wind: Wind,
    val name: String
)

data class MainWeather(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int
)

data class WeatherDescription(
    val id: Int,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)

// NewsAPI
interface NewsApiService {
    @GET("everything")
    suspend fun getNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 10,
        @Query("language") language: String = "es"
    ): NewsResponse
}

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val source: Source,
    val publishedAt: String
)

data class Source(
    val name: String
)
