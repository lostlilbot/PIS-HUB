package com.pishub.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderPhotoUrl: String? = null,
    val receiverId: String,
    val receiverName: String,
    val content: String,
    val isRead: Boolean = false,
    val attachments: List<String> = emptyList(),
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val targetRoles: List<UserRole> = emptyList(), // Who can see this
    val targetClasses: List<String> = emptyList(), // Which classes
    val attachments: List<String> = emptyList(),
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null
)

enum class AnnouncementPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

data class Conversation(
    val participantId: String,
    val participantName: String,
    val participantPhotoUrl: String? = null,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0
)
