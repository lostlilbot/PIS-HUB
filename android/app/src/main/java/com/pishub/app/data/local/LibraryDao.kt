package com.pishub.app.data.local

import androidx.room.*
import com.pishub.app.data.model.EmergencyLesson
import com.pishub.app.data.model.LibraryCategory
import com.pishub.app.data.model.LibraryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    // Library Item queries
    @Query("SELECT * FROM library_items WHERE id = :id")
    suspend fun getLibraryItemById(id: String): LibraryItem?

    @Query("SELECT * FROM library_items ORDER BY createdAt DESC")
    fun getAllLibraryItems(): Flow<List<LibraryItem>>

    @Query("SELECT * FROM library_items WHERE category = :category ORDER BY createdAt DESC")
    fun getLibraryItemsByCategory(category: LibraryCategory): Flow<List<LibraryItem>>

    @Query("SELECT * FROM library_items WHERE subjectId = :subjectId ORDER BY createdAt DESC")
    fun getLibraryItemsBySubject(subjectId: String): Flow<List<LibraryItem>>

    @Query("SELECT * FROM library_items WHERE gradeLevel = :gradeLevel ORDER BY createdAt DESC")
    fun getLibraryItemsByGrade(gradeLevel: Int): Flow<List<LibraryItem>>

    @Query("SELECT * FROM library_items WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchLibraryItems(query: String): Flow<List<LibraryItem>>

    @Query("SELECT * FROM library_items WHERE isSynced = 0")
    suspend fun getUnsyncedLibraryItems(): List<LibraryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibraryItem(item: LibraryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibraryItems(items: List<LibraryItem>)

    @Update
    suspend fun updateLibraryItem(item: LibraryItem)

    @Delete
    suspend fun deleteLibraryItem(item: LibraryItem)

    @Query("DELETE FROM library_items")
    suspend fun deleteAllLibraryItems()

    // Emergency Lesson queries
    @Query("SELECT * FROM emergency_lessons WHERE id = :id")
    suspend fun getEmergencyLessonById(id: String): EmergencyLesson?

    @Query("SELECT * FROM emergency_lessons WHERE classId = :classId ORDER BY createdAt DESC")
    fun getEmergencyLessonsByClass(classId: String): Flow<List<EmergencyLesson>>

    @Query("SELECT * FROM emergency_lessons ORDER BY createdAt DESC")
    fun getAllEmergencyLessons(): Flow<List<EmergencyLesson>>

    @Query("SELECT * FROM emergency_lessons WHERE isSynced = 0")
    suspend fun getUnsyncedEmergencyLessons(): List<EmergencyLesson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyLesson(lesson: EmergencyLesson)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyLessons(lessons: List<EmergencyLesson>)

    @Update
    suspend fun updateEmergencyLesson(lesson: EmergencyLesson)

    @Delete
    suspend fun deleteEmergencyLesson(lesson: EmergencyLesson)

    @Query("DELETE FROM emergency_lessons")
    suspend fun deleteAllEmergencyLessons()
}
