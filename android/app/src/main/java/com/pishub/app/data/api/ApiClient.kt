package com.pishub.app.data.api

import com.pishub.app.data.model.NewsArticle
import com.pishub.app.data.model.WeatherData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // API Keys - Replace with your actual keys
    const val WEATHER_API_KEY = "YOUR_OPENWEATHERMAP_API_KEY"
    const val NEWS_API_KEY = "YOUR_NEWSAPI_KEY"
    
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val NEWS_BASE_URL = "https://newsapi.org/v2/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val weatherRetrofit = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val newsRetrofit = Retrofit.Builder()
        .baseUrl(NEWS_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherApiService: WeatherApiService = weatherRetrofit.create(WeatherApiService::class.java)
    val newsApiService: NewsApiService = newsRetrofit.create(NewsApiService::class.java)

    suspend fun getWeather(language: String = "es"): Result<WeatherData> {
        return try {
            val response = weatherApiService.getWeather(
                city = "El Progreso,HN",
                apiKey = WEATHER_API_KEY,
                language = language
            )
            
            val weatherData = WeatherData(
                temperature = response.main.temp,
                feelsLike = response.main.feels_like,
                humidity = response.main.humidity,
                description = response.weather.firstOrNull()?.description ?: "",
                icon = response.weather.firstOrNull()?.icon ?: "",
                cityName = response.name,
                windSpeed = response.wind.speed
            )
            
            Result.success(weatherData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNews(language: String = "es"): Result<List<NewsArticle>> {
        return try {
            val response = newsApiService.getNews(
                query = "El Progreso Honduras",
                apiKey = NEWS_API_KEY,
                language = language
            )
            
            val articles = response.articles.take(10).map { article ->
                NewsArticle(
                    title = article.title,
                    description = article.description,
                    url = article.url,
                    imageUrl = article.urlToImage,
                    source = article.source.name,
                    publishedAt = article.publishedAt
                )
            }
            
            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
