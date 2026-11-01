package com.example.whoos_aplication.model

data class BookingData(
    val userId         : Int    = 0,
    val username       : String = "",
    val userEmail      : String = "",
    val from           : String = "",
    val to             : String = "",
    val date           : String = "",
    val quantity       : Int    = 1,
    val departureTime  : String = "",
    val arrivalTime    : String = "",
    val trainCode      : String = "",
    val scheduleId     : Int    = 0,
    val wagonClass     : String = "",
    val pricePerTicket : Int    = 0,
    val basePrice      : Int    = 0,  // Storing the original schedule base price
    val totalPrice     : Int    = 0,
    val selectedCar    : String = "01",
    val selectedSeat   : String = "",      // Tetap simpan "1A" untuk display
    val selectedSeatId : Int    = 0,       // Tambah seat_id untuk kirim ke API
    val reservationNo  : String = "",
    val bookingStatus  : String = "paid",
    val passengers     : List<Passenger> = emptyList(),
    val createdAt      : String = ""       // Waktu order dibuat (dari server)
)

data class Schedule(
    val trainCode: String,
    val departureStation: String,
    val arrivalStation: String,
    val departureTime: String,
    val arrivalTime: String,
    val durationMinutes: Int,
    val price: Double
)

val whooshSchedules = emptyList<Schedule>()
