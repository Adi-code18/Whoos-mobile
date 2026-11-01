package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.delay

//import androidx.navigation.NavController
//import com.example.whoos_aplication.navigation.Screen
//import com.example.whoos_aplication.viewmodel.BookingViewModel
//import com.example.whoos_aplication.utils.WhooshUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DalamProsesScreen(nav: NavController, vm: BookingViewModel) {
    val b   = vm.booking
    val red = Color(0xFFCC0000)
    val context = androidx.compose.ui.platform.LocalContext.current

    // Countdown timer: 20 menit
    var totalSeconds by remember { mutableIntStateOf(20 * 60) }
    LaunchedEffect(Unit) {
        while (totalSeconds > 0) { 
            delay(1000)
            totalSeconds-- 
        }
        // Waktu habis, otomatis tunda ticket ke unpaid
        vm.tundaBooking(
            onSuccess = {
                android.widget.Toast.makeText(context, "Waktu pembayaran habis, tiket ditunda", android.widget.Toast.LENGTH_LONG).show()
                nav.navigate(Screen.MyTicket.route) {
                    popUpTo(Screen.Dashboard.route)
                }
            },
            onError = {}
        )
    }
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dalam proses", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = red)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Harga Tiket", color = Color.Gray, fontSize = 14.sp)
                        Text(WhooshUtils.formatRupiah(b.pricePerTicket), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total harga", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(WhooshUtils.formatRupiah(b.totalPrice), fontWeight = FontWeight.ExtraBold, color = red, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { nav.navigate(Screen.Dashboard.route) { popUpTo(Screen.Dashboard.route) } },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Batal") }

                        OutlinedButton(
                            onClick = {
                                vm.tundaBooking(
                                    onSuccess = {
                                        android.widget.Toast.makeText(context, "Tiket ditunda pembayaran", android.widget.Toast.LENGTH_SHORT).show()
                                        nav.navigate(Screen.MyTicket.route) {
                                            popUpTo(Screen.Dashboard.route)
                                        }
                                    },
                                    onError = { err ->
                                        android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Tunda Ticket") }

                        Button(
                            onClick = { nav.navigate(Screen.Bayar.route) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape  = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = red)
                        ) { Text("Bayar", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Countdown
            Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🕐", fontSize = 16.sp)
                    Text("Menunggu pembayaran, waktu tersisa ", fontSize = 13.sp)
                    Text("$minutes", color = red, fontWeight = FontWeight.Bold)
                    Text(" menit ", fontSize = 13.sp)
                    Text("$seconds", color = red, fontWeight = FontWeight.Bold)
                    Text(" detik", fontSize = 13.sp)
                }
            }

            // Jadwal card
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(b.date, color = Color.Gray, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🕐", fontSize = 12.sp)
                            Text("${WhooshUtils.getTravelMinutes(b.from, b.to)} Menit", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(b.departureTime, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                                Text("WIB", color = red, fontSize = 10.sp)
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
                                Text("WIB", color = red, fontSize = 10.sp)
                            }
                            Text(b.to, color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Penumpang card
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Penumpang", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    val passengerName = if (vm.passengers.isNotEmpty()) vm.passengers.first().name else b.username
                    val passengerId   = if (vm.passengers.isNotEmpty()) vm.passengers.first().idNumber else ""
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(passengerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Surface(shape = RoundedCornerShape(4.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)) {
                                    Text("Tiket Dewasa", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp)
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text("No. Identitas   ${passengerId.take(4)}****${passengerId.takeLast(2)}", color = Color.Gray, fontSize = 12.sp)
                        }
                        Text(WhooshUtils.formatRupiah(b.pricePerTicket), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            // Prompt
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Catatan*", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    listOf(
                        "Mohon untuk menyelesaikan pembayaran sebelum waktu pembayaran habis.",
                        "Apabila terdapat pembayaran yang melebihi waktu yang sudah ditentukan, perusahaan berhak membatalkan transaksi tersebut.",
                        "Apabila masih ada transaksi yang belum diselesaikan, maka anda tidak dapat melakukan proses pemesanan untuk jadwal yang baru."
                    ).forEachIndexed { i, s ->
                        Text("${i + 1}. $s", fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}