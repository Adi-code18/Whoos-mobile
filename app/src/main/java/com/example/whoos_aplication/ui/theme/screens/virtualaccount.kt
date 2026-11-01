package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
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
fun VirtualAccountScreen(nav: NavController, vm: BookingViewModel) {
    val b   = vm.booking
    val red = Color(0xFFCC0000)

    // VA number will be generated from backend
    val vaNumber = ""
    val expDate  = "${b.date} 08:19"
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pembayaran", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = { IconButton(onClick = { 
                    nav.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }) { Icon(Icons.Default.Close, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Header VA
            Surface(color = red, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)) {
                Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("💳", fontSize = 16.sp)
                    Text("Virtual Account", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            // Detail VA
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp, topStart = 0.dp, topEnd = 0.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Detil pembayaran anda adalah demikian:", color = Color.DarkGray, fontSize = 13.sp)

                    VARow("Bank", "SAHABAT_SAMPOERNA")

                    // VA number with copy button
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("No Virtual Account", color = Color.Gray, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(": $vaNumber", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Icon(Icons.Default.ContentCopy, "Copy", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }

                    VARow("Nominal", WhooshUtils.formatRupiah(b.totalPrice) + ",-")
                    VARow("Batas Waktu", expDate)
                }
            }

            Spacer(Modifier.weight(1f))

            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = red, fontSize = 12.sp)
            }
            
            // Cek pembayaran button
            Button(
                onClick = {
                    loading = true
                    vm.completePayment(
                        onSuccess = {
                            loading = false
                            nav.navigate(Screen.MyTicket.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = false }
                            }
                        },
                        onError = {
                            loading = false
                            errorMsg = it
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !loading,
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = red)
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Selesaikan Pembayaran", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun VARow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(": $value", fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}