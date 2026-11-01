package com.example.whoos_aplication.utils

object WhooshUtils {

    val stations = listOf("Halim", "Karawang", "Padalarang", "Tegalluar")

    private val travelTimes = mapOf(
        "Halim"      to mapOf("Karawang" to 11, "Padalarang" to 30, "Tegalluar" to 52),
        "Karawang"   to mapOf("Halim"    to 11, "Padalarang" to 19, "Tegalluar" to 35),
        "Padalarang" to mapOf("Halim"    to 30, "Karawang"   to 19, "Tegalluar" to 17),
        "Tegalluar"  to mapOf("Halim"    to 52, "Karawang"   to 35, "Padalarang" to 17)
    )

    fun getTravelMinutes(from: String, to: String) = travelTimes[from]?.get(to) ?: 0

    fun getArrivalTime(departure: String, from: String, to: String): String {
        val (h, m) = departure.split(":").map { it.toInt() }
        val total  = h * 60 + m + getTravelMinutes(from, to)
        return "%02d:%02d".format(total / 60 % 24, total % 60)
    }

    fun getDurationLabel(from: String, to: String): String {
        val m = getTravelMinutes(from, to)
        return "${m}m"
    }

    fun getPricePerTicket(wagonClass: String, quantity: Int): Int = when (wagonClass) {
        "First Class"      -> when (quantity) {
            in 1..2 -> 600_000; in 3..5 -> 575_000; in 6..8 -> 550_000; else -> 525_000
        }
        "Kelas Bisnis"     -> when (quantity) {
            in 1..2 -> 450_000; in 3..5 -> 435_000; in 6..8 -> 420_000; else -> 400_000
        }
        "Kelas Ekonomi Premium" -> when (quantity) {
            in 1..2 -> 295_000; in 3..5 -> 280_000; in 6..8 -> 265_000; else -> 250_000
        }
        else -> 0
    }

    fun formatRupiah(amount: Int): String =
        "Rp." + "%,d".format(amount).replace(",", ".")

    val departureTimes: List<String> = buildList {
        var mins = 6 * 60
        while (mins <= 21 * 60 + 30) {
            add("%02d:%02d".format(mins / 60, mins % 60))
            mins += 30
        }
    }

    // Seat layout First Class: 3 kolom kiri (A,C) + lorong + 1 kolom kanan (F), 3 baris
    val firstClassSeats: List<String> = buildList {
        for (row in 1..3) { addAll(listOf("${row}A", "${row}C", "${row}F")) }
    }

    // Seat layout Economy: 3 kolom kiri (A,B,C) + lorong + 2 kolom kanan (E,F), 10 baris
    val economySeats: List<String> = buildList {
        for (row in 4..13) { addAll(listOf("${row}A", "${row}B", "${row}C", "${row}E", "${row}F")) }
    }

    val banks = listOf("Bank Mandiri","Bank BNI","Bank BRI","Bank BTN","Bank BCA","Bank BSI","Bank BJB")

    fun canAction(date: String, time: String): Boolean {
        return try {
            // date format: YYYY-MM-DD, time format: HH:mm
            val departureStr = "$date $time"
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val departureDate = sdf.parse(departureStr)
            val now = java.util.Date()
            
            // 2 hours in milliseconds = 2 * 60 * 60 * 1000 = 7,200,000
            val diff = (departureDate?.time ?: 0) - now.time
            diff >= 2 * 60 * 60 * 1000
        } catch (e: Exception) {
            false
        }
    }

    fun hasDeparted(date: String, time: String): Boolean {
        return try {
            val departureStr = "$date $time"
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val departureDate = sdf.parse(departureStr)
            val now = java.util.Date()
            departureDate?.before(now) ?: true
        } catch (e: Exception) {
            true
        }
    }

    fun decodeSeatId(seatId: String): String {
        val idVal = seatId.toIntOrNull() ?: return seatId
        if (idVal < 1000) return seatId
        val remainder = idVal % 1000
        val row = remainder / 10
        val colIndex = remainder % 10
        val colChar = when (colIndex) {
            1 -> 'A'
            2 -> 'B'
            3 -> 'C'
            4 -> 'D'
            5 -> 'F'
            else -> return seatId
        }
        return "$row$colChar"
    }

    fun decodeWagonNumber(seatId: String): String {
        val idVal = seatId.toIntOrNull() ?: return "01"
        if (idVal < 1000) return "01"
        val wagon = idVal / 1000
        return "%02d".format(wagon)
    }
}
