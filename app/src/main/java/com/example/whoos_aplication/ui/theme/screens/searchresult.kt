package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whoos_aplication.navigation.Screen
import com.example.whoos_aplication.utils.WhooshUtils
import com.example.whoos_aplication.viewmodel.BookingViewModel

//import androidx.navigation.NavController
//import com.example.whoos_aplication.navigation.Screen
//import com.example.whoos_aplication.viewmodel.BookingViewModel
//import com.example.whoos_aplication.utils.WhooshUtils

val Red = Color(0xFFCC0000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(nav: NavController, vm: BookingViewModel) {
    val b = vm.booking
    val schedules = vm.schedules
    var expandedIdx by remember { mutableStateOf(-1) }

    LaunchedEffect(b.from, b.to, b.date) {
        vm.fetchSchedules(b.from, b.to, b.date)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("${b.from}  →  ${b.to}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(b.date, color = Color.White.copy(.8f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.ChevronLeft, null, tint = Color.White) }
                    Text(b.date, color = Color.White, fontSize = 13.sp)
                    IconButton(onClick = {}) { Icon(Icons.Default.ChevronRight, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Red)
            )
        }
    ) { padding ->
        if (schedules.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No schedules found for this date.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                itemsIndexed(schedules) { idx, s ->
                    val isOpen = expandedIdx == idx
                    // Cek apakah waktu keberangkatan sudah terlewat
                    val isTimePassed = WhooshUtils.hasDeparted(b.date, s.departure_time)

                    Card(
                        Modifier.fillMaxWidth().clickable { expandedIdx = if (isOpen) -1 else idx },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        border = if (isOpen && !isTimePassed) BorderStroke(1.5.dp, Red) else null
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            // Badge "Waktu Sudah Terlewat" di kanan atas
                            if (isTimePassed) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Surface(
                                        color = Color(0xFFB71C1C),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "Waktu Sudah Terlewat",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                            }

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Waktu berangkat
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            s.departure_time,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 22.sp,
                                            color = if (isTimePassed) Color.Gray else Color.Unspecified
                                        )
                                        Text("WIB", color = if (isTimePassed) Color.Gray else Red, fontSize = 9.sp)
                                    }
                                    Text(s.from_station, color = Color.Gray, fontSize = 12.sp)
                                }
                                // Durasi + kode kereta
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(s.train_code, color = Color.Gray, fontSize = 11.sp)
                                    HorizontalDivider(Modifier.width(60.dp), color = if (isTimePassed) Color.Gray else Red, thickness = 1.5.dp)
                                    Text("46m", color = Color.Gray, fontSize = 11.sp) // Duration can be calculated if needed
                                }
                                // Waktu tiba
                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            s.arrival_time,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 22.sp,
                                            color = if (isTimePassed) Color.Gray else Color.Unspecified
                                        )
                                        Text("WIB", color = if (isTimePassed) Color.Gray else Red, fontSize = 9.sp)
                                    }
                                    Text(s.to_station, color = Color.Gray, fontSize = 12.sp)
                                }
                            }

                            Spacer(Modifier.height(6.dp))
                            Text("Stop Station ▾", color = if (isTimePassed) Color.Gray else Red, fontSize = 12.sp)

                            // Expanded: pilih kelas
                            if (isOpen) {
                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider()
                                Spacer(Modifier.height(8.dp))

                                // Jika waktu sudah terlewat, tampilkan pesan dan jangan tampilkan pilihan kelas
                                if (isTimePassed) {
                                    Text(
                                        "Tidak dapat memesan tiket ini karena waktu keberangkatan sudah terlewat.",
                                        color = Color.Red.copy(alpha = 0.7f),
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    // Gunakan data dari API jika tersedia
                                    val classes = if (vm.wagonClasses.isNotEmpty()) {
                                        vm.wagonClasses.map { wc ->
                                            Triple(
                                                wc.class_name,
                                                (s.price * wc.price_multiplier).toInt(),
                                                "Tersedia"
                                            )
                                        }
                                    } else {
                                        // Fallback ke hardcoded dengan nama yang benar
                                        listOf(
                                            Triple("Ekonomi", s.price, "Tersedia"),
                                            Triple("Eksekutif", (s.price * 1.5).toInt(), "Tersedia"),
                                            Triple("Busines", (s.price * 1.7).toInt(), "Tersedia")
                                        )
                                    }

                                    classes.forEachIndexed { index, (cls, pricePerTicket, availability) ->
                                        val totalPrice = pricePerTicket * b.quantity
                                        Row(
                                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(cls, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(WhooshUtils.formatRupiah(totalPrice), color = Red, fontSize = 12.sp)
                                                Text(availability, color = Color.Gray, fontSize = 11.sp)
                                            }
                                            Button(
                                                onClick = {
                                                    if (b.username.isEmpty()) {
                                                        nav.navigate(Screen.Login.route)
                                                    } else {
                                                        vm.selectSchedule(s.id, s.departure_time, s.arrival_time, s.train_code, s.price)
                                                        vm.selectClass(cls, pricePerTicket)
                                                        nav.navigate(Screen.Pemesanan.route)
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Red),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                                            ) { Text("Book", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                                        }
                                        if (index < classes.size - 1) HorizontalDivider(color = Color(0xFFEEEEEE))
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
