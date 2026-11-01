package com.example.whoos_aplication.utils

import android.content.Context
import android.util.Log

class EmailNotificationManager(private val context: Context) {
    
    private val emailSender = EmailSender()
    
    /**
     * Mengirim notifikasi email untuk pembelian tiket baru
     */
    fun sendPurchaseNotification(
        recipientEmail: String,
        bookingCode: String,
        passengerName: String,
        trainName: String,
        fromStation: String,
        toStation: String,
        departureDate: String,
        departureTime: String,
        arrivalTime: String,
        seatNumber: String,
        price: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val bookingInfo = EmailSender.BookingInfo(
            bookingCode = bookingCode,
            passengerName = passengerName,
            trainName = trainName,
            fromStation = fromStation,
            toStation = toStation,
            departureDate = departureDate,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
            seatNumber = seatNumber,
            price = price
        )
        
        emailSender.sendNotificationEmail(
            recipientEmail = recipientEmail,
            emailType = EmailSender.EmailType.PURCHASE,
            bookingInfo = bookingInfo
        ) { success, error ->
            Log.d("EmailNotification", "Purchase email sent: $success, error: $error")
            onResult(success, error)
        }
    }
    
    /**
     * Mengirim notifikasi email untuk reschedule tiket
     */
    fun sendRescheduleNotification(
        recipientEmail: String,
        bookingCode: String,
        passengerName: String,
        trainName: String,
        fromStation: String,
        toStation: String,
        oldDepartureDate: String,
        oldDepartureTime: String,
        newDepartureDate: String,
        newDepartureTime: String,
        newArrivalTime: String,
        seatNumber: String,
        price: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val bookingInfo = EmailSender.BookingInfo(
            bookingCode = bookingCode,
            passengerName = passengerName,
            trainName = trainName,
            fromStation = fromStation,
            toStation = toStation,
            departureDate = oldDepartureDate,
            departureTime = oldDepartureTime,
            arrivalTime = "", // Not needed for old schedule display
            seatNumber = seatNumber,
            price = price,
            newDepartureDate = newDepartureDate,
            newDepartureTime = newDepartureTime,
            newArrivalTime = newArrivalTime
        )
        
        emailSender.sendNotificationEmail(
            recipientEmail = recipientEmail,
            emailType = EmailSender.EmailType.RESCHEDULE,
            bookingInfo = bookingInfo
        ) { success, error ->
            Log.d("EmailNotification", "Reschedule email sent: $success, error: $error")
            onResult(success, error)
        }
    }
    
    /**
     * Mengirim notifikasi email untuk refund tiket
     */
    fun sendRefundNotification(
        recipientEmail: String,
        bookingCode: String,
        passengerName: String,
        trainName: String,
        fromStation: String,
        toStation: String,
        departureDate: String,
        departureTime: String,
        seatNumber: String,
        originalPrice: String,
        refundAmount: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val bookingInfo = EmailSender.BookingInfo(
            bookingCode = bookingCode,
            passengerName = passengerName,
            trainName = trainName,
            fromStation = fromStation,
            toStation = toStation,
            departureDate = departureDate,
            departureTime = departureTime,
            arrivalTime = "", // Not needed for refund
            seatNumber = seatNumber,
            price = originalPrice,
            refundAmount = refundAmount
        )
        
        emailSender.sendNotificationEmail(
            recipientEmail = recipientEmail,
            emailType = EmailSender.EmailType.REFUND,
            bookingInfo = bookingInfo
        ) { success, error ->
            Log.d("EmailNotification", "Refund email sent: $success, error: $error")
            onResult(success, error)
        }
    }
}

/**
 * Contoh penggunaan EmailNotificationManager
 */
object EmailNotificationExample {
    
    fun demonstratePurchaseEmail(context: Context) {
        val emailManager = EmailNotificationManager(context)
        
        emailManager.sendPurchaseNotification(
            recipientEmail = "customer@example.com",
            bookingCode = "WHH240609001",
            passengerName = "Budi Santoso",
            trainName = "WHOOSH 101",
            fromStation = "Halim Perdanakusuma",
            toStation = "Tegalluar",
            departureDate = "15 Juni 2026",
            departureTime = "08:00",
            arrivalTime = "08:45",
            seatNumber = "2A-15",
            price = "Rp 150.000"
        ) { success, error ->
            if (success) {
                Log.i("Email", "Email pembelian berhasil dikirim!")
            } else {
                Log.e("Email", "Gagal mengirim email: $error")
            }
        }
    }
    
    fun demonstrateRescheduleEmail(context: Context) {
        val emailManager = EmailNotificationManager(context)
        
        emailManager.sendRescheduleNotification(
            recipientEmail = "customer@example.com",
            bookingCode = "WHH240609001",
            passengerName = "Budi Santoso",
            trainName = "WHOOSH 103",
            fromStation = "Halim Perdanakusuma",
            toStation = "Tegalluar",
            oldDepartureDate = "15 Juni 2026",
            oldDepartureTime = "08:00",
            newDepartureDate = "16 Juni 2026",
            newDepartureTime = "10:30",
            newArrivalTime = "11:15",
            seatNumber = "2A-15",
            price = "Rp 150.000"
        ) { success, error ->
            if (success) {
                Log.i("Email", "Email reschedule berhasil dikirim!")
            } else {
                Log.e("Email", "Gagal mengirim email: $error")
            }
        }
    }
    
    fun demonstrateRefundEmail(context: Context) {
        val emailManager = EmailNotificationManager(context)
        
        emailManager.sendRefundNotification(
            recipientEmail = "customer@example.com",
            bookingCode = "WHH240609001",
            passengerName = "Budi Santoso",
            trainName = "WHOOSH 101",
            fromStation = "Halim Perdanakusuma",
            toStation = "Tegalluar",
            departureDate = "15 Juni 2026",
            departureTime = "08:00",
            seatNumber = "2A-15",
            originalPrice = "Rp 150.000",
            refundAmount = "Rp 135.000"
        ) { success, error ->
            if (success) {
                Log.i("Email", "Email refund berhasil dikirim!")
            } else {
                Log.e("Email", "Gagal mengirim email: $error")
            }
        }
    }
}