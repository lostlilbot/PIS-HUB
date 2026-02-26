package com.pishub.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.pishub.app.data.model.*

@Database(
    entities = [
        User::class,
        Attendance::class,
        Grade::class,
        Assignment::class,
        AssignmentSubmission::class,
        Message::class,
        Announcement::class,
        Event::class,
        Schedule::class,
        LibraryItem::class,
        EmergencyLesson::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PISHubDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun gradeDao(): GradeDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun messageDao(): MessageDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun eventDao(): EventDao
    abstract fun libraryDao(): LibraryDao
    abstract fun emergencyLessonDao(): EmergencyLessonDao

    companion object {
        @Volatile
        private var INSTANCE: PISHubDatabase? = null

        fun getInstance(context: Context): PISHubDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PISHubDatabase::class.java,
                    "pishub_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.valueOf(value)

    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus): String = status.name

    @TypeConverter
    fun toAttendanceStatus(value: String): AttendanceStatus = AttendanceStatus.valueOf(value)

    @TypeConverter
    fun fromAssignmentStatus(status: AssignmentStatus): String = status.name

    @TypeConverter
    fun toAssignmentStatus(value: String): AssignmentStatus = AssignmentStatus.valueOf(value)

    @TypeConverter
    fun fromAnnouncementPriority(priority: AnnouncementPriority): String = priority.name

    @TypeConverter
    fun toAnnouncementPriority(value: String): AnnouncementPriority = AnnouncementPriority.valueOf(value)

    @TypeConverter
    fun fromLibraryCategory(category: LibraryCategory): String = category.name

    @TypeConverter
    fun toLibraryCategory(value: String): LibraryCategory = LibraryCategory.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String): List<String> = 
        if (value.isEmpty()) emptyList() else value.split(",")
}
