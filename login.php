<?php
session_start();

require_once 'auth.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $username = $_POST['username'];
    $password = $_POST['password'];

    // Validasi kredensial
    if (validateUser($username, $password)) {
        // Redirect ke dashboard jika login berhasil
        header("Location: dashboard.php"); // Ubah ke halaman dashboard yang sesuai
        exit();
    } else {
        echo "Invalid username or password.";
    }
}