package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BayarScreen(nav: NavController, vm: BookingViewModel) {
    val b   = vm.booking
    val red = Color(0xFFCC0000)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bayar", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.Close, null) } }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(bottom = 16.dp)) {

            // Header merah
            item {
                Surface(color = red, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🛒", fontSize = 16.sp)
                            Text("Selesaikan Pembayaran", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
                Surface(color = Color(0xFFF5F5F5), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text("Penumpang :  ${if (vm.passengers.isNotEmpty()) vm.passengers.first().name else b.username}", fontSize = 13.sp)
                        Text("Nomor Reservasi :  ${b.reservationNo}", fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(WhooshUtils.formatRupiah(b.totalPrice), color = red, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                    Text("Hitung Mundur Pembayaran : 16:38", color = Color.Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Sesuai kebijakan KCIC, pembayaran pemesanan tiket tidak bisa dimulai\napabila sisa waktu pemesanan kurang dari 5 menit",
                        color = red, fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // Virtual Account header
            item {
                Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("💳", fontSize = 18.sp)
                        Text("Virtual Account", color = red, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Daftar bank
            items(WhooshUtils.banks) { bank ->
                Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp)
                    .clickable { nav.navigate(Screen.VirtualAccount.route) },
                    shape  = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Bank icon placeholder
                        Surface(color = Color(0xFFEEEEEE), shape = RoundedCornerShape(6.dp), modifier = Modifier.size(44.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(bank.take(1), fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 18.sp)
                            }
                        }
                        Text(bank, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}