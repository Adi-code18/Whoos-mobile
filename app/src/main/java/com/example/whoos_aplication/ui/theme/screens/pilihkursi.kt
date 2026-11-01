package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whoos_aplication.viewmodel.BookingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihKursiScreen(nav: NavController, vm: BookingViewModel) {
    val b            = vm.booking
    var selectedCar  by remember { mutableStateOf(if (b.wagonClass.contains("First")) "01" else "03") }
    var selectedSeat by remember { mutableStateOf("") }
    var selectedSeatId by remember { mutableIntStateOf(0) }
    val red          = Color(0xFFCC0000)

    // Debug: Log booking info
    LaunchedEffect(Unit) {
        println("DEBUG PilihKursi: scheduleId = ${b.scheduleId}")
        println("DEBUG PilihKursi: wagonClass = ${b.wagonClass}")
        println("DEBUG PilihKursi: trainCode = ${b.trainCode}")
    }

    // Fetch seats dari API saat wagon berubah
    LaunchedEffect(selectedCar, b.scheduleId) {
        if (b.scheduleId > 0) {
            println("DEBUG PilihKursi: Fetching seats for schedule ${b.scheduleId}, wagon $selectedCar")
            vm.fetchSeats(b.scheduleId, selectedCar)
        } else {
            println("DEBUG PilihKursi: scheduleId is 0, cannot fetch seats from API")
        }
    }

    // Configuration based on class
    val isFirstClass = b.wagonClass.contains("First", ignoreCase = true) || b.wagonClass.contains("Business", ignoreCase = true)
    
    // Gunakan data dari API jika tersedia, fallback ke hardcoded
    val availableSeatsMap = vm.availableSeats.associate { it.seat_number to it }
    
    val seatRows = if (isFirstClass) {
        // First Class: 2-1 configuration
        (1..5).map { row ->
            listOf("${row}A", "${row}C", null, "${row}F")
        }
    } else {
        // Economy: 3-2 configuration
        (1..10).map { row ->
            listOf("${row}A", "${row}B", "${row}C", null, "${row}D", "${row}F")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih kursi - ${b.wagonClass}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = red, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        if (selectedSeat.isNotEmpty() && selectedSeatId > 0) {
                            println("DEBUG: Saving seat - Number: $selectedSeat, ID: $selectedSeatId")
                            vm.selectSeat(selectedCar, selectedSeat, selectedSeatId)
                            nav.popBackStack()
                        } else {
                            println("DEBUG: Cannot save - Seat: $selectedSeat, ID: $selectedSeatId")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = red),
                    enabled = selectedSeat.isNotEmpty() && selectedSeatId > 0
                ) { 
                    Text(
                        if (selectedSeatId > 0) "Simpan Kursi ($selectedSeat)" else "Pilih Kursi Dulu",
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    ) 
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {

            // Info Kereta
            Card(Modifier.fillMaxWidth().padding(12.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Penumpang", color = Color.Gray, fontSize = 14.sp)
                        val displayName = if (b.passengers.isNotEmpty()) b.passengers.first().name else b.username
                        Text("$displayName ›", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Pilih Kereta", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val cars = if (isFirstClass) listOf("01", "02") else listOf("03", "04", "05", "06")
                        cars.forEach { car ->
                            val isSelected = selectedCar == car
                            OutlinedButton(
                                onClick = { selectedCar = car; selectedSeat = "" },
                                shape  = RoundedCornerShape(10.dp),
                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) red else Color.LightGray),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) Color(0xFFFFF0F0) else Color.White
                                )
                            ) { Text(car, color = if (isSelected) red else Color.DarkGray, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }

            // Legend
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendItem("Tersedia", Color(0xFFFFE0E0))
                LegendItem("Dipilih", red)
                LegendItem("Terisi", Color(0xFFDDDDDD))
            }

            Spacer(Modifier.height(16.dp))

            // Grid Kursi
            Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 20.dp)) {
                seatRows.forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        row.forEach { seat ->
                            if (seat == null) {
                                Spacer(Modifier.width(24.dp))
                            } else {
                                val isSelected = selectedSeat == seat
                                // Cek apakah kursi tersedia dari API
                                val seatData = availableSeatsMap[seat]
                                val isBooked = seatData?.is_available == false
                                
                                // Generate seat_id dari seat_number jika API tidak tersedia
                                val seatId = seatData?.id ?: run {
                                    // Fallback: generate ID dari seat number
                                    // Format: row number * 10 + column index
                                    val rowNum = seat.filter { it.isDigit() }.toIntOrNull() ?: 0
                                    val colChar = seat.filter { it.isLetter() }.firstOrNull() ?: 'A'
                                    val colIndex = when(colChar) {
                                        'A' -> 1
                                        'B' -> 2
                                        'C' -> 3
                                        'D' -> 4
                                        'F' -> 5
                                        else -> 0
                                    }
                                    // Generate unique ID: wagon_number * 1000 + row * 10 + col
                                    val wagonNum = selectedCar.toIntOrNull() ?: 3
                                    wagonNum * 1000 + rowNum * 10 + colIndex
                                }
                                
                                Box(
                                    Modifier.padding(4.dp).size(50.dp)
                                        .background(
                                            when {
                                                isSelected -> red
                                                isBooked -> Color(0xFFDDDDDD)
                                                else -> Color(0xFFFFE0E0)
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable(enabled = !isBooked) { 
                                            if (isSelected) {
                                                println("DEBUG: Deselecting seat $seat")
                                                selectedSeat = ""
                                                selectedSeatId = 0
                                            } else {
                                                println("DEBUG: Selecting seat $seat with ID $seatId (from API: ${seatData != null})")
                                                selectedSeat = seat
                                                selectedSeatId = seatId
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(if (isBooked) "🚫" else "🪑", fontSize = 16.sp)
                                        Text(seat, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else if (isBooked) Color.Gray else red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).background(color, RoundedCornerShape(3.dp)))
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}
