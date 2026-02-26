package com.pishub.app.data.model

data class WeatherData(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val description: String,
    val icon: String,
    val cityName: String,
    val windSpeed: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class NewsArticle(
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val source: String,
    val publishedAt: String
)

data class DashboardData(
    val weather: WeatherData?,
    val news: List<NewsArticle>,
    val upcomingEvents: List<Event>,
    val recentAnnouncements: List<Announcement>,
    val pinnedAssignments: List<Assignment>
)

data class Webcam(
    val id: String,
    val name: String,
    val location: String,
    val streamUrl: String,
    val isActive: Boolean = true
)

data class AppSettings(
    val darkMode: Boolean = false,
    val language: String = "en", // "en" or "es"
    val notificationsEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val lastSyncTime: Long = 0
)
