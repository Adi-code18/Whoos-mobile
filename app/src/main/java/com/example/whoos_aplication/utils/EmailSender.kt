package com.example.whoos_aplication.utils

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.mail.*
import javax.mail.internet.*

class EmailSender {
    
    companion object {
        private const val SMTP_HOST = "smtp.gmail.com"
        private const val SMTP_PORT = "587"
        private const val SENDER_EMAIL = "kcicwhoos@gmail.com" // Ganti dengan email aplikasi Anda
        private const val SENDER_PASSWORD = "aoqfdewaikwuqzvz" // Ganti dengan password aplikasi
    }
    
    enum class EmailType {
        PURCHASE, RESCHEDULE, REFUND
    }
    
    data class BookingInfo(
        val bookingCode: String,
        val passengerName: String,
        val trainName: String,
        val fromStation: String,
        val toStation: String,
        val departureDate: String,
        val departureTime: String,
        val arrivalTime: String,
        val seatNumber: String,
        val price: String,
        val refundAmount: String? = null,
        val newDepartureDate: String? = null,
        val newDepartureTime: String? = null,
        val newArrivalTime: String? = null
    )
    
    fun sendNotificationEmail(
        recipientEmail: String,
        emailType: EmailType,
        bookingInfo: BookingInfo,
        callback: (Boolean, String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val properties = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", SMTP_HOST)
                    put("mail.smtp.port", SMTP_PORT)
                }
                
                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD)
                    }
                })
                
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(SENDER_EMAIL, "WHOOSH Ticket"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    subject = getEmailSubject(emailType, bookingInfo.bookingCode)
                    setContent(getEmailContent(emailType, bookingInfo), "text/html; charset=utf-8")
                }
                
                Transport.send(message)
                callback(true, null)
                
            } catch (e: Exception) {
                callback(false, e.message)
            }
        }
    }
    
    private fun getEmailSubject(emailType: EmailType, bookingCode: String): String {
        return when (emailType) {
            EmailType.PURCHASE -> "Konfirmasi Pembelian Tiket - $bookingCode"
            EmailType.RESCHEDULE -> "Konfirmasi Reschedule Tiket - $bookingCode"
            EmailType.REFUND -> "Konfirmasi Refund Tiket - $bookingCode"
        }
    }
    
    private fun getEmailContent(emailType: EmailType, bookingInfo: BookingInfo): String {
        val baseTemplate = getBaseEmailTemplate()
        val contentBody = when (emailType) {
            EmailType.PURCHASE -> getPurchaseEmailBody(bookingInfo)
            EmailType.RESCHEDULE -> getRescheduleEmailBody(bookingInfo)
            EmailType.REFUND -> getRefundEmailBody(bookingInfo)
        }
        
        return baseTemplate.replace("{{CONTENT_BODY}}", contentBody)
            .replace("{{PASSENGER_NAME}}", bookingInfo.passengerName)
    }
    
    private fun getBaseEmailTemplate(): String {
        return """
        <!DOCTYPE html>
        <html lang="id">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>WHOOSH Notification</title>
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    background-color: #f5f5f5;
                    color: #333;
                    line-height: 1.6;
                }
                
                .email-container {
                    max-width: 600px;
                    margin: 20px auto;
                    background-color: #ffffff;
                    border-radius: 12px;
                    overflow: hidden;
                    box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                }
                
                .header {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    padding: 30px 20px;
                    text-align: center;
                }
                
                .header h1 {
                    font-size: 28px;
                    margin-bottom: 10px;
                    font-weight: 700;
                }
                
                .header p {
                    font-size: 16px;
                    opacity: 0.9;
                }
                
                .content {
                    padding: 30px;
                }
                
                .greeting {
                    font-size: 18px;
                    margin-bottom: 20px;
                    color: #2c3e50;
                }
                
                .ticket-info {
                    background-color: #f8f9fa;
                    border-radius: 8px;
                    padding: 25px;
                    margin: 25px 0;
                    border-left: 4px solid #667eea;
                }
                
                .info-row {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin: 12px 0;
                    padding: 8px 0;
                    border-bottom: 1px solid #e9ecef;
                }
                
                .info-row:last-child {
                    border-bottom: none;
                    margin-bottom: 0;
                }
                
                .info-label {
                    font-weight: 600;
                    color: #495057;
                    flex: 1;
                }
                
                .info-value {
                    font-weight: 500;
                    color: #2c3e50;
                    flex: 2;
                    text-align: right;
                }
                
                .highlight {
                    background-color: #fff3cd;
                    border: 1px solid #ffeaa7;
                    padding: 15px;
                    border-radius: 8px;
                    margin: 20px 0;
                }
                
                .success-highlight {
                    background-color: #d4edda;
                    border: 1px solid #c3e6cb;
                    color: #155724;
                }
                
                .warning-highlight {
                    background-color: #fff3cd;
                    border: 1px solid #ffeaa7;
                    color: #856404;
                }
                
                .info-highlight {
                    background-color: #d1ecf1;
                    border: 1px solid #bee5eb;
                    color: #0c5460;
                }
                
                .button {
                    display: inline-block;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    padding: 15px 30px;
                    text-decoration: none;
                    border-radius: 8px;
                    font-weight: 600;
                    text-align: center;
                    margin: 20px 0;
                    transition: transform 0.2s;
                }
                
                .button:hover {
                    transform: translateY(-2px);
                }
                
                .footer {
                    background-color: #2c3e50;
                    color: white;
                    padding: 25px;
                    text-align: center;
                }
                
                .footer p {
                    margin: 8px 0;
                }
                
                .footer a {
                    color: #74b9ff;
                    text-decoration: none;
                }
                
                @media (max-width: 600px) {
                    .email-container {
                        margin: 10px;
                        border-radius: 8px;
                    }
                    
                    .content {
                        padding: 20px;
                    }
                    
                    .info-row {
                        flex-direction: column;
                        align-items: flex-start;
                    }
                    
                    .info-value {
                        text-align: left;
                        margin-top: 5px;
                    }
                }
            </style>
        </head>
        <body>
            <div class="email-container">
                <div class="header">
                    <h1>🚄 WHOOSH</h1>
                    <p>High Speed Rail Indonesia</p>
                </div>
                
                <div class="content">
                    <div class="greeting">
                        Halo, {{PASSENGER_NAME}}!
                    </div>
                    
                    {{CONTENT_BODY}}
                    
                    <div style="margin-top: 30px; padding-top: 20px; border-top: 2px solid #e9ecef;">
                        <p style="color: #6c757d; font-size: 14px; margin-bottom: 10px;">
                            Jika Anda memiliki pertanyaan, jangan ragu untuk menghubungi customer service kami.
                        </p>
                        <a href="tel:08001234567" class="button" style="display: inline-block; margin-right: 10px;">
                            📞 Hubungi CS
                        </a>
                        <a href="https://whoosh.co.id/help" class="button">
                            ❓ FAQ
                        </a>
                    </div>
                </div>
                
                <div class="footer">
                    <p><strong>WHOOSH - PT Kereta Cepat Indonesia-China</strong></p>
                    <p>📧 info@whoosh.co.id | 📱 0800-1234-567</p>
                    <p>🌐 <a href="https://whoosh.co.id">www.whoosh.co.id</a></p>
                    <p style="margin-top: 15px; font-size: 12px; opacity: 0.8;">
                        Email ini dikirim secara otomatis, mohon tidak membalas email ini.
                    </p>
                </div>
            </div>
        </body>
        </html>
        """.trimIndent()
    }
    
    private fun getPurchaseEmailBody(bookingInfo: BookingInfo): String {
        return """
        <div class="highlight success-highlight">
            <h3 style="margin-bottom: 10px; color: #155724;">✅ Pembelian Tiket Berhasil!</h3>
            <p>Terima kasih telah mempercayai WHOOSH untuk perjalanan Anda. Tiket Anda telah berhasil dipesan.</p>
        </div>
        
        <div class="ticket-info">
            <h4 style="margin-bottom: 20px; color: #2c3e50; text-align: center;">🎫 Detail Tiket</h4>
            
            <div class="info-row">
                <span class="info-label">Kode Booking:</span>
                <span class="info-value" style="font-weight: 700; color: #667eea;">${bookingInfo.bookingCode}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Nama Penumpang:</span>
                <span class="info-value">${bookingInfo.passengerName}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Kereta:</span>
                <span class="info-value">${bookingInfo.trainName}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Rute:</span>
                <span class="info-value">${bookingInfo.fromStation} → ${bookingInfo.toStation}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Tanggal Keberangkatan:</span>
                <span class="info-value">${bookingInfo.departureDate}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Waktu Keberangkatan:</span>
                <span class="info-value">${bookingInfo.departureTime}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Waktu Tiba:</span>
                <span class="info-value">${bookingInfo.arrivalTime}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Nomor Kursi:</span>
                <span class="info-value">${bookingInfo.seatNumber}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Total Harga:</span>
                <span class="info-value" style="font-weight: 700; color: #e74c3c;">${bookingInfo.price}</span>
            </div>
        </div>
        
        <div class="highlight info-highlight">
            <h4 style="margin-bottom: 10px;">📋 Informasi Penting:</h4>
            <ul style="margin-left: 20px;">
                <li>Harap datang minimal 30 menit sebelum keberangkatan</li>
                <li>Bawa identitas diri yang valid (KTP/Paspor)</li>
                <li>Simpan kode booking untuk check-in</li>
                <li>Tiket dapat direschedule hingga 2 jam sebelum keberangkatan</li>
            </ul>
        </div>
        """.trimIndent()
    }
    
    private fun getRescheduleEmailBody(bookingInfo: BookingInfo): String {
        return """
        <div class="highlight warning-highlight">
            <h3 style="margin-bottom: 10px; color: #856404;">🔄 Reschedule Tiket Berhasil!</h3>
            <p>Jadwal perjalanan Anda telah berhasil diubah sesuai permintaan.</p>
        </div>
        
        <div class="ticket-info">
            <h4 style="margin-bottom: 20px; color: #2c3e50; text-align: center;">🎫 Detail Tiket Baru</h4>
            
            <div class="info-row">
                <span class="info-label">Kode Booking:</span>
                <span class="info-value" style="font-weight: 700; color: #667eea;">${bookingInfo.bookingCode}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Nama Penumpang:</span>
                <span class="info-value">${bookingInfo.passengerName}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Kereta:</span>
                <span class="info-value">${bookingInfo.trainName}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Rute:</span>
                <span class="info-value">${bookingInfo.fromStation} → ${bookingInfo.toStation}</span>
            </div>
            
            <div style="background-color: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0;">
                <h5 style="color: #856404; margin-bottom: 15px;">📅 Jadwal Baru:</h5>
                
                <div class="info-row" style="border-bottom: none; margin: 8px 0;">
                    <span class="info-label">Tanggal Keberangkatan:</span>
                    <span class="info-value" style="font-weight: 700;">${bookingInfo.newDepartureDate}</span>
                </div>
                
                <div class="info-row" style="border-bottom: none; margin: 8px 0;">
                    <span class="info-label">Waktu Keberangkatan:</span>
                    <span class="info-value" style="font-weight: 700;">${bookingInfo.newDepartureTime}</span>
                </div>
                
                <div class="info-row" style="border-bottom: none; margin: 8px 0;">
                    <span class="info-label">Waktu Tiba:</span>
                    <span class="info-value" style="font-weight: 700;">${bookingInfo.newArrivalTime}</span>
                </div>
            </div>
            
            <div class="info-row">
                <span class="info-label">Nomor Kursi:</span>
                <span class="info-value">${bookingInfo.seatNumber}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Total Harga:</span>
                <span class="info-value" style="font-weight: 700; color: #e74c3c;">${bookingInfo.price}</span>
            </div>
        </div>
        
        <div style="background-color: #f8f9fa; padding: 15px; border-radius: 8px; border-left: 4px solid #ffc107;">
            <h5 style="color: #856404; margin-bottom: 10px;">⚠️ Jadwal Lama (Dibatalkan):</h5>
            <p style="color: #6c757d; text-decoration: line-through;">
                ${bookingInfo.departureDate} - ${bookingInfo.departureTime}
            </p>
        </div>
        
        <div class="highlight info-highlight">
            <h4 style="margin-bottom: 10px;">📋 Informasi Penting:</h4>
            <ul style="margin-left: 20px;">
                <li>Harap datang minimal 30 menit sebelum keberangkatan BARU</li>
                <li>Bawa identitas diri yang valid (KTP/Paspor)</li>
                <li>Kode booking tetap sama</li>
                <li>Tiket dapat direschedule lagi hingga 2 jam sebelum keberangkatan</li>
            </ul>
        </div>
        """.trimIndent()
    }
    
    private fun getRefundEmailBody(bookingInfo: BookingInfo): String {
        return """
        <div class="highlight info-highlight">
            <h3 style="margin-bottom: 10px; color: #0c5460;">💰 Refund Tiket Berhasil!</h3>
            <p>Permintaan refund Anda telah diproses. Dana akan dikembalikan dalam 3-5 hari kerja.</p>
        </div>
        
        <div class="ticket-info">
            <h4 style="margin-bottom: 20px; color: #2c3e50; text-align: center;">🎫 Detail Tiket yang Direfund</h4>
            
            <div class="info-row">
                <span class="info-label">Kode Booking:</span>
                <span class="info-value" style="font-weight: 700; color: #667eea;">${bookingInfo.bookingCode}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Nama Penumpang:</span>
                <span class="info-value">${bookingInfo.passengerName}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Kereta:</span>
                <span class="info-value">${bookingInfo.trainName}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Rute:</span>
                <span class="info-value">${bookingInfo.fromStation} → ${bookingInfo.toStation}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Tanggal Keberangkatan:</span>
                <span class="info-value">${bookingInfo.departureDate}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Waktu Keberangkatan:</span>
                <span class="info-value">${bookingInfo.departureTime}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Nomor Kursi:</span>
                <span class="info-value">${bookingInfo.seatNumber}</span>
            </div>
            
            <div style="background-color: #d4edda; padding: 15px; border-radius: 8px; margin: 20px 0; border: 2px solid #28a745;">
                <h5 style="color: #155724; margin-bottom: 15px;">💵 Detail Refund:</h5>
                
                <div class="info-row" style="border-bottom: none; margin: 8px 0;">
                    <span class="info-label">Harga Tiket:</span>
                    <span class="info-value">${bookingInfo.price}</span>
                </div>
                
                <div class="info-row" style="border-bottom: none; margin: 8px 0;">
                    <span class="info-label">Jumlah Refund:</span>
                    <span class="info-value" style="font-weight: 700; color: #28a745; font-size: 18px;">${bookingInfo.refundAmount}</span>
                </div>
            </div>
        </div>
        
        <div class="highlight warning-highlight">
            <h4 style="margin-bottom: 10px;">⏰ Timeline Refund:</h4>
            <ul style="margin-left: 20px;">
                <li><strong>Kartu Kredit/Debit:</strong> 3-5 hari kerja</li>
                <li><strong>Transfer Bank:</strong> 1-3 hari kerja</li>
                <li><strong>E-Wallet:</strong> 1-2 hari kerja</li>
                <li><strong>Virtual Account:</strong> 3-5 hari kerja</li>
            </ul>
        </div>
        
        <div style="background-color: #f8f9fa; padding: 15px; border-radius: 8px; border-left: 4px solid #6c757d; margin-top: 20px;">
            <p style="color: #6c757d; margin-bottom: 10px;">
                <strong>Nomor Referensi Refund:</strong> REF-${bookingInfo.bookingCode}-${System.currentTimeMillis()}
            </p>
            <p style="color: #6c757d; font-size: 14px;">
                Simpan nomor referensi ini untuk keperluan follow-up jika diperlukan.
            </p>
        </div>
        """.trimIndent()
    }
}