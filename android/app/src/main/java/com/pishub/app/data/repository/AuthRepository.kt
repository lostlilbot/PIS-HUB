package com.pishub.app.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.pishub.app.data.local.UserDao
import com.pishub.app.data.model.User
import com.pishub.app.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao,
    private val context: Context
) {
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: Flow<FirebaseUser?> = _currentUser.asStateFlow()

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    init {
        _currentUser.value = firebaseAuth.currentUser
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("User not found")
            
            // Get user from database
            val user = userDao.getUserById(firebaseUser.uid)
                ?: throw Exception("User profile not found")
            
            saveUserSession(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("User not found")
            
            // Check if user exists in database, create if not
            var user = userDao.getUserById(firebaseUser.uid)
            if (user == null) {
                user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "User",
                    role = UserRole.STUDENT, // Default role, admin should update
                    photoUrl = firebaseUser.photoUrl?.toString()
                )
                userDao.insertUser(user)
            }
            
            saveUserSession(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserWithEmail(email: String, password: String, displayName: String, role: UserRole): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("User not found")
            
            val user = User(
                id = firebaseUser.uid,
                email = email,
                displayName = displayName,
                role = role
            )
            userDao.insertUser(user)
            
            saveUserSession(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
        clearUserSession()
    }

    fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean("biometric_enabled", false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }

    fun saveCredentials(email: String, password: String) {
        encryptedPrefs.edit()
            .putString("saved_email", email)
            .putString("saved_password", password)
            .apply()
    }

    fun getSavedCredentials(): Pair<String, String>? {
        val email = encryptedPrefs.getString("saved_email", null)
        val password = encryptedPrefs.getString("saved_password", null)
        return if (email != null && password != null) Pair(email, password) else null
    }

    private fun saveUserSession(user: User) {
        encryptedPrefs.edit()
            .putString("user_id", user.id)
            .putString("user_role", user.role.name)
            .apply()
    }

    private fun clearUserSession() {
        encryptedPrefs.edit().clear().apply()
    }

    fun getSavedUserId(): String? {
        return encryptedPrefs.getString("user_id", null)
    }

    fun getSavedUserRole(): UserRole? {
        val roleStr = encryptedPrefs.getString("user_role", null)
        return roleStr?.let { UserRole.valueOf(it) }
    }

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
