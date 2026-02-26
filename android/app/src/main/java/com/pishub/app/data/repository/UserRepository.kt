package com.pishub.app.data.repository

import com.pishub.app.data.local.UserDao
import com.pishub.app.data.model.User
import com.pishub.app.data.model.UserRole
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }

    fun observeUser(userId: String): Flow<User?> {
        return userDao.observeUser(userId)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    fun getUsersByRole(role: UserRole): Flow<List<User>> {
        return userDao.getUsersByRole(role)
    }

    suspend fun getUsersByRoleSync(role: UserRole): List<User> {
        return userDao.getUsersByRoleSync(role)
    }

    suspend fun getParentByChildId(childId: String): User? {
        return userDao.getParentByChildId(childId)
    }

    fun getChildrenByParentId(parentId: String): Flow<List<User>> {
        return userDao.getChildrenByParentId(parentId)
    }

    fun getAllStaffAndStudents(): Flow<List<User>> {
        return userDao.getAllStaffAndStudents()
    }

    suspend fun getAllStaffAndStudentsSync(): List<User> {
        return userDao.getAllStaffAndStudentsSync()
    }

    fun searchUsers(query: String): Flow<List<User>> {
        return userDao.searchUsers(query)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun insertUsers(users: List<User>) {
        userDao.insertUsers(users)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun linkChildToParent(parentId: String, childId: String) {
        val parent = userDao.getUserById(parentId) ?: return
        val updatedChildIds = parent.childIds.toMutableList().apply { add(childId) }
        userDao.updateUser(parent.copy(childIds = updatedChildIds, updatedAt = System.currentTimeMillis()))

        val child = userDao.getUserById(childId) ?: return
        userDao.updateUser(child.copy(parentId = parentId, updatedAt = System.currentTimeMillis()))
    }
}
