package com.pishub.app.data.repository

import com.google.firebase.database.DatabaseReference
import com.pishub.app.data.local.LibraryDao
import com.pishub.app.data.model.EmergencyLesson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class EmergencyRepository(
    private val emergencyLessonDao: LibraryDao,
    private val firebaseRef: DatabaseReference
) {
    fun getEmergencyLessonsByClass(classId: String): Flow<List<EmergencyLesson>> {
        return emergencyLessonDao.getEmergencyLessonsByClass(classId)
    }

    fun getAllEmergencyLessons(): Flow<List<EmergencyLesson>> {
        return emergencyLessonDao.getAllEmergencyLessons()
    }

    suspend fun getEmergencyLessonById(id: String): EmergencyLesson? {
        return emergencyLessonDao.getEmergencyLessonById(id)
    }

    suspend fun uploadEmergencyLesson(lesson: EmergencyLesson): Result<Unit> {
        return try {
            emergencyLessonDao.insertEmergencyLesson(lesson.copy(isSynced = false))
            
            try {
                firebaseRef.child(lesson.id).setValue(lesson.copy(isSynced = true)).await()
                emergencyLessonDao.updateEmergencyLesson(lesson.copy(isSynced = true, notified = true))
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
            emergencyLessonDao.deleteEmergencyLesson(lesson)
            
            try {
                firebaseRef.child(lesson.id).removeValue().await()
            } catch (e: Exception) {
                // Continue
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsNotified(lessonId: String) {
        val lesson = emergencyLessonDao.getEmergencyLessonById(lessonId) ?: return
        emergencyLessonDao.updateEmergencyLesson(lesson.copy(notified = true))
        
        try {
            firebaseRef.child(lessonId).child("notified").setValue(true).await()
        } catch (e: Exception) {
            // Continue
        }
    }

    suspend fun syncFromFirebase() {
        try {
            val snapshot = firebaseRef.get().await()
            val lessons = mutableListOf<EmergencyLesson>()
            
            for (child in snapshot.children) {
                val lesson = child.getValue(EmergencyLesson::class.java)
                if (lesson != null) {
                    lessons.add(lesson.copy(isSynced = true))
                }
            }
            
            if (lessons.isNotEmpty()) {
                emergencyLessonDao.insertEmergencyLessons(lessons)
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    suspend fun syncUnsyncedData() {
        val unsynced = emergencyLessonDao.getUnsyncedEmergencyLessons()
        for (lesson in unsynced) {
            try {
                firebaseRef.child(lesson.id).setValue(lesson.copy(isSynced = true)).await()
                emergencyLessonDao.updateEmergencyLesson(lesson.copy(isSynced = true))
            } catch (e: Exception) {
                // Continue with next
            }
        }
    }
}
