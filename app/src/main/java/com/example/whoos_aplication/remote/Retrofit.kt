package com.example.whoos_aplication.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

// 1. Model Respon
data class RegisterResponse(
    val status: String,
    val message: String
)

data class LoginResponse(
    val status: String,
    val message: String,
    val user_id: Int?,
    val username: String?,
    val email: String?
)

data class PassengerRequest(
    val full_name: String,
    val id_number: String,
    val seat_id: Int  // Ubah dari seat_number (String) ke seat_id (Int)
)

data class BookingRequest(
    val user_id: Int,
    val schedule_id: Int,
    val total_price: Int,
    val class_name: String,
    val passengers: List<PassengerRequest>,
    val status: String = "pending"
)

data class BookingResponse(
    val status: String,
    val message: String,
    val booking_code: String? = null
)

data class StationResponse(
    val id: Int,
    val station_name: String
)

data class ScheduleResponse(
    val id: Int,
    val train_code: String,
    val from_station: String,
    val to_station: String,
    val departure_time: String,
    val arrival_time: String,
    val price: Int
)

data class SeatResponse(
    val id: Int,           // seat_id dari database
    val seat_number: String, // "1A", "1B", dll
    val wagon_number: String, // "01", "02", dll
    val is_available: Boolean // true/false
)

data class WagonClassResponse(
    val id: Int,
    val class_name: String,        // "Ekonomi", "Eksekutif", "Busines"
    val price_multiplier: Float,   // 1.0, 1.5, 1.7
    val description: String
)

// 2. API Interface
interface ApiService {
    @FormUrlEncoded
    @POST("register.php")
    suspend fun register(
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("createbooking.php")
    suspend fun createBooking(
        @Body request: BookingRequest
    ): BookingResponse

    @FormUrlEncoded
    @POST("cancel_booking.php")
    suspend fun cancelBooking(
        @Field("reservation_no") reservationNo: String,
        @Field("refund_amount") refundAmount: Int
    ): BookingResponse

    @FormUrlEncoded
    @POST("update_booking_status.php")
    suspend fun updateBookingStatus(
        @Field("reservation_no") reservationNo: String,
        @Field("status") status: String
    ): BookingResponse

    @FormUrlEncoded
    @POST("update_booking_date.php")
    suspend fun rescheduleBooking(
        @Field("reservation_no") reservationNo: String,
        @Field("new_date") newDate: String
    ): BookingResponse

    @FormUrlEncoded
    @POST("my_bookings.php")
    suspend fun getBookings(
        @Field("email") email: String
    ): List<BookingDataRemote>

    @GET("get_station.php")
    suspend fun getStations(): List<StationResponse>

    @FormUrlEncoded
    @POST("get_schedules.php")
    suspend fun getSchedules(
        @Field("from_station") from: String,
        @Field("to_station") to: String,
        @Field("date") date: String
    ): List<ScheduleResponse>

    @FormUrlEncoded
    @POST("get_seats.php")
    suspend fun getSeats(
        @Field("schedule_id") scheduleId: Int,
        @Field("wagon_number") wagonNumber: String
    ): List<SeatResponse>

    @GET("get_classes.php")
    suspend fun getWagonClasses(): List<WagonClassResponse>

    
}

data class BookingDataRemote(
    val reservation_no: String,
    val from_station: String,
    val to_station: String,
    val date: String,
    val departure_time: String,
    val arrival_time: String,
    val train_code: String,
    val wagon_class: String,
    val seat: String,
    val total_price: Int,
    val status: String,
    val created_at: String = ""
)

// 3. Retrofit Client
object RetrofitClient {
    // Gunakan 10.0.2.2 untuk Android Emulator atau 192.168.x.x untuk device fisik
    // Untuk Laragon default biasanya localhost atau 127.0.0.1
    private const val BASE_URL = "http://10.10.45.1/apiwhoosmobile/"

    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .retryOnConnectionFailure(true)
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
