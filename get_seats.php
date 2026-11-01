<?php
/**
 * GET SEATS API
 * Endpoint untuk mendapatkan daftar kursi berdasarkan schedule_id dan wagon_number
 * 
 * Method: POST
 * Parameters:
 *   - schedule_id (required): ID jadwal kereta
 *   - wagon_number (required): Nomor gerbong (01, 02, 03, dll)
 * 
 * Response:
 * [
 *   {
 *     "id": 1,
 *     "seat_number": "1A",
 *     "wagon_number": "01",
 *     "is_available": true
 *   },
 *   ...
 * ]
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Database configuration
$host = 'localhost';
$dbname = 'whoosh_db';  // Ganti dengan nama database Anda
$username = 'root';      // Ganti dengan username database Anda
$password = '';          // Ganti dengan password database Anda

try {
    // Create PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Get POST parameters
    $schedule_id = isset($_POST['schedule_id']) ? intval($_POST['schedule_id']) : 0;
    $wagon_number = isset($_POST['wagon_number']) ? trim($_POST['wagon_number']) : '';
    
    // Validate parameters
    if ($schedule_id <= 0) {
        echo json_encode([
            'status' => 'error',
            'message' => 'schedule_id is required'
        ]);
        exit;
    }
    
    if (empty($wagon_number)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'wagon_number is required'
        ]);
        exit;
    }
    
    // Query to get seats with availability
    $sql = "SELECT 
                s.id,
                s.seat_number,
                s.wagon_number,
                COALESCE(sb.is_available, 1) as is_available
            FROM seats s
            LEFT JOIN seat_bookings sb ON s.id = sb.seat_id AND sb.schedule_id = :schedule_id
            WHERE s.wagon_number = :wagon_number
            ORDER BY s.seat_number";
    
    $stmt = $pdo->prepare($sql);
    $stmt->bindParam(':schedule_id', $schedule_id, PDO::PARAM_INT);
    $stmt->bindParam(':wagon_number', $wagon_number, PDO::PARAM_STR);
    $stmt->execute();
    
    $seats = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $seats[] = [
            'id' => (int)$row['id'],
            'seat_number' => $row['seat_number'],
            'wagon_number' => $row['wagon_number'],
            'is_available' => (bool)$row['is_available']
        ];
    }
    
    // Return seats data
    echo json_encode($seats);
    
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>
