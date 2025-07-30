package com.example.hypercars.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hypercars.model.UserModel
import com.example.hypercars.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repo: UserRepository) : ViewModel() {

    fun login(
        email: String, password: String,
        callback: (Boolean, String, FirebaseUser?) -> Unit
    ) {
        repo.login(email, password, callback)
    }

    // Authentication function
    fun register(
        email: String, password: String,
        callback: (Boolean, String, FirebaseUser?) -> Unit
    ) {
        repo.register(email, password, callback)
    }

    // Add user to database
    fun addUserToDatabase(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addUserToDatabase(userId, model, callback)
    }

    fun updateProfile(
        userId: String, data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateProfile(userId, data, callback)
    }

    fun forgetPassword(
        email: String, callback: (Boolean, String) -> Unit
    ) {
        repo.forgetPassword(email, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    private val _users = MutableLiveData<UserModel?>()
    val users: LiveData<UserModel?> get() = _users

    fun getUserByID(userId: String) {
        repo.getUserByID(userId) { user, success, message ->
            if (success && user != null) {
                _users.postValue(user)
            } else {
                _users.postValue(null)
            }
        }
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }
}
