package com.example.hypercars.repository

import com.example.hypercars.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class UserRepositoryImpl : UserRepository{


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("users")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String, FirebaseUser?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login successful", auth.currentUser)
                } else {
                    callback(false, task.exception?.message ?: "Login failed", null)
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, FirebaseUser?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Registration successful", auth.currentUser)
                } else {
                    callback(false, task.exception?.message ?: "Registration failed", null)
                }
            }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Password reset email sent")
                } else {
                    callback(false, task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    override fun updateProfile(
        userID: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        db.child(userID).updateChildren(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Profile updated")
                } else {
                    callback(false, task.exception?.message ?: "Update failed")
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override fun addUserToDatabase(
        userID: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        db.child(userID).setValue(model)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "User added to database")
                } else {
                    callback(false, task.exception?.message ?: "Failed to add user")
                }
            }
    }

    override fun getUserByID(
        userID: String,
        callback: (UserModel?, Boolean, String) -> Unit
    ) {
        db.child(userID).get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(UserModel::class.java)
            callback(user, true, "User fetched")
        }.addOnFailureListener {
            callback(null, false, it.message ?: "Error fetching user")
        }
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logged out successfully")
        } catch (e: Exception) {
            callback(false, e.message ?: "Logout failed")
        }
    }
}