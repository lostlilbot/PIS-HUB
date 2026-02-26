package com.pishub.app.data.local

import androidx.room.*
import com.pishub.app.data.model.User
import com.pishub.app.data.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = :role")
    suspend fun getUsersByRoleSync(role: UserRole): List<User>

    @Query("SELECT * FROM users WHERE childIds LIKE '%' || :childId || '%'")
    suspend fun getParentByChildId(childId: String): User?

    @Query("SELECT * FROM users WHERE parentId = :parentId")
    fun getChildrenByParentId(parentId: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = 'STUDENT' OR role = 'TEACHER' OR role = 'ADMIN'")
    fun getAllStaffAndStudents(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = 'STUDENT' OR role = 'TEACHER' OR role = 'ADMIN'")
    suspend fun getAllStaffAndStudentsSync(): List<User>

    @Query("SELECT * FROM users WHERE displayName LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
