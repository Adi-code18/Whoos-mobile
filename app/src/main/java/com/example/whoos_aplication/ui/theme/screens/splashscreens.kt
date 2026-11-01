package com.example.whoos_aplication.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.whoos_aplication.R
import com.example.whoos_aplication.navigation.Screen
import com.example.whoos_aplication.viewmodel.BookingViewModel
import com.example.whoos_aplication.utils.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(nav: NavController, vm: BookingViewModel) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    LaunchedEffect(Unit) {
        delay(2000)
        if (sessionManager.isLoggedIn()) {
            nav.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            nav.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }
    Box(
        Modifier.fillMaxSize().background(Color(0xFFFFF0F1)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.whooslogo),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
//            Text("🚄", fontSize = 80.sp)
//            Spacer(Modifier.height(16.dp))
            Spacer(Modifier.height(4.dp))
            Text("Kereta Cepat Indonesia", color = Color.White.copy(.8f), fontSize = 14.sp)
        }
    }
}