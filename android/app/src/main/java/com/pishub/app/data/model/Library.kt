package com.pishub.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LibraryCategory {
    TEXTBOOK,
    WORKBOOK,
    REFERENCE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    OTHER
}

@Entity(tableName = "library_items")
data class LibraryItem(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val category: LibraryCategory,
    val subjectId: String?,
    val subjectName: String?,
    val gradeLevel: Int?, // For students
    val fileUrl: String,
    val thumbnailUrl: String? = null,
    val fileType: String, // "pdf", "doc", "video", "audio", "image"
    val fileSize: Long,
    val uploadedBy: String,
    val uploadedByName: String,
    val isVisibleToStudents: Boolean = true,
    val isVisibleToParents: Boolean = true,
    val isVisibleToTeachers: Boolean = true,
    val isSynced: Boolean = false,
    val viewCount: Int = 0,
    val downloadCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "emergency_lessons")
data class EmergencyLesson(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val classId: String,
    val className: String,
    val subjectId: String,
    val subjectName: String,
    val teacherId: String,
    val teacherName: String,
    val fileUrls: List<String>,
    val notified: Boolean = false,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
