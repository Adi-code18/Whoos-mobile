package com.example.whoos_aplication.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whoos_aplication.ui.theme.screens.*
import com.example.whoos_aplication.viewmodel.BookingViewModel

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()
    val vm: BookingViewModel = viewModel()

    NavHost(
        navController    = nav,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route)          { SplashScreen(nav, vm) }
        composable(Screen.Login.route)           { LoginScreen(nav, vm) }
        composable(Screen.Dashboard.route)       { DashboardScreen(nav, vm) }
        composable(Screen.SearchResult.route)    { SearchResultScreen(nav, vm) }
        composable(Screen.Pemesanan.route)       { PemesananScreen(nav, vm) }
        composable(Screen.DaftarPenumpang.route) { DaftarPenumpangScreen(nav, vm) }
        composable(Screen.PenumpangForm.route)   { PenumpangFormScreen(nav, vm) }
        composable(Screen.PilihKursi.route)      { PilihKursiScreen(nav, vm) }
        composable(Screen.DalamProses.route)     { DalamProsesScreen(nav, vm) }
        composable(Screen.Bayar.route)           { BayarScreen(nav, vm) }
        composable(Screen.VirtualAccount.route)  { VirtualAccountScreen(nav, vm) }
        composable(Screen.MyTicket.route)        { MyTicketScreen(nav, vm) }
        composable(Screen.Account.route)         { AccountScreen(nav, vm) }
        composable(Screen.Register.route)        { RegisterScreen(nav, vm) }
        composable(Screen.TicketDetail.route + "/{resNo}") { backStackEntry ->
            val resNo = backStackEntry.arguments?.getString("resNo") ?: ""
            TicketDetailScreen(nav, vm, resNo)
        }
    }
}
