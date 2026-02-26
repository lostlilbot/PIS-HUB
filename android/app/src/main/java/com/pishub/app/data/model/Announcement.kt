package com.pishub.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AnnouncementPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
    val targetAudience: List<String>, // Student IDs, class IDs, or "all"
    val attachments: List<String> = emptyList(), // URLs
    val isPinned: Boolean = false,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null // Optional expiration
)
