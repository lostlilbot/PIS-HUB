package com.pishub.app.data.local

import androidx.room.*
import com.pishub.app.data.model.Announcement
import com.pishub.app.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    // Message queries
    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: String): Message?

    @Query("SELECT * FROM messages WHERE senderId = :userId OR receiverId = :userId ORDER BY createdAt DESC")
    fun getMessagesByUser(userId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) ORDER BY createdAt ASC")
    fun getConversation(userId1: String, userId2: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE receiverId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadMessages(userId: String): Flow<List<Message>>

    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND isRead = 0")
    fun getUnreadCount(userId: String): Flow<Int>

    @Query("SELECT * FROM messages WHERE isSynced = 0")
    suspend fun getUnsyncedMessages(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Update
    suspend fun updateMessage(message: Message)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()

    // Announcement queries
    @Query("SELECT * FROM announcements WHERE id = :id")
    suspend fun getAnnouncementById(id: String): Announcement?

    @Query("SELECT * FROM announcements ORDER BY createdAt DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentAnnouncements(limit: Int): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements WHERE priority = 'URGENT' OR priority = 'HIGH' ORDER BY createdAt DESC")
    fun getPriorityAnnouncements(): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements WHERE isSynced = 0")
    suspend fun getUnsyncedAnnouncements(): List<Announcement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<Announcement>)

    @Update
    suspend fun updateAnnouncement(announcement: Announcement)

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)

    @Query("DELETE FROM announcements")
    suspend fun deleteAllAnnouncements()
}
