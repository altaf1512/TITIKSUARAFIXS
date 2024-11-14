<?php
// Database configuration
$host = "localhost";
$username = "root";
$password = "";
$database = "titik_suara";

// Create database connection
try {
    $conn = new mysqli($host, $username, $password, $database);
    
    // Check connection
    if ($conn->connect_error) {
        throw new Exception("Connection failed: " . $conn->connect_error);
    }
} catch (Exception $e) {
    die("Database connection failed: " . $e->getMessage());
}

// Set charset to handle Indonesian characters properly
$conn->set_charset("utf8mb4");

// Error reporting for development
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Functions for database operations
function escape_input($conn, $data) {
    return mysqli_real_escape_string($conn, trim($data));
}

// Session start
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
?>