package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whoos_aplication.R
import com.example.whoos_aplication.model.BookingData
import com.example.whoos_aplication.navigation.Screen
import com.example.whoos_aplication.utils.WhooshUtils
import com.example.whoos_aplication.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketScreen(nav: NavController, vm: BookingViewModel) {
    val red = Color(0xFFCC0000)
    var selectedTab by remember { mutableIntStateOf(1) } // Default ke 'Paid' sesuai gambar (indeks 1)
    val tabs = listOf("Unpaid", "Paid", "History")

    var showCancelDialog by remember { mutableStateOf<BookingData?>(null) }
    var showRescheduleDialog by remember { mutableStateOf<BookingData?>(null) }
    var showContinuePayDialog by remember { mutableStateOf<BookingData?>(null) }

    LaunchedEffect(Unit) {
        vm.fetchBookingHistory()
    }

    // Filter data berdasarkan status dan tanggal keberangkatan
    // Unpaid: hanya pending yang tanggal keberangkatannya belum lewat
    val unpaidList  = vm.bookingHistory.filter { 
        it.bookingStatus.equals("pending", ignoreCase = true) && !WhooshUtils.hasDeparted(it.date, it.departureTime)
    }
    // Paid: paid & rescheduled yang tanggal keberangkatannya belum lewat
    val paidList    = vm.bookingHistory.filter { 
        (it.bookingStatus.equals("paid", ignoreCase = true) || it.bookingStatus.equals("rescheduled", ignoreCase = true)) && 
        !WhooshUtils.hasDeparted(it.date, it.departureTime)
    }
    // History: semua yang sudah selesai/dibatalkan, PLUS paid, rescheduled & pending yang tanggal keberangkatannya sudah lewat
    val historyList = vm.bookingHistory.filter { 
        it.bookingStatus.equals("completed", ignoreCase = true) ||
        it.bookingStatus.equals("cancelled", ignoreCase = true) ||
        it.bookingStatus.equals("refunded", ignoreCase = true) ||
        ((it.bookingStatus.equals("paid", ignoreCase = true) || it.bookingStatus.equals("rescheduled", ignoreCase = true)) && WhooshUtils.hasDeparted(it.date, it.departureTime)) ||
        (it.bookingStatus.equals("pending", ignoreCase = true) && WhooshUtils.hasDeparted(it.date, it.departureTime))
    }

    val currentList = when (selectedTab) {
        0 -> unpaidList
        1 -> paidList
        else -> historyList
    }

    if (showCancelDialog != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = null },
            title = { Text("Batalkan Tiket") },
            text = { Text("Apakah Anda yakin ingin membatalkan tiket ini? Akan ada potongan biaya administrasi sebesar 25%.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog?.let { b ->
                        vm.cancelBooking(b.reservationNo, b.totalPrice, onSuccess = {
                            showCancelDialog = null
                        }, onError = { /* handle error */ })
                    }
                }) { Text("Ya, Batal", color = red) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = null }) { Text("Kembali") }
            }
        )
    }

    // Dialog Lanjutkan Pembayaran
    if (showContinuePayDialog != null) {
        AlertDialog(
            onDismissRequest = { showContinuePayDialog = null },
            title = { Text("Lanjutkan Pembayaran") },
            text = { Text("Apakah Anda yakin ingin melanjutkan pembayaran untuk tiket ini?") },
            confirmButton = {
                TextButton(onClick = {
                    showContinuePayDialog?.let { b ->
                        vm.continuePayment(b.reservationNo, onSuccess = {
                            showContinuePayDialog = null
                            selectedTab = 1 // Pindah ke tab Paid
                        }, onError = { /* handle error */ })
                    }
                }) { Text("Ya, Bayar", color = red) }
            },
            dismissButton = {
                TextButton(onClick = { showContinuePayDialog = null }) { Text("Kembali") }
            }
        )
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    if (showRescheduleDialog != null) {
        AlertDialog(
            onDismissRequest = { showRescheduleDialog = null },
            title = { Text("Reschedule Tiket") },
            text = { Text("Apakah Anda yakin ingin mengubah jadwal? Jadwal baru hanya memajukan tanggal tiket saat ini.") },
            confirmButton = {
                TextButton(onClick = {
                    val b = showRescheduleDialog
                    showRescheduleDialog = null
                    if (b != null) {
                        val calendar = java.util.Calendar.getInstance()
                        // Minimal tanggal besok
                        val tomorrowMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
                        
                        val datePickerDialog = android.app.DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val formattedDate = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                                vm.rescheduleBooking(b.reservationNo, formattedDate, onSuccess = {
                                    android.widget.Toast.makeText(context, "Reschedule Berhasil ke $formattedDate!", android.widget.Toast.LENGTH_SHORT).show()
                                }, onError = { err ->
                                    android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_SHORT).show()
                                })
                            },
                            calendar.get(java.util.Calendar.YEAR),
                            calendar.get(java.util.Calendar.MONTH),
                            calendar.get(java.util.Calendar.DAY_OF_MONTH)
                        )
                        datePickerDialog.datePicker.minDate = tomorrowMillis
                        datePickerDialog.show()
                    }
                }) { Text("Ya, Ubah", color = red) }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = null }) { Text("Kembali") }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF1F4F9),
        topBar = {
            Column(Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text("My Tickets", fontWeight = FontWeight.Normal, fontSize = 18.sp) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = red,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = red
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    color = if (selectedTab == index) red else Color.Gray,
                                    fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        },
        bottomBar = { WhooshBottomBar(nav = nav, currentRoute = Screen.MyTicket.route) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (currentList.isEmpty()) {
                // Tampilan No Data sesuai gambar
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gunakan icon search sebagai placeholder jika gambar tidak ada
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = Color.LightGray.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("No Data", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentList) { booking ->
                        TicketHistoryCard(
                            booking,
                            onReschedule = { showRescheduleDialog = booking },
                            onCancel = { showCancelDialog = booking },
                            onContinuePayment = { showContinuePayDialog = booking },
                            onClick = {
                                nav.navigate(Screen.TicketDetail.route + "/${booking.reservationNo}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketHistoryCard(b: BookingData, onReschedule: () -> Unit, onCancel: () -> Unit, onContinuePayment: () -> Unit = {}, onClick: () -> Unit) {
    val canAction = WhooshUtils.canAction(b.date, b.departureTime)
    val isPaid = b.bookingStatus.equals("paid", ignoreCase = true) || b.bookingStatus.equals("rescheduled", ignoreCase = true)
    val isPending = b.bookingStatus.equals("pending", ignoreCase = true)
    val isRescheduled = b.bookingStatus.equals("rescheduled", ignoreCase = true)
    // Cek apakah tanggal keberangkatan sudah lewat (tiket expired)
    val isExpired = WhooshUtils.hasDeparted(b.date, b.departureTime)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(bottom = 12.dp)) {
            // Header Row
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Label "Ticket" with status color
                Surface(
                    color = when {
                        isPending && !isExpired -> Color(0xFFE65100) // Orange for pending
                        isPending && isExpired -> Color(0xFF9E9E9E) // Gray for expired pending
                        isRescheduled -> Color(0xFF2E7D32) // Green for rescheduled
                        else -> Color(0xFF94A3B8) // Default blue-gray
                    },
                    shape = RoundedCornerShape(topStart = 4.dp, bottomEnd = 12.dp),
                    modifier = Modifier.offset(x = (-12).dp, y = (-8).dp)
                ) {
                    Text(
                        when {
                            isPending && isExpired -> "Expired"
                            isPending -> "Pending"
                            isRescheduled -> "Rescheduled"
                            else -> "Ticket"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
                
                // Reservation No
                Text(
                    b.reservationNo,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Route Row (Station names and Train Code)
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(b.from, fontWeight = FontWeight.Normal, color = Color.Gray, fontSize = 14.sp)
                
                // Train indicator icon (simulated)
                Row(
                    Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(b.trainCode, color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Canvas(modifier = Modifier.width(40.dp).height(1.dp)) {
                        drawLine(color = Color.LightGray, start = androidx.compose.ui.geometry.Offset(0f, size.height/2), end = androidx.compose.ui.geometry.Offset(size.width, size.height/2))
                    }
                }
                
                Text(b.to, fontWeight = FontWeight.Normal, color = Color.Gray, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }

            Spacer(Modifier.height(12.dp))

            // Info rows
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Departure:  ${b.departureTime} ${b.date}", color = Color.LightGray, fontSize = 13.sp)
                
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${b.quantity} tickets  ${if(b.passengers.isNotEmpty()) b.passengers.first().name else b.username} MT", 
                        color = Color.LightGray, fontSize = 13.sp)
                }
            }

            // Actions for Paid Tickets
            if (isPaid && canAction) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCancel) {
                        Text("Batal", color = Color.Gray, fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onReschedule,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Reschedule", color = Color.White, fontSize = 12.sp)
                    }
                }
            } else if (isPaid && !canAction) {
                Text(
                    "Batas waktu reschedule berakhir",
                    color = Color.Red.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.End).padding(end = 16.dp, top = 4.dp)
                )
            }

            // Action for Pending/Unpaid Tickets - Lanjutkan Pembayaran
            if (isPending && !isExpired) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onContinuePayment,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Lanjutkan Pembayaran", color = Color.White, fontSize = 12.sp)
                    }
                }
            } else if (isPending && isExpired) {
                Text(
                    "Tiket telah kedaluwarsa",
                    color = Color.Red.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.End).padding(end = 16.dp, top = 4.dp)
                )
            }
        }
    }
}

