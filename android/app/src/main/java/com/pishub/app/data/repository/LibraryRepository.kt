package com.pishub.app.data.repository

import com.google.firebase.database.DatabaseReference
import com.pishub.app.data.local.LibraryDao
import com.pishub.app.data.model.EmergencyLesson
import com.pishub.app.data.model.LibraryCategory
import com.pishub.app.data.model.LibraryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class LibraryRepository(
    private val libraryDao: LibraryDao,
    private val firebaseRef: DatabaseReference
) {
    // Library Item methods
    fun getAllLibraryItems(): Flow<List<LibraryItem>> {
        return libraryDao.getAllLibraryItems()
    }

    fun getLibraryItemsByCategory(category: LibraryCategory): Flow<List<LibraryItem>> {
        return libraryDao.getLibraryItemsByCategory(category)
    }

    fun getLibraryItemsBySubject(subjectId: String): Flow<List<LibraryItem>> {
        return libraryDao.getLibraryItemsBySubject(subjectId)
    }

    fun getLibraryItemsByGrade(gradeLevel: Int): Flow<List<LibraryItem>> {
        return libraryDao.getLibraryItemsByGrade(gradeLevel)
    }

    fun searchLibraryItems(query: String): Flow<List<LibraryItem>> {
        return libraryDao.searchLibraryItems(query)
    }

    suspend fun getLibraryItemById(id: String): LibraryItem? {
        return libraryDao.getLibraryItemById(id)
    }

    suspend fun addLibraryItem(item: LibraryItem): Result<Unit> {
        return try {
            libraryDao.insertLibraryItem(item.copy(isSynced = false))
            
            try {
                firebaseRef.child("items").child(item.id).setValue(item.copy(isSynced = true)).await()
                libraryDao.updateLibraryItem(item.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLibraryItem(item: LibraryItem): Result<Unit> {
        return try {
            libraryDao.updateLibraryItem(item.copy(isSynced = false, updatedAt = System.currentTimeMillis()))
            
            try {
                firebaseRef.child("items").child(item.id).setValue(item.copy(isSynced = true)).await()
                libraryDao.updateLibraryItem(item.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLibraryItem(item: LibraryItem): Result<Unit> {
        return try {
            libraryDao.deleteLibraryItem(item)
            
            try {
                firebaseRef.child("items").child(item.id).removeValue().await()
            } catch (e: Exception) {
                // Continue
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Emergency Lesson methods
    fun getEmergencyLessonsByClass(classId: String): Flow<List<EmergencyLesson>> {
        return libraryDao.getEmergencyLessonsByClass(classId)
    }

    fun getAllEmergencyLessons(): Flow<List<EmergencyLesson>> {
        return libraryDao.getAllEmergencyLessons()
    }

    suspend fun getEmergencyLessonById(id: String): EmergencyLesson? {
        return libraryDao.getEmergencyLessonById(id)
    }

    suspend fun uploadEmergencyLesson(lesson: EmergencyLesson): Result<Unit> {
        return try {
            libraryDao.insertEmergencyLesson(lesson.copy(isSynced = false))
            
            try {
                firebaseRef.child("emergency").child(lesson.id).setValue(lesson.copy(isSynced = true)).await()
                libraryDao.updateEmergencyLesson(lesson.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep as unsynced
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEmergencyLesson(lesson: EmergencyLesson): Result<Unit> {
        return try {
            libraryDao.deleteEmergencyLesson(lesson)
            
            try {
                firebaseRef.child("emergency").child(lesson.id).removeValue().await()
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
            // Sync library items
            val itemsSnapshot = firebaseRef.child("items").get().await()
            val items = mutableListOf<LibraryItem>()
            for (child in itemsSnapshot.children) {
                val item = child.getValue(LibraryItem::class.java)
                if (item != null) {
                    items.add(item.copy(isSynced = true))
                }
            }
            if (items.isNotEmpty()) {
                libraryDao.insertLibraryItems(items)
            }

            // Sync emergency lessons
            val emergencySnapshot = firebaseRef.child("emergency").get().await()
            val lessons = mutableListOf<EmergencyLesson>()
            for (child in emergencySnapshot.children) {
                val lesson = child.getValue(EmergencyLesson::class.java)
                if (lesson != null) {
                    lessons.add(lesson.copy(isSynced = true))
                }
            }
            if (lessons.isNotEmpty()) {
                libraryDao.insertEmergencyLessons(lessons)
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }
}
