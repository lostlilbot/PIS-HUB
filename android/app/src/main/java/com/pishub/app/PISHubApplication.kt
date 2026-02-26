package com.pishub.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.pishub.app.data.local.PISHubDatabase
import com.pishub.app.data.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PISHubApplication : Application(), Configuration.Provider {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Database
    lateinit var database: PISHubDatabase
        private set

    // Repositories
    lateinit var userRepository: UserRepository
        private set
    lateinit var authRepository: AuthRepository
        private set
    lateinit var attendanceRepository: AttendanceRepository
        private set
    lateinit var gradeRepository: GradeRepository
        private set
    lateinit var assignmentRepository: AssignmentRepository
        private set
    lateinit var communicationRepository: CommunicationRepository
        private set
    lateinit var calendarRepository: CalendarRepository
        private set
    lateinit var libraryRepository: LibraryRepository
        private set
    lateinit var emergencyRepository: EmergencyRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Enable Firebase persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        
        // Subscribe to topic for push notifications
        applicationScope.launch {
            try {
                FirebaseMessaging.getInstance().subscribeToTopic("pishub_notifications")
            } catch (e: Exception) {
                // Handle exception silently
            }
        }

        // Initialize Room Database
        database = PISHubDatabase.getInstance(this)

        // Initialize Repositories
        userRepository = UserRepository(database.userDao())
        authRepository = AuthRepository(
            FirebaseAuth.getInstance(),
            database.userDao(),
            this
        )
        attendanceRepository = AttendanceRepository(
            database.attendanceDao(),
            FirebaseDatabase.getInstance().reference.child("attendance")
        )
        gradeRepository = GradeRepository(
            database.gradeDao(),
            FirebaseDatabase.getInstance().reference.child("grades")
        )
        assignmentRepository = AssignmentRepository(
            database.assignmentDao(),
            FirebaseDatabase.getInstance().reference.child("assignments")
        )
        communicationRepository = CommunicationRepository(
            database.messageDao(),
            database.announcementDao(),
            FirebaseDatabase.getInstance().reference.child("messages"),
            FirebaseDatabase.getInstance().reference.child("announcements")
        )
        calendarRepository = CalendarRepository(
            database.eventDao(),
            FirebaseDatabase.getInstance().reference.child("events")
        )
        libraryRepository = LibraryRepository(
            database.libraryDao(),
            FirebaseDatabase.getInstance().reference.child("library")
        )
        emergencyRepository = EmergencyRepository(
            database.emergencyLessonDao(),
            FirebaseDatabase.getInstance().reference.child("emergency_lessons")
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
