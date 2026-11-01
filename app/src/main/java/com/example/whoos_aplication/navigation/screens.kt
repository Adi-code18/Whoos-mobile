package com.example.whoos_aplication.navigation

sealed class Screen(val route: String) {
    object Splash          : Screen("splash")
    object Login           : Screen("login")
    object Register        : Screen("register")
    object Dashboard       : Screen("dashboard")
    object SearchResult    : Screen("search_result")
    object Pemesanan       : Screen("pemesanan")
    object DaftarPenumpang : Screen("daftar_penumpang")
    object PenumpangForm   : Screen("penumpang_form")
    object PilihKursi      : Screen("pilih_kursi")
    object DalamProses     : Screen("dalam_proses")
    object Bayar           : Screen("bayar")
    object VirtualAccount  : Screen("virtual_account")
    object MyTicket        : Screen("my_ticket")
    object Account         : Screen("account")
    object TicketDetail    : Screen("ticket_detail")
}
