package com.example.whoos_aplication.utils

import android.util.Log

object EmailLogger {
    private const val TAG = "📧 WHOOSH_EMAIL"
    
    fun logPurchaseEmail(userEmail: String, bookingCode: String) {
        Log.i(TAG, """
            🎫 PURCHASE EMAIL SENT
            ═══════════════════════════════════════
            📧 Recipient: $userEmail
            🎫 Booking Code: $bookingCode
            📅 Timestamp: ${getCurrentTimestamp()}
            ✅ Status: Email berhasil dikirim ke Gmail user
            ═══════════════════════════════════════
        """.trimIndent())
    }
    
    fun logRescheduleEmail(userEmail: String, bookingCode: String, newDate: String) {
        Log.i(TAG, """
            🔄 RESCHEDULE EMAIL SENT
            ═══════════════════════════════════════
            📧 Recipient: $userEmail
            🎫 Booking Code: $bookingCode
            📅 New Date: $newDate
            📅 Timestamp: ${getCurrentTimestamp()}
            ✅ Status: Email reschedule berhasil dikirim
            ═══════════════════════════════════════
        """.trimIndent())
    }
    
    fun logRefundEmail(userEmail: String, bookingCode: String, refundAmount: String) {
        Log.i(TAG, """
            💰 REFUND EMAIL SENT
            ═══════════════════════════════════════
            📧 Recipient: $userEmail
            🎫 Booking Code: $bookingCode
            💵 Refund Amount: $refundAmount
            📅 Timestamp: ${getCurrentTimestamp()}
            ✅ Status: Email refund berhasil dikirim
            ═══════════════════════════════════════
        """.trimIndent())
    }
    
    fun logEmailError(emailType: String, userEmail: String, error: String) {
        Log.e(TAG, """
            ❌ EMAIL ERROR
            ═══════════════════════════════════════
            📧 Type: $emailType
            📧 Recipient: $userEmail
            ❌ Error: $error
            📅 Timestamp: ${getCurrentTimestamp()}
            ═══════════════════════════════════════
        """.trimIndent())
    }
    
    fun logUserSession(email: String?, username: String?, userId: Int) {
        Log.d(TAG, """
            👤 USER SESSION INFO
            ═══════════════════════════════════════
            📧 Email: ${email ?: "Not found"}
            👤 Username: ${username ?: "Not found"}
            🆔 User ID: $userId
            📅 Timestamp: ${getCurrentTimestamp()}
            ═══════════════════════════════════════
        """.trimIndent())
    }
    
    private fun getCurrentTimestamp(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
    }
}