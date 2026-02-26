package com.pishub.app.data.repository

import com.google.firebase.database.DatabaseReference
import com.pishub.app.data.local.MessageDao
import com.pishub.app.data.model.Announcement
import com.pishub.app.data.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class CommunicationRepository(
    private val messageDao: MessageDao,
    private val announcementDao: MessageDao,
    private val messagesRef: DatabaseReference,
    private val announcementsRef: DatabaseReference
) {
    // Message methods
    fun getMessagesByUser(userId: String): Flow<List<Message>> {
        return messageDao.getMessagesByUser(userId)
    }

    fun getConversation(userId1: String, userId2: String): Flow<List<Message>> {
        return messageDao.getConversation(userId1, userId2)
    }

    fun getUnreadMessages(userId: String): Flow<List<Message>> {
        return messageDao.getUnreadMessages(userId)
    }

    fun getUnreadCount(userId: String): Flow<Int> {
        return messageDao.getUnreadCount(userId)
    }

    suspend fun sendMessage(message: Message): Result<Unit> {
        return try {
            messageDao.insertMessage(message.copy(isSynced = false))
            
            try {
                messagesRef.child(message.id).setValue(message.copy(isSynced = true)).await()
                messageDao.updateMessage(message.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsRead(messageId: String): Result<Unit> {
        val message = messageDao.getMessageById(messageId) ?: return Result.failure(Exception("Message not found"))
        return try {
            messageDao.updateMessage(message.copy(isRead = true))
            
            try {
                messagesRef.child(messageId).child("isRead").setValue(true).await()
            } catch (e: Exception) {
                // Continue
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Announcement methods
    fun getAllAnnouncements(): Flow<List<Announcement>> {
        return announcementDao.getAllAnnouncements()
    }

    fun getRecentAnnouncements(limit: Int = 5): Flow<List<Announcement>> {
        return announcementDao.getRecentAnnouncements(limit)
    }

    fun getPriorityAnnouncements(): Flow<List<Announcement>> {
        return announcementDao.getPriorityAnnouncements()
    }

    suspend fun createAnnouncement(announcement: Announcement): Result<Unit> {
        return try {
            announcementDao.insertAnnouncement(announcement.copy(isSynced = false))
            
            try {
                announcementsRef.child(announcement.id).setValue(announcement.copy(isSynced = true)).await()
                announcementDao.updateAnnouncement(announcement.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAnnouncement(announcement: Announcement): Result<Unit> {
        return try {
            announcementDao.deleteAnnouncement(announcement)
            
            try {
                announcementsRef.child(announcement.id).removeValue().await()
            } catch (e: Exception) {
                // Continue
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncFromFirebase() {
        try {
            // Sync messages
            val messagesSnapshot = messagesRef.get().await()
            val messages = mutableListOf<Message>()
            for (child in messagesSnapshot.children) {
                val message = child.getValue(Message::class.java)
                if (message != null) {
                    messages.add(message.copy(isSynced = true))
                }
            }
            if (messages.isNotEmpty()) {
                messageDao.insertMessages(messages)
            }

            // Sync announcements
            val announcementsSnapshot = announcementsRef.get().await()
            val announcements = mutableListOf<Announcement>()
            for (child in announcementsSnapshot.children) {
                val announcement = child.getValue(Announcement::class.java)
                if (announcement != null) {
                    announcements.add(announcement.copy(isSynced = true))
                }
            }
            if (announcements.isNotEmpty()) {
                announcementDao.insertAnnouncements(announcements)
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }
}
