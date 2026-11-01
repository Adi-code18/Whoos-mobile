package com.example.whoos_aplication.ui.theme.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.whoos_aplication.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whoos_aplication.model.Schedule
import com.example.whoos_aplication.navigation.Screen
import com.example.whoos_aplication.utils.WhooshUtils
import com.example.whoos_aplication.viewmodel.BookingViewModel
import java.util.Calendar

val WhooshRed = Color(0xFFCC0000)

val stationDisplayList = listOf(
    "Halim", "Karawang", "Padalarang", "Tegalluar"
)

fun stationKey(display: String) =
    if (display.contains("Tegalluar")) "Tegalluar" else display

data class ScheduleCardItem(
    val trainCode: String,
    val from     : String,
    val to       : String,
    val depTime  : String,
    val arrTime  : String,
    val duration : String,
    val price    : String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(nav: NavController, vm: BookingViewModel) {
    val context = LocalContext.current
    val cal     = Calendar.getInstance()

    var from         by remember { mutableStateOf("Halim") }
    var to           by remember { mutableStateOf("Tegalluar Summarecon") }
    var dateRaw      by remember { mutableStateOf("") }
    var dateLabel    by remember { mutableStateOf("") }
    var quantity     by remember { mutableIntStateOf(1) }
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded   by remember { mutableStateOf(false) }
    var error        by remember { mutableStateOf("") }

    val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
    val days   = listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")

    val popularSchedules = vm.popularSchedules.map { s ->
        // Hitung durasi dari departure_time dan arrival_time
        val duration = try {
            val depParts = s.departure_time.split(":")
            val arrParts = s.arrival_time.split(":")
            val depMinutes = depParts[0].toInt() * 60 + depParts[1].toInt()
            val arrMinutes = arrParts[0].toInt() * 60 + arrParts[1].toInt()
            val diffMinutes = if (arrMinutes >= depMinutes) {
                arrMinutes - depMinutes
            } else {
                (24 * 60 - depMinutes) + arrMinutes
            }
            "${diffMinutes}m"
        } catch (e: Exception) {
            "N/A"
        }
        
        ScheduleCardItem(
            trainCode = s.train_code,
            from      = s.from_station,
            to        = s.to_station,
            depTime   = s.departure_time,
            arrTime   = s.arrival_time,
            duration  = duration,
            price     = WhooshUtils.formatRupiah(s.price)
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F8F8),
        bottomBar = { WhooshBottomBar(nav = nav, currentRoute = Screen.Dashboard.route) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            // ── BANNER ──
            Box(
                Modifier.fillMaxWidth().height(180.dp)
                    .background(Brush.verticalGradient(listOf(WhooshRed, Color(0xFFAA0000))))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kereta),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // ── SEARCH CARD (PUTIH) ──
            Card(
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 14.dp).offset(y = (-30).dp),
                shape     = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // FROM
                        Column(Modifier.weight(1f)) {
                            Text("From", color = Color.Gray, fontSize = 11.sp)
                            ExposedDropdownMenuBox(expanded = fromExpanded, onExpandedChange = { fromExpanded = it }) {
                                Text(from, fontWeight = FontWeight.Bold, fontSize = 17.sp,
                                    modifier = Modifier.fillMaxWidth().menuAnchor().clickable { fromExpanded = true })
                                ExposedDropdownMenu(expanded = fromExpanded, onDismissRequest = { fromExpanded = false }) {
                                    vm.stations.forEach { s ->
                                        DropdownMenuItem(text = { Text(s.station_name) },
                                            onClick = { from = s.station_name; if (to == s.station_name) to = ""; fromExpanded = false; error = "" },
                                            enabled = s.station_name != to)
                                    }
                                }
                            }
                        }

                        // SWAP ICON
                        IconButton(onClick = { val t = from; from = to; to = t },
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .size(36.dp)
                                .background(Color.White, CircleShape)
                                .border(1.dp, Color(0xFFEEEEEE), CircleShape)) {
                            Icon(Icons.Default.SwapHoriz, null, tint = WhooshRed, modifier = Modifier.size(22.dp))
                        }

                        // TO
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text("To", color = Color.Gray, fontSize = 11.sp)
                            ExposedDropdownMenuBox(expanded = toExpanded, onExpandedChange = { toExpanded = it }) {
                                Text(to.ifEmpty { "Pilih Tujuan" }, 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 17.sp,
                                    textAlign = TextAlign.End,
                                    color = if (to.isEmpty()) Color.LightGray else Color.Black,
                                    modifier = Modifier.fillMaxWidth().menuAnchor().clickable { toExpanded = true })
                                ExposedDropdownMenu(expanded = toExpanded, onDismissRequest = { toExpanded = false }) {
                                    vm.stations.forEach { s ->
                                        DropdownMenuItem(text = { Text(s.station_name) },
                                            onClick = { to = s.station_name; toExpanded = false; error = "" },
                                            enabled = s.station_name != from)
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

                    // DATE
                    Text("Departure", color = Color.Gray, fontSize = 11.sp)
                    Box(Modifier.fillMaxWidth().clickable {
                        val picker = DatePickerDialog(context, { _, y, m, d ->
                            val c = Calendar.getInstance().apply { set(y, m, d) }
                            dateLabel = "${days[c.get(Calendar.DAY_OF_WEEK)-1]}, $d ${months[m]} $y"
                            dateRaw   = "%02d/%02d/%04d".format(d, m+1, y)
                            error     = ""
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                        picker.datePicker.minDate = cal.timeInMillis
                        picker.show()
                    }) {
                        Text(if (dateLabel.isEmpty()) "Select Date" else dateLabel,
                            fontWeight = FontWeight.Bold, fontSize = 17.sp,
                            modifier = Modifier.padding(vertical = 4.dp))
                    }

                    if (error.isNotEmpty()) {
                        Text(error, color = WhooshRed, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (dateRaw.isEmpty()) error = "Please select departure date"
                            else {
                                vm.updateForm(stationKey(from), stationKey(to), dateRaw, quantity)
                                nav.navigate(Screen.SearchResult.route)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = WhooshRed)
                    ) { Text("Search", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                }
            }

            // ── RUNNING TEXT (MARQUEE) ──
            Surface(
                color = Color(0xFFFFF0F0),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).offset(y = (-15).dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, null, tint = WhooshRed, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Please check your travel schedule again. Ensure your departure and arrival stations are correct before proceeding to payment.     ",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                    )
                }
            }

            // ── JADWAL POPULER ──
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Popular Schedules", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("See All", color = WhooshRed, fontSize = 13.sp)
            }
            Spacer(Modifier.height(10.dp))
            LazyRow(contentPadding = PaddingValues(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(popularSchedules) { s ->
                    SchedulePopularCard(item = s) {
                        // Jika user belum pilih tanggal, gunakan tanggal hari ini
                        val selectedDate = if (dateRaw.isEmpty()) {
                            val today = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                .format(java.util.Date())
                            today
                        } else {
                            dateRaw
                        }
                        vm.updateForm(stationKey(s.from), stationKey(s.to), selectedDate, quantity)
                        nav.navigate(Screen.SearchResult.route)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── REGULATIONS ──
            Text("Railway Regulations", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(10.dp))
            RegulationCard("Whoosh and Feeder Train Schedule", "High Speed Railway schedule and integration", Icons.Default.Schedule)
            RegulationCard("Refund / Reschedule", "Information about ticket changes", Icons.Default.Sync)
            
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun RegulationCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = WhooshRed) }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(desc, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SchedulePopularCard(item: ScheduleCardItem, onBook: () -> Unit) {
    Card(modifier = Modifier.width(200.dp), shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(12.dp)) {
            Text(item.trainCode, color = WhooshRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(item.depTime, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(item.from.take(8), color = Color.Gray, fontSize = 11.sp)
                }
                Icon(Icons.Default.ArrowForward, null, tint = Color.LightGray, modifier = Modifier.size(16.dp).align(Alignment.CenterVertically))
                Column(horizontalAlignment = Alignment.End) {
                    Text(item.arrTime, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(item.to.take(8), color = Color.Gray, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(item.price, color = WhooshRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Button(onClick = onBook, colors = ButtonDefaults.buttonColors(containerColor = WhooshRed),
                    contentPadding = PaddingValues(horizontal = 12.dp), modifier = Modifier.height(30.dp),
                    shape = RoundedCornerShape(6.dp)) {
                    Text("Book", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WhooshBottomBar(nav: NavController, currentRoute: String?) {
    val red = Color(0xFFCC0000)
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier.height(90.dp)
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Dashboard.route,
            onClick = { if (currentRoute != Screen.Dashboard.route) nav.navigate(Screen.Dashboard.route) },
            icon = { Icon(Icons.Default.Home, null, modifier = Modifier.size(26.dp)) },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = red, selectedTextColor = red, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = currentRoute == Screen.MyTicket.route,
            onClick = { if (currentRoute != Screen.MyTicket.route) nav.navigate(Screen.MyTicket.route) },
            icon = { Icon(Icons.AutoMirrored.Filled.Assignment, null, modifier = Modifier.size(26.dp)) },
            label = { Text("My Tickets", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = red, selectedTextColor = red, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Account.route,
            onClick = { if (currentRoute != Screen.Account.route) nav.navigate(Screen.Account.route) },
            icon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(26.dp)) },
            label = { Text("Me", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = red, selectedTextColor = red, indicatorColor = Color.Transparent)
        )
    }
}
