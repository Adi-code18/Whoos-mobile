package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whoos_aplication.model.BookingData
import com.example.whoos_aplication.utils.WhooshUtils
import com.example.whoos_aplication.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(nav: NavController, vm: BookingViewModel, resNo: String) {
    val red = Color(0xFFCC0000)
    val booking = vm.bookingHistory.find { it.reservationNo == resNo }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontWeight = FontWeight.Normal, color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { /* Handle rules */ }) {
                        Text("Rules", color = Color.White, fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = red)
            )
        }
    ) { padding ->
        if (booking == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Ticket not found")
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF1F4F9))
                    .verticalScroll(rememberScrollState())
            ) {
                // Red Header Section with Order Info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(red, red.copy(alpha = 0.8f), Color(0xFFF1F4F9))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text("Order Number:  ${booking.reservationNo}", color = Color.White, fontSize = 13.sp)
                        Text("Order Time:  ${booking.createdAt.ifEmpty { "-" }}", color = Color.White, fontSize = 13.sp)
                        Spacer(Modifier.height(16.dp))
                    }
                }

                Column(
                    Modifier
                        .offset(y = (-16).dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // One card per passenger (or at least one if passengers list is empty)
                    if (booking.passengers.isEmpty()) {
                        PassengerTicketCard(booking, null, red)
                    } else {
                        booking.passengers.forEach { passenger ->
                            PassengerTicketCard(booking, passenger, red)
                        }
                    }
                    
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun PassengerTicketCard(b: BookingData, p: com.example.whoos_aplication.model.Passenger?, red: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Top Section: Time & Route
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FBFF))
                    .padding(16.dp)
            ) {
                // Format tanggal dari YYYY-MM-DD ke format yang lebih readable
                val formattedDate = try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
                    val date = inputFormat.parse(b.date)
                    if (date != null) outputFormat.format(date) else b.date
                } catch (e: Exception) { b.date }

                // Hitung durasi perjalanan
                val durationLabel = WhooshUtils.getDurationLabel(b.from, b.to)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formattedDate, color = Color.Gray, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(durationLabel, color = Color.Gray, fontSize = 13.sp)
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(b.departureTime, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Text("WIB", color = red, fontSize = 10.sp, modifier = Modifier.padding(start = 2.dp, bottom = 4.dp))
                        }
                        Text(b.from, color = Color.Gray, fontSize = 14.sp)
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(b.trainCode, color = Color.Gray, fontSize = 12.sp)
                        Canvas(modifier = Modifier.width(40.dp).height(1.dp)) {
                            drawLine(color = Color.LightGray, start = androidx.compose.ui.geometry.Offset(0f, size.height/2), end = androidx.compose.ui.geometry.Offset(size.width, size.height/2))
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(b.arrivalTime, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Text("WIB", color = red, fontSize = 10.sp, modifier = Modifier.padding(start = 2.dp, bottom = 4.dp))
                        }
                        Text(b.to, color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            // Middle Section: Passenger Details
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(p?.name ?: b.username, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(0.5.dp, Color.LightGray),
                                color = Color.Transparent
                            ) {
                                Text(
                                    "Adult ticket", 
                                    fontSize = 10.sp, 
                                    color = Color.Gray,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text("Identity No. ${p?.idNumber?.let { it.take(4) + "****" + it.takeLast(2) } ?: ""}", 
                            color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 2.dp))
                        Text("Coach ${WhooshUtils.decodeWagonNumber(b.selectedSeat)} | ${b.wagonClass}", color = Color.Gray, fontSize = 13.sp)
                        Text("Seat ${WhooshUtils.decodeSeatId(b.selectedSeat)}", color = Color.Gray, fontSize = 13.sp)
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(WhooshUtils.formatRupiah(b.totalPrice / (if(b.quantity > 0) b.quantity else 1)), 
                            fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        val statusText = when (b.bookingStatus.lowercase()) {
                            "paid" -> "Success"
                            "pending" -> "Pending"
                            "rescheduled", "rescheduling" -> "Rescheduled"
                            "cancelled", "refunded" -> "Refunded"
                            else -> b.bookingStatus.replaceFirstChar { it.uppercase() }
                        }
                        val statusColor = when (b.bookingStatus.lowercase()) {
                            "paid" -> Color(0xFF2E7D32)
                            "pending" -> Color(0xFFE65100)
                            "cancelled", "refunded" -> Color(0xFFCC0000)
                            else -> Color.Gray
                        }
                        Text(statusText, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Ticket", color = Color.Gray, fontSize = 11.sp)
                        Text("Checked", color = Color(0xFF3B82F6), fontSize = 11.sp)
                    }
                }
            }
            
            HorizontalDivider(color = Color(0xFFF1F4F9))

            // QR Code Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tunjukkan QR Code ini saat boarding",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SimulatedQRCode(
                    code = b.reservationNo,
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color.White)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = b.reservationNo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            }

            HorizontalDivider(color = Color(0xFFF1F4F9))
            
            // Bottom Section: Payment Amount
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total payment amount:", color = Color.Gray, fontSize = 14.sp)
                Text(WhooshUtils.formatRupiah(b.totalPrice), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SimulatedQRCode(code: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val sizePx = size.width
        // Clear background
        drawRect(Color.White)

        // Draw outer border
        drawRect(Color.Black, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))

        val modules = 21 // 21x21 grid for Version 1 QR code
        val cellSize = sizePx / modules

        // Function to draw a pixel module
        fun drawModule(row: Int, col: Int, color: Color = Color.Black) {
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(col * cellSize, row * cellSize),
                size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
            )
        }

        // Draw standard QR Finder Patterns (7x7 modules)
        fun drawFinderPattern(rowOffset: Int, colOffset: Int) {
            for (r in 0 until 7) {
                for (c in 0 until 7) {
                    val isBlack = (r == 0 || r == 6 || c == 0 || c == 6) || // Outer ring
                            (r >= 2 && r <= 4 && c >= 2 && c <= 4) // Center dot
                    if (isBlack) {
                        drawModule(rowOffset + r, colOffset + c)
                    }
                }
            }
        }

        // 1. Top-Left Finder Pattern (0, 0)
        drawFinderPattern(0, 0)

        // 2. Top-Right Finder Pattern (0, modules - 7)
        drawFinderPattern(0, modules - 7)

        // 3. Bottom-Left Finder Pattern (modules - 7, 0)
        drawFinderPattern(modules - 7, 0)

        // 4. Fill the rest with pseudo-random noise based on the booking code hash
        val random = java.util.Random(code.hashCode().toLong())
        for (r in 0 until modules) {
            for (c in 0 until modules) {
                // Skip finder patterns
                val inTopLeft = r < 8 && c < 8
                val inTopRight = r < 8 && c >= modules - 8
                val inBottomLeft = r >= modules - 8 && c < 8
                if (!inTopLeft && !inTopRight && !inBottomLeft) {
                    // Timing patterns or random dots
                    if (random.nextBoolean()) {
                        drawModule(r, c)
                    }
                }
            }
        }
    }
}
