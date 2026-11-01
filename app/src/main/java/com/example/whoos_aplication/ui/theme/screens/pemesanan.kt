package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemesananScreen(nav: NavController, vm: BookingViewModel) {
    val b = vm.booking
    
    // Gunakan data dari API jika tersedia, fallback ke hardcoded
    val classes = if (vm.wagonClasses.isNotEmpty()) {
        vm.wagonClasses.map { it.class_name }
    } else {
        listOf("Ekonomi", "Eksekutif", "Busines")
    }
    
    var selectedClass by remember { mutableStateOf(b.wagonClass.ifEmpty { classes.firstOrNull() ?: "Ekonomi" }) }
    
    // Hitung harga berdasarkan base price dari schedule dan multiplier dari class
    val basePrice = b.basePrice.takeIf { it > 0 } ?: b.pricePerTicket.takeIf { it > 0 } ?: 295_000
    val calculatedPrice = vm.calculatePrice(selectedClass, basePrice)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pemesanan", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        bottomBar = {
            val isPassengerSelected = b.passengers.isNotEmpty()
            val isSeatSelected = b.selectedSeat.isNotEmpty() && b.selectedSeatId > 0

            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Harga Tiket", color = Color.Gray)
                    Text(WhooshUtils.formatRupiah(calculatedPrice), fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { nav.navigate(Screen.PilihKursi.route) },
                    modifier = Modifier.fillMaxWidth(),
                    shape  = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFCC0000))
                ) { 
                    Text(
                        if (isSeatSelected) "Kursi Terpilih: Gerbong ${b.selectedCar}, Kursi ${b.selectedSeat}" else "Pilih tempat duduk", 
                        color = Color(0xFFCC0000), 
                        fontWeight = FontWeight.SemiBold
                    ) 
                }

                if (!isPassengerSelected || !isSeatSelected) {
                    Text(
                        text = when {
                            !isPassengerSelected && !isSeatSelected -> "Silakan pilih penumpang & tempat duduk terlebih dahulu"
                            !isPassengerSelected -> "Silakan pilih penumpang terlebih dahulu"
                            else -> "Silakan pilih tempat duduk terlebih dahulu"
                        },
                        color = Color(0xFFCC0000),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Button(
                    onClick = {
                        vm.selectClass(selectedClass, calculatedPrice)
                        nav.navigate(Screen.DalamProses.route)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000)),
                    enabled = isPassengerSelected && isSeatSelected
                ) { Text("Selanjutnya", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Info jadwal
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(b.date, color = Color.Gray, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(androidx.compose.material.icons.Icons.Default.ArrowBack,
                                null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Text("${WhooshUtils.getTravelMinutes(b.from, b.to)} Menit", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(b.departureTime, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                                Text("WIB", color = Color(0xFFCC0000), fontSize = 10.sp)
                            }
                            Text(b.from, color = Color.Gray, fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(b.trainCode, color = Color.Gray, fontSize = 12.sp)
                            HorizontalDivider(Modifier.width(60.dp), color = Color.Gray)
                            Text("→", color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(b.arrivalTime, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                                Text("WIB", color = Color(0xFFCC0000), fontSize = 10.sp)
                            }
                            Text(b.to, color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Pilih kelas
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                classes.forEach { cls ->
                    val price = vm.calculatePrice(cls, basePrice)
                    val isSelected = cls == selectedClass
                    OutlinedCard(
                        onClick = { 
                            selectedClass = cls
                            vm.selectClass(cls, price)
                        },
                        modifier = Modifier.weight(1f),
                        shape  = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) Color(0xFFCC0000) else Color.LightGray
                        ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) Color(0xFFFFF0F0) else Color.White
                        )
                    ) {
                        Box(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(10.dp)) {
                                Text(WhooshUtils.formatRupiah(price), fontWeight = FontWeight.Bold,
                                    color = Color(0xFFCC0000), fontSize = 13.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(cls, fontSize = 11.sp, color = Color.DarkGray)
                                Text("Tersedia", fontSize = 11.sp, color = Color.Gray)
                            }
                            if (isSelected) {
                                Box(Modifier.align(Alignment.BottomEnd).padding(4.dp)) {
                                    Surface(shape = RoundedCornerShape(topStart = 8.dp), color = Color(0xFFCC0000)) {
                                        Text("✓", color = Color.White, modifier = Modifier.padding(4.dp), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Penumpang
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Penumpang", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    
                    if (vm.booking.passengers.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            vm.booking.passengers.forEach { p ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp), 
                                    color = Color(0xFFF8F8F8),
                                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text(p.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(p.passengerType, color = Color.Gray, fontSize = 12.sp)
                                        }
                                        Text(p.idNumber.take(4) + "****", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    
                    OutlinedButton(
                        onClick = { nav.navigate(Screen.DaftarPenumpang.route) },
                        modifier = Modifier.fillMaxWidth(),
                        shape  = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFCC0000))
                    ) { 
                        Text(
                            if(vm.booking.passengers.isEmpty()) "Pilihan penumpang" else "Ubah Pilihan Penumpang", 
                            color = Color(0xFFCC0000)
                        ) 
                    }
                }
            }

            // Syarat & Ketentuan
            Card(
                Modifier.fillMaxWidth(),
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Persyaratan Pemesanan & Pembatalan tiket", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    listOf(
                        "Bayi yaitu usia dibawah 3 (tiga) tahun.",
                        "Dewasa yaitu usia diatas 17 (tujuh belas) tahun atau lebih",
                        "Nama dan Nomor identitas harus sesuai dengan bukti identitas (KTP/Paspor)",
                        "Proses Ubah jadwal dan Pembatalan tiket hanya dapat dilakukan di Ticket Counter stasiun terdekat"
                    ).forEachIndexed { i, s ->
                        Text("${i + 1}. $s", fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}