package com.example.hypercars.repository

import android.telecom.Call
import com.example.hypercars.model.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.MutableData


interface UserRepository {
    // login
    //register
    //forget password
    //update profile
    //get current user
    //add user to database
    //logout
//    {
//        "success" : true,
//        "message" : "login successful"
//    }
    fun login(email: String,password: String,
              callback: (Boolean, String)-> Unit)
    //authentication function
    fun register(email: String, password: String,
                 callback: (Boolean, String, String) -> Unit)
    //database function
    fun addUserToDatabase(userId: String, model: UserModel,
                          callback: (Boolean, String) -> Unit)

    fun updateProfile(userId: String,data : MutableMap<String,Any?>,
                      callback: (Boolean, String) -> Unit)

    fun forgetPassword(
        email : String,
        callback: (Boolean, String) -> Unit
    )
    fun getCurrentUser() : FirebaseUser?

    fun getUserById(userId: String, callback: (UserModel?,Boolean, String) -> Unit)


    fun logout(callback: (Boolean, String) -> Unit)

}