package com.example.whoos_aplication.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.whoos_aplication.model.BookingData
import com.example.whoos_aplication.model.Passenger
import com.example.whoos_aplication.remote.*
import com.example.whoos_aplication.utils.SessionManager
import com.example.whoos_aplication.utils.EmailNotificationManager
import com.example.whoos_aplication.utils.EmailLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val emailNotificationManager = EmailNotificationManager(application.applicationContext)

    var booking by mutableStateOf(BookingData())
        private set

    val passengers = mutableStateListOf<Passenger>()
    val bookingHistory = mutableStateListOf<BookingData>()
    val popularSchedules = mutableStateListOf<ScheduleResponse>()
    val wagonClasses = mutableStateListOf<WagonClassResponse>()
    
    var stations = mutableStateListOf<StationResponse>()
        private set
    
    var schedules = mutableStateListOf<ScheduleResponse>()
        private set

    init {
        if (sessionManager.isLoggedIn()) {
            booking = booking.copy(
                userId = sessionManager.getUserId(),
                username = sessionManager.getUsername() ?: "User",
                userEmail = sessionManager.getEmail() ?: ""
            )
        }
        fetchStations()
        fetchPopularSchedules()
        fetchWagonClasses()
    }

    fun fetchPopularSchedules() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Mengambil jadwal default untuk dashboard
                // Gunakan tanggal hari ini atau kosong untuk mendapatkan semua jadwal
                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())
                val response = RetrofitClient.instance.getSchedules("Halim", "Tegalluar", today)
                withContext(Dispatchers.Main) {
                    popularSchedules.clear()
                    popularSchedules.addAll(response.take(5))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // Jika gagal, kosongkan list
                    popularSchedules.clear()
                }
            }
        }
    }

    fun fetchWagonClasses() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getWagonClasses()
                withContext(Dispatchers.Main) {
                    wagonClasses.clear()
                    wagonClasses.addAll(response)
                    println("DEBUG: Loaded ${response.size} wagon classes from API")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // Fallback ke hardcoded jika API gagal
                    wagonClasses.clear()
                    println("DEBUG: Failed to load wagon classes from API: ${e.message}")
                }
            }
        }
    }

    fun fetchStations() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getStations()
                withContext(Dispatchers.Main) {
                    stations.clear()
                    stations.addAll(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchSchedules(from: String, to: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getSchedules(from, to, date)
                withContext(Dispatchers.Main) {
                    schedules.clear()
                    schedules.addAll(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    schedules.clear()
                }
            }
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.login(email, pass)
                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        val username = response.username ?: "User"
                        val userEmail = response.email ?: email
                        val userId = response.user_id ?: 0
                        
                        sessionManager.saveAuthToken(userEmail, username, userId)
                        
                        booking = booking.copy(
                            userId = userId,
                            username = username,
                            userEmail = userEmail
                        )
                        onSuccess()
                    } else {
                        onError(response.message)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Gagal login: ${e.message}")
                }
            }
        }
    }

    private fun setLocalUser(username: String, email: String = "", userId: Int = 0) {
        sessionManager.saveAuthToken(email, username, userId)
        booking = booking.copy(username = username, userEmail = email)
    }

    fun registerUser(fullName: String, email: String, phone: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.register(fullName, email, phone, pass)
                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        // Jangan panggil setLocalUser di sini karena kita butuh userId yang asli dari database.
                        // User harus login setelah registrasi agar mendapatkan userId valid.
                        onSuccess()
                    } else {
                        onError(response.message)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Gagal terhubung ke server: ${e.message}")
                }
            }
        }
    }

    fun updateForm(from: String, to: String, date: String, quantity: Int) {
        // Konversi format tanggal dari DD/MM/YYYY ke YYYY-MM-DD untuk API
        val formattedDate = try {
            if (date.contains("/")) {
                val parts = date.split("/")
                if (parts.size == 3) {
                    "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}"
                } else {
                    date
                }
            } else {
                date
            }
        } catch (e: Exception) {
            date
        }
        booking = booking.copy(from = from, to = to, date = formattedDate, quantity = quantity)
    }

    fun selectSchedule(scheduleId: Int, depTime: String, arrTime: String, trainCode: String, basePrice: Int) {
        booking = booking.copy(
            scheduleId    = scheduleId,
            departureTime = depTime,
            arrivalTime   = arrTime,
            trainCode     = trainCode,
            basePrice     = basePrice
        )
    }

    fun selectClass(wagonClass: String, pricePerTicket: Int, status: String = "pending") {
        val randomResNo = (10000000..99999999).random().toString()
        val finalQuantity = if (booking.passengers.isNotEmpty()) booking.passengers.size else booking.quantity

        booking = booking.copy(
            wagonClass     = wagonClass,
            pricePerTicket = pricePerTicket,
            quantity       = finalQuantity,
            totalPrice     = pricePerTicket * finalQuantity,
            reservationNo  = "GA$randomResNo",
            bookingStatus  = status
        )
    }

    fun togglePassengerSelection(p: Passenger) {
        val currentSelected = booking.passengers.toMutableList()
        if (currentSelected.any { it.id == p.id }) {
            currentSelected.removeIf { it.id == p.id }
        } else {
            currentSelected.add(p)
        }
        val newQuantity = if (currentSelected.isNotEmpty()) currentSelected.size else 1
        booking = booking.copy(
            passengers = currentSelected,
            quantity   = newQuantity,
            totalPrice = booking.pricePerTicket * newQuantity
        )
    }

    fun completePayment(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (booking.reservationNo.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Validasi seat_id
                    if (booking.selectedSeatId == 0) {
                        withContext(Dispatchers.Main) { onError("Silakan pilih kursi") }
                        return@launch
                    }
                    
                    val passengerRequests = booking.passengers.map { p ->
                        PassengerRequest(full_name = p.name, id_number = p.idNumber, seat_id = booking.selectedSeatId)
                    }

                    val currentUserId = if (booking.userId != 0) booking.userId else sessionManager.getUserId()

                    val request = BookingRequest(
                        user_id = currentUserId,
                        schedule_id = booking.scheduleId,
                        total_price = booking.totalPrice,
                        class_name = booking.wagonClass,
                        passengers = passengerRequests,
                        status = "paid" 
                    )

                    val response = RetrofitClient.instance.createBooking(request)

                    if (response.status == "success") {
                        // PAKSA UPDATE: Gunakan booking_code dari server, bukan reservationNo lokal
                        val resNo = response.booking_code 
                        if (resNo != null) {
                            try {
                                RetrofitClient.instance.updateBookingStatus(resNo, "paid")
                            } catch (e: Exception) {
                                e.printStackTrace() 
                            }
                        }

                        // Kirim email notifikasi pembelian
                        sendPurchaseEmailNotification(resNo ?: booking.reservationNo)

                        withContext(Dispatchers.Main) {
                            fetchBookingHistory()
                            resetBooking()
                            onSuccess()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onError(response.message)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { onError("Error: ${e.message}") }
                }
            }
        }
    }

    fun tundaBooking(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (booking.reservationNo.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Validasi seat_id
                    if (booking.selectedSeatId == 0) {
                        withContext(Dispatchers.Main) { onError("Silakan pilih kursi") }
                        return@launch
                    }
                    
                    val passengerRequests = booking.passengers.map { p ->
                        PassengerRequest(full_name = p.name, id_number = p.idNumber, seat_id = booking.selectedSeatId)
                    }

                    val currentUserId = if (booking.userId != 0) booking.userId else sessionManager.getUserId()

                    val request = BookingRequest(
                        user_id = currentUserId,
                        schedule_id = booking.scheduleId,
                        total_price = booking.totalPrice,
                        class_name = booking.wagonClass,
                        passengers = passengerRequests,
                        status = "pending" // TUNDA = pending
                    )

                    val response = RetrofitClient.instance.createBooking(request)

                    if (response.status == "success") {
                        val resNo = response.booking_code 
                        if (resNo != null) {
                            try {
                                RetrofitClient.instance.updateBookingStatus(resNo, "pending")
                            } catch (e: Exception) {
                                e.printStackTrace() 
                            }
                        }

                        withContext(Dispatchers.Main) {
                            fetchBookingHistory()
                            resetBooking()
                            onSuccess()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onError(response.message)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { onError("Error: ${e.message}") }
                }
            }
        }
    }

    fun cancelBooking(reservationNo: String, totalPrice: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Aturan: Potongan biaya administrasi 25%
                val refundAmount = (totalPrice * 0.75).toInt()
                
                // Update status ke 'cancelled' atau 'refunded' (yang akan masuk ke list refund di UI)
                val response = RetrofitClient.instance.updateBookingStatus(reservationNo, "refunded")
                
                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        // Cari booking data dari history untuk email
                        val bookingData = bookingHistory.find { it.reservationNo == reservationNo }
                        
                        if (bookingData != null) {
                            // Kirim email refund menggunakan data booking yang ditemukan
                            sendEmailForExistingBooking(
                                bookingData = bookingData,
                                emailType = "refund",
                                refundAmount = refundAmount
                            ) { emailSuccess, emailError ->
                                if (emailSuccess) {
                                    println("✅ Email refund berhasil dikirim untuk booking: $reservationNo")
                                } else {
                                    println("❌ Gagal mengirim email refund: $emailError")
                                }
                            }
                        } else {
                            println("⚠️ Data booking tidak ditemukan untuk email refund")
                        }
                        
                        fetchBookingHistory()
                        onSuccess()
                    } else {
                        onError(response.message)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError("Gagal membatalkan: ${e.message}") }
            }
        }
    }

    fun prepareReschedule(oldBooking: BookingData, onReady: () -> Unit) {
        // Aturan: Biaya admin 15% dari harga lama
        val adminFee = (oldBooking.totalPrice * 0.15).toInt()
        
        // Reset state booking dengan data lama untuk memulai proses pemilihan jadwal baru
        // Kita simpan info dasar, tapi user harus pilih jadwal dan seat lagi
        booking = BookingData(
            userId = oldBooking.userId,
            username = oldBooking.username,
            userEmail = oldBooking.userEmail,
            from = oldBooking.from,
            to = oldBooking.to,
            quantity = oldBooking.quantity,
            passengers = oldBooking.passengers,
            wagonClass = oldBooking.wagonClass,
            basePrice = adminFee, // Simpan admin fee di basePrice sementara sebagai penanda biaya tambahan
            bookingStatus = "rescheduling"
        )
        onReady()
    }

    fun rescheduleBooking(reservationNo: String, newDate: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.rescheduleBooking(reservationNo, newDate)
                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        // Cari booking data dari history untuk email
                        val bookingData = bookingHistory.find { it.reservationNo == reservationNo }
                        
                        if (bookingData != null) {
                            // Kirim email reschedule menggunakan data booking yang ditemukan
                            sendEmailForExistingBooking(
                                bookingData = bookingData,
                                emailType = "reschedule",
                                newDate = newDate
                            ) { emailSuccess, emailError ->
                                if (emailSuccess) {
                                    println("✅ Email reschedule berhasil dikirim untuk booking: $reservationNo")
                                } else {
                                    println("❌ Gagal mengirim email reschedule: $emailError")
                                }
                            }
                        } else {
                            println("⚠️ Data booking tidak ditemukan untuk email reschedule")
                        }
                        
                        fetchBookingHistory()
                        onSuccess()
                    } else {
                        onError(response.message)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Gagal reschedule: ${e.message}")
                }
            }
        }
    }

    fun fetchBookingHistory() {
        if (booking.userEmail.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getBookings(booking.userEmail)
                val remoteData = response.map { r ->
                    BookingData(
                        userEmail     = booking.userEmail,
                        reservationNo  = r.reservation_no,
                        from           = r.from_station,
                        to             = r.to_station,
                        date           = r.date,
                        departureTime  = r.departure_time,
                        arrivalTime    = r.arrival_time,
                        trainCode      = r.train_code,
                        wagonClass     = r.wagon_class,
                        selectedSeat   = r.seat,
                        totalPrice     = r.total_price,
                        bookingStatus  = r.status,
                        createdAt      = r.created_at
                    )
                }
                
                withContext(Dispatchers.Main) {
                    // Cek jika data berbeda sebelum update untuk mengurangi recomposition
                    if (bookingHistory.size != remoteData.size || bookingHistory != remoteData) {
                        bookingHistory.clear()
                        bookingHistory.addAll(remoteData)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectSeat(car: String, seat: String, seatId: Int) {
        println("DEBUG ViewModel: selectSeat called - Car: $car, Seat: $seat, SeatID: $seatId")
        
        // Update booking dengan seat yang dipilih
        booking = booking.copy(selectedCar = car, selectedSeat = seat, selectedSeatId = seatId)
        
        println("DEBUG ViewModel: Booking updated - selectedSeatId: ${booking.selectedSeatId}")
        
        // Update passenger pertama dengan seat info (jika ada passenger yang dipilih)
        if (booking.passengers.isNotEmpty()) {
            val updatedPassengers = booking.passengers.mapIndexed { index, p ->
                if (index == 0) { // Assign seat ke passenger pertama
                    p.copy(seatNumber = seat, seatId = seatId)
                } else {
                    p
                }
            }
            booking = booking.copy(passengers = updatedPassengers)
            println("DEBUG ViewModel: Passenger updated with seatId: ${updatedPassengers.first().seatId}")
        } else {
            println("DEBUG ViewModel: No passengers to update")
        }
    }

    // Tambah state untuk seats
    var availableSeats = mutableStateListOf<SeatResponse>()
        private set

    fun fetchSeats(scheduleId: Int, wagonNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getSeats(scheduleId, wagonNumber)
                withContext(Dispatchers.Main) {
                    availableSeats.clear()
                    availableSeats.addAll(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    availableSeats.clear()
                }
            }
        }
    }

    // Helper function untuk hitung harga berdasarkan class dan base price dari schedule
    fun calculatePrice(className: String, basePrice: Int): Int {
        val wagonClass = wagonClasses.find { it.class_name.equals(className, ignoreCase = true) }
        return if (wagonClass != null) {
            (basePrice * wagonClass.price_multiplier).toInt()
        } else {
            // Fallback ke multiplier hardcoded jika API gagal
            when {
                className.contains("Ekonomi", ignoreCase = true) -> basePrice
                className.contains("Eksekutif", ignoreCase = true) -> (basePrice * 1.5).toInt()
                className.contains("Busines", ignoreCase = true) || 
                className.contains("Business", ignoreCase = true) -> (basePrice * 1.7).toInt()
                else -> basePrice
            }
        }
    }

    fun addPassenger(p: Passenger) = passengers.add(p)

    // Lanjutkan pembayaran untuk tiket yang ditunda (pending -> paid)
    fun continuePayment(reservationNo: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.updateBookingStatus(reservationNo, "paid")
                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        fetchBookingHistory()
                        onSuccess()
                    } else {
                        onError(response.message)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError("Gagal memproses pembayaran: ${e.message}") }
            }
        }
    }

    fun removePassenger(id: String) = passengers.removeIf { it.id == id }

    fun resetBooking() {
        booking = BookingData(username = booking.username, userEmail = booking.userEmail)
        passengers.clear()
    }

    fun logout(onLogout: () -> Unit) {
        sessionManager.clearSession()
        booking = BookingData()
        passengers.clear()
        bookingHistory.clear()
        onLogout()
    }

    // Email notification functions
    private fun sendPurchaseEmailNotification(bookingCode: String) {
        viewModelScope.launch {
            try {
                // Pastikan user sudah login dan email tersedia
                val userEmail = booking.userEmail.ifEmpty { sessionManager.getEmail() }
                
                if (userEmail.isNullOrEmpty()) {
                    EmailLogger.logEmailError("PURCHASE", "Unknown", "User email tidak ditemukan")
                    return@launch
                }

                // Log user session info untuk debugging
                EmailLogger.logUserSession(userEmail, sessionManager.getUsername(), sessionManager.getUserId())

                val passengerName = if (booking.passengers.isNotEmpty()) {
                    booking.passengers.first().name
                } else {
                    booking.username.ifEmpty { sessionManager.getUsername() ?: "Penumpang" }
                }

                println("📧 Mengirim email pembelian ke: $userEmail untuk booking: $bookingCode")

                emailNotificationManager.sendPurchaseNotification(
                    recipientEmail = userEmail,
                    bookingCode = bookingCode,
                    passengerName = passengerName,
                    trainName = booking.trainCode.ifEmpty { "WHOOSH" },
                    fromStation = booking.from,
                    toStation = booking.to,
                    departureDate = formatDateForEmail(booking.date),
                    departureTime = booking.departureTime,
                    arrivalTime = booking.arrivalTime,
                    seatNumber = booking.selectedSeat.ifEmpty { "Belum dipilih" },
                    price = formatPriceForEmail(booking.totalPrice)
                ) { success, error ->
                    if (success) {
                        EmailLogger.logPurchaseEmail(userEmail, bookingCode)
                    } else {
                        EmailLogger.logEmailError("PURCHASE", userEmail, error ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                println("❌ Error saat mengirim email pembelian: ${e.message}")
            }
        }
    }

    private fun sendRescheduleEmailNotification(bookingCode: String, newDate: String) {
        viewModelScope.launch {
            try {
                // Ambil email dari user yang sedang login
                val userEmail = booking.userEmail.ifEmpty { sessionManager.getEmail() }
                
                if (userEmail.isNullOrEmpty()) {
                    println("❌ Tidak dapat mengirim email reschedule: User email tidak ditemukan")
                    return@launch
                }

                val passengerName = if (booking.passengers.isNotEmpty()) {
                    booking.passengers.first().name
                } else {
                    booking.username.ifEmpty { sessionManager.getUsername() ?: "Penumpang" }
                }

                println("📧 Mengirim email reschedule ke: $userEmail untuk booking: $bookingCode")

                emailNotificationManager.sendRescheduleNotification(
                    recipientEmail = userEmail,
                    bookingCode = bookingCode,
                    passengerName = passengerName,
                    trainName = booking.trainCode.ifEmpty { "WHOOSH" },
                    fromStation = booking.from,
                    toStation = booking.to,
                    oldDepartureDate = formatDateForEmail(booking.date),
                    oldDepartureTime = booking.departureTime,
                    newDepartureDate = formatDateForEmail(newDate),
                    newDepartureTime = booking.departureTime, // Asumsi waktu sama, sesuaikan jika berbeda
                    newArrivalTime = booking.arrivalTime, // Asumsi waktu sama, sesuaikan jika berbeda
                    seatNumber = booking.selectedSeat.ifEmpty { "Belum dipilih" },
                    price = formatPriceForEmail(booking.totalPrice)
                ) { success, error ->
                    if (success) {
                        println("✅ Email reschedule berhasil dikirim ke $userEmail")
                    } else {
                        println("❌ Gagal mengirim email reschedule: $error")
                    }
                }
            } catch (e: Exception) {
                println("❌ Error saat mengirim email reschedule: ${e.message}")
            }
        }
    }

    private fun sendRefundEmailNotification(bookingCode: String, originalPrice: Int, refundAmount: Int) {
        viewModelScope.launch {
            try {
                // Ambil email dari user yang sedang login
                val userEmail = booking.userEmail.ifEmpty { sessionManager.getEmail() }
                
                if (userEmail.isNullOrEmpty()) {
                    println("❌ Tidak dapat mengirim email refund: User email tidak ditemukan")
                    return@launch
                }

                val passengerName = if (booking.passengers.isNotEmpty()) {
                    booking.passengers.first().name
                } else {
                    booking.username.ifEmpty { sessionManager.getUsername() ?: "Penumpang" }
                }

                println("📧 Mengirim email refund ke: $userEmail untuk booking: $bookingCode")

                emailNotificationManager.sendRefundNotification(
                    recipientEmail = userEmail,
                    bookingCode = bookingCode,
                    passengerName = passengerName,
                    trainName = booking.trainCode.ifEmpty { "WHOOSH" },
                    fromStation = booking.from,
                    toStation = booking.to,
                    departureDate = formatDateForEmail(booking.date),
                    departureTime = booking.departureTime,
                    seatNumber = booking.selectedSeat.ifEmpty { "Belum dipilih" },
                    originalPrice = formatPriceForEmail(originalPrice),
                    refundAmount = formatPriceForEmail(refundAmount)
                ) { success, error ->
                    if (success) {
                        println("✅ Email refund berhasil dikirim ke $userEmail")
                    } else {
                        println("❌ Gagal mengirim email refund: $error")
                    }
                }
            } catch (e: Exception) {
                println("❌ Error saat mengirim email refund: ${e.message}")
            }
        }
    }

    // Helper functions untuk format data email
    private fun formatDateForEmail(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString // Return original string jika parsing gagal
        }
    }

    private fun formatPriceForEmail(price: Int): String {
        return try {
            val formatter = java.text.DecimalFormat("#,###")
            "Rp ${formatter.format(price)}"
        } catch (e: Exception) {
            "Rp $price"
        }
    }

    // Fungsi untuk mengirim email berdasarkan booking history (untuk reschedule & refund)
    fun sendEmailForExistingBooking(
        bookingData: BookingData,
        emailType: String, // "reschedule" atau "refund"
        newDate: String? = null,
        refundAmount: Int? = null,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Ambil email user yang sedang login
                val currentUserEmail = sessionManager.getEmail()
                
                if (currentUserEmail.isNullOrEmpty()) {
                    onResult(false, "Email user tidak ditemukan. Silakan login ulang.")
                    return@launch
                }

                val currentUsername = sessionManager.getUsername() ?: "Penumpang"

                println("📧 Mengirim email $emailType ke: $currentUserEmail untuk booking: ${bookingData.reservationNo}")

                when (emailType.lowercase()) {
                    "reschedule" -> {
                        if (newDate == null) {
                            onResult(false, "Tanggal baru diperlukan untuk reschedule email")
                            return@launch
                        }

                        emailNotificationManager.sendRescheduleNotification(
                            recipientEmail = currentUserEmail,
                            bookingCode = bookingData.reservationNo,
                            passengerName = currentUsername,
                            trainName = bookingData.trainCode,
                            fromStation = bookingData.from,
                            toStation = bookingData.to,
                            oldDepartureDate = formatDateForEmail(bookingData.date),
                            oldDepartureTime = bookingData.departureTime,
                            newDepartureDate = formatDateForEmail(newDate),
                            newDepartureTime = bookingData.departureTime,
                            newArrivalTime = bookingData.arrivalTime,
                            seatNumber = bookingData.selectedSeat,
                            price = formatPriceForEmail(bookingData.totalPrice)
                        ) { success, error ->
                            onResult(success, error)
                        }
                    }

                    "refund" -> {
                        val refundAmountFinal = refundAmount ?: (bookingData.totalPrice * 0.75).toInt()

                        emailNotificationManager.sendRefundNotification(
                            recipientEmail = currentUserEmail,
                            bookingCode = bookingData.reservationNo,
                            passengerName = currentUsername,
                            trainName = bookingData.trainCode,
                            fromStation = bookingData.from,
                            toStation = bookingData.to,
                            departureDate = formatDateForEmail(bookingData.date),
                            departureTime = bookingData.departureTime,
                            seatNumber = bookingData.selectedSeat,
                            originalPrice = formatPriceForEmail(bookingData.totalPrice),
                            refundAmount = formatPriceForEmail(refundAmountFinal)
                        ) { success, error ->
                            onResult(success, error)
                        }
                    }

                    else -> {
                        onResult(false, "Tipe email tidak valid: $emailType")
                    }
                }
            } catch (e: Exception) {
                println("❌ Error saat mengirim email $emailType: ${e.message}")
                onResult(false, "Error: ${e.message}")
            }
        }
    }

    // Fungsi untuk debug - menampilkan info user yang sedang login
    fun getCurrentUserInfo(): String {
        return """
            User Info:
            - Email: ${sessionManager.getEmail()}
            - Username: ${sessionManager.getUsername()}
            - User ID: ${sessionManager.getUserId()}
            - Is Logged In: ${sessionManager.isLoggedIn()}
            - Booking Email: ${booking.userEmail}
        """.trimIndent()
    }
}

