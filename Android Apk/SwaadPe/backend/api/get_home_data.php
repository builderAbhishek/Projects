<?php
header('Content-Type: application/json');
require_once('db_config.php');

// --- SECURE API KEY ---
$api_key = "SWAADPE_SECURE_KEY_123";
$headers = apache_request_headers();

// Check if API Key is present in headers
if (!isset($headers['X-API-KEY']) || $headers['X-API-KEY'] !== $api_key) {
    http_response_code(401);
    echo json_encode([
        "status" => "error",
        "message" => "Unauthorized access. API Key missing or invalid."
    ]);
    exit();
}

$response = array();

// 1. Fetch Active Banners
$banner_query = "SELECT id, title, image_url, action_url FROM banners WHERE is_active = 1";
$banner_result = $conn->query($banner_query);
$banners = array();
if ($banner_result) {
    while($row = $banner_result->fetch_assoc()) {
        $banners[] = $row;
    }
}

// 2. Fetch Active Categories (Sorted by sort_order)
$cat_query = "SELECT id, name, icon_url FROM categories WHERE is_active = 1 ORDER BY sort_order ASC";
$cat_result = $conn->query($cat_query);
$categories = array();
if ($cat_result) {
    while($row = $cat_result->fetch_assoc()) {
        $categories[] = $row;
    }
}

// 3. Fetch Active Vendors (Restaurants)
$vendor_query = "SELECT id, name, mobile, address, image, rating, delivery_time, min_order FROM vendors WHERE is_active = 1";
$vendor_result = $conn->query($vendor_query);
$vendors = array();
if ($vendor_result) {
    while($row = $vendor_result->fetch_assoc()) {
        $vendors[] = $row;
    }
}

// Prepare Final Response
$response['status'] = "success";
$response['data'] = [
    "banners" => $banners,
    "categories" => $categories,
    "restaurants" => $vendors // Mapping 'vendors' table to 'restaurants' for app compatibility
];

// Return JSON output
echo json_encode($response);

// Close connection
$conn->close();
?>
