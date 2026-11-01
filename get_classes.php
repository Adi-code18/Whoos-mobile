<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');

// Database connection
require_once 'db_connect.php';

try {
    // Query untuk ambil semua kelas dengan multiplier harga
    $query = "SELECT 
                id,
                class_name,
                price_multiplier,
                description
              FROM wagon_classes 
              ORDER BY price_multiplier ASC";
    
    $stmt = $conn->prepare($query);
    $stmt->execute();
    
    $classes = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $classes[] = [
            'id' => (int)$row['id'],
            'class_name' => $row['class_name'],
            'price_multiplier' => (float)$row['price_multiplier'],
            'description' => $row['description']
        ];
    }
    
    // Return data
    echo json_encode($classes);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Database error',
        'message' => $e->getMessage()
    ]);
}
?>
