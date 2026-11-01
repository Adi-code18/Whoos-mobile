package com.example.whoos_aplication.model

import java.util.UUID

data class Passenger(
    val id           : String = UUID.randomUUID().toString(),
    val name         : String = "",
    val idNumber     : String = "",
    val gender       : String = "",
    val birthDate    : String = "",
    val passengerType: String = "Adult",
    val phone        : String = "",
    val email        : String = "",
    val seatNumber   : String = "",      // "1A" untuk display
    val seatId       : Int    = 0        // seat_id dari database
)