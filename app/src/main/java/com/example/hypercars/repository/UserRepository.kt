package com.example.hypercars.repository

import com.example.hypercars.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepository {


    // Login: returns success flag, message, and FirebaseUser
    fun login(
        email: String,
        password: String,
        callback: (Boolean, String, FirebaseUser?) -> Unit
    )

    // Register: returns success flag, message, and FirebaseUser
    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, FirebaseUser?) -> Unit
    )

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    fun updateProfile(
        userID: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    )

    fun getCurrentUser(): FirebaseUser?

    fun addUserToDatabase(
        userID: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun getUserByID(
        userID: String,
        callback: (UserModel?, Boolean, String) -> Unit
    )

    fun logout(callback: (Boolean, String) -> Unit)
}