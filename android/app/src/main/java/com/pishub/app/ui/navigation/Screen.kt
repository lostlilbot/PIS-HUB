package com.pishub.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // Auth
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")
    
    // Main
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Attendance : Screen("attendance", "Attendance", Icons.Default.CheckCircle)
    object Grades : Screen("grades", "Grades", Icons.Default.Grade)
    object Assignments : Screen("assignments", "Assignments", Icons.Default.Assignment)
    object Communication : Screen("communication", "Communication", Icons.Default.Chat)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
    // Additional
    object Library : Screen("library", "Library", Icons.Default.LibraryBooks)
    object Directory : Screen("directory", "Directory", Icons.Default.People)
    object EmergencyLessons : Screen("emergency_lessons", "Emergency Lessons", Icons.Default.Warning)
    object Webcams : Screen("webcams", "Webcams", Icons.Default.Videocam)
    object Chat : Screen("chat/{userId}", "Chat")
    object AnnouncementDetail : Screen("announcement/{announcementId}", "Announcement")
    object AssignmentDetail : Screen("assignment/{assignmentId}", "Assignment")
    object UserProfile : Screen("user/{userId}", "User Profile")
    object EditProfile : Screen("edit_profile", "Edit Profile")
}

object NavRoutes {
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Attendance,
        Screen.Grades,
        Screen.Assignments,
        Screen.Communication,
        Screen.Profile
    )

    val adminNavItems = listOf(
        Screen.Directory,
        Screen.Library,
        Screen.EmergencyLessons,
        Screen.Webcams
    )

    val teacherNavItems = listOf(
        Screen.Library,
        Screen.EmergencyLessons
    )
}

fun getStartDestination(isLoggedIn: Boolean): String {
    return if (isLoggedIn) Screen.Home.route else Screen.Login.route
}
