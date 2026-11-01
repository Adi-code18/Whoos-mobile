# Testing Checklist - Seat Selection & Payment

## Quick Test Steps

### ✅ Complete Flow Test
1. **Login**
   - Use existing account or register new one
   
2. **Search Schedule**
   - From: Halim
   - To: Tegalluar
   - Date: Any date
   - Click "Cari Jadwal"

3. **Select Schedule**
   - Choose any schedule from results
   - Select a class (First/Business/Economy)
   - Click "Pesan Sekarang"

4. **Add/Select Passengers**
   - Click "Pilihan penumpang"
   - Add new passenger or select existing
   - Make sure at least 1 passenger is selected

5. **Select Seat** ⭐ CRITICAL STEP
   - Click "Pilih tempat duduk"
   - **Open Logcat** and filter by "DEBUG"
   - Click on any available seat (pink color)
   - **Verify in Logcat**: Should see "Selecting seat XX with ID YYYY"
   - Click "Simpan Kursi"
   - **Verify in Logcat**: Should see "Saving seat - Number: XX, ID: YYYY"

6. **Proceed to Payment**
   - Click "Selanjutnya"
   - Review booking details
   - Click "Bayar Sekarang"

7. **Complete Payment**
   - **Open Logcat** before clicking payment
   - Click "Cek Pembayaran"
   - **Verify in Logcat**: Should see "Booking selectedSeatId: YYYY" (NOT 0)
   - **Expected**: Payment succeeds, navigates to My Ticket

## Logcat Messages to Look For

### ✅ SUCCESS Indicators
```
DEBUG PilihKursi: scheduleId = 123
DEBUG: Selecting seat 1A with ID 1010
DEBUG: Saving seat - Number: 1A, ID: 1010
DEBUG ViewModel: Booking updated - selectedSeatId: 1010
DEBUG Payment: Booking selectedSeatId: 1010
DEBUG Payment: Passenger John - using seatId: 1010
```

### ❌ FAILURE Indicators
```
DEBUG PilihKursi: scheduleId = 0  ❌ Schedule not selected
DEBUG: Selecting seat 1A with ID 0  ❌ ID generation failed
DEBUG: Cannot save - Seat: 1A, ID: 0  ❌ Validation failed
DEBUG Payment: Booking selectedSeatId: 0  ❌ Will fail payment
```

## Common Issues & Solutions

### Issue 1: "scheduleId = 0"
**Symptom**: Logcat shows `scheduleId = 0` when opening seat selection
**Cause**: Navigated to seat selection without selecting a schedule
**Solution**: Always select a schedule from search results first

### Issue 2: "seat_id = 0" after clicking seat
**Symptom**: Logcat shows `Selecting seat 1A with ID 0`
**Cause**: Fallback ID generator failed (shouldn't happen)
**Solution**: Check if seat number format is correct (e.g., "1A", "2B")

### Issue 3: "Cannot save - ID: 0"
**Symptom**: Save button shows this message in Logcat
**Cause**: selectedSeatId is still 0
**Solution**: Try selecting seat again, check Logcat for errors

### Issue 4: Payment error "Silakan pilih kursi terlebih dahulu"
**Symptom**: Error message shown when clicking payment
**Cause**: booking.selectedSeatId is 0
**Solution**: Go back and select a seat properly

### Issue 5: SQL Error during payment
**Symptom**: Error message about database/SQL
**Cause**: seat_id is 0 or invalid
**Solution**: This should NOT happen anymore with the fix

## How to View Logcat

### Android Studio
1. Open Android Studio
2. Click "Logcat" tab at bottom
3. Select your device/emulator
4. In filter box, type: `DEBUG`
5. Run the app and watch messages

### Command Line
```bash
adb logcat | grep DEBUG
```

## Expected Results After Fix

| Step | Expected Behavior |
|------|-------------------|
| Select seat | seat_id > 0 (e.g., 1010, 3025) |
| Save seat | booking.selectedSeatId > 0 |
| Payment | All passengers use same seat_id |
| Complete | Success, no SQL errors |

## If All Else Fails

1. **Clear app data**
   - Settings → Apps → Whoos → Clear Data
   - Restart app and try again

2. **Check API connection**
   - Verify `BASE_URL` in `Retrofit.kt`
   - Should be `http://10.0.2.2/apiwhoosmobile/` for emulator
   - Or `http://10.10.45.1/apiwhoosmobile/` for physical device

3. **Verify database**
   - Check if `schedules` table has data
   - Check if `stations` table has data
   - Seat tables are optional (fallback works without them)

## Success Criteria
✅ Seat selection shows ID > 0 in Logcat
✅ Payment shows selectedSeatId > 0 in Logcat
✅ Payment completes without errors
✅ Ticket appears in "My Ticket" screen


## di akun atau menu me 
  val gridItems = listOf(
                        GridItemData("Passenger", Icons.Default.Badge), // menambahkan pesengger agar tidak perlu isi data penumpang setiap mau pesan
                        GridItemData("Language", Icons.Default.Translate), // translate ke bahaasa inggris
                        GridItemData("Password", Icons.Default.Lock), // ganti password
                        GridItemData("E-mail", Icons.Default.Email), // ganti email untuk notifikasi si akun membeli ticket,merescheduls,dan merefund
                    )