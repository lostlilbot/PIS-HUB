package com.pishub.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    ADMIN,
    TEACHER,
    PARENT,
    STUDENT
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val displayName: String,
    val role: UserRole,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val childIds: List<String> = emptyList(), // For parents
    val parentId: String? = null, // For students
    val studentId: String? = null, // Student ID number
    val grade: Int? = null, // For students
    val section: String? = null, // For students
    val department: String? = null, // For teachers
    val healthInfo: String? = null,
    val emergencyContact: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class UserProfile(
    val user: User,
    val children: List<User> = emptyList(),
    val profilePhotoUrl: String? = null
)
