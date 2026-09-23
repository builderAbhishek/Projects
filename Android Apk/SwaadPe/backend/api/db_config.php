<?php
// Database configuration
$servername = "localhost";
$username = "your_db_user"; // Change this to your database username
$password = "your_db_password"; // Change this to your database password
$dbname = "swaadpe_db"; // Change this to your database name

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed"]));
}

// Set charset to utf8mb4 for emoji support and correct encoding
$conn->set_charset("utf8mb4");
?>
