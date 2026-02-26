package com.pishub.app.data.local

import androidx.room.*
import com.pishub.app.data.model.Announcement
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements WHERE id = :id")
    suspend fun getAnnouncementById(id: String): Announcement?

    @Query("SELECT * FROM announcements ORDER BY isPinned DESC, createdAt DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements ORDER BY isPinned DESC, createdAt DESC LIMIT :limit")
    fun getRecentAnnouncements(limit: Int): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements WHERE priority = 'URGENT' OR priority = 'HIGH' ORDER BY isPinned DESC, createdAt DESC")
    fun getPriorityAnnouncements(): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements WHERE isPinned = 1 ORDER BY createdAt DESC")
    fun getPinnedAnnouncements(): Flow<List<Announcement>>

    @Query("SELECT * FROM announcements WHERE expiresAt IS NULL OR expiresAt > :currentTime ORDER BY isPinned DESC, createdAt DESC")
    fun getActiveAnnouncements(currentTime: Long): Flow<List<Announcement>>

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
