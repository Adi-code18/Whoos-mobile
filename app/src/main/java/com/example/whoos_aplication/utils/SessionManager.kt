package com.example.whoos_aplication.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("whoosh_prefs", Context.MODE_PRIVATE)

    fun saveAuthToken(email: String, username: String, userId: Int) {
        prefs.edit().apply {
            putString("email", email)
            putString("username", username)
            putInt("user_id", userId)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getEmail(): String? = prefs.getString("email", null)
    fun getUsername(): String? = prefs.getString("username", "User")
    fun getUserId(): Int = prefs.getInt("user_id", 0)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
