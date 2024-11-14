<?php

function validateUser($username, $password) {
    // Dummy credentials for demonstration
    $valid_username = "admin";
    $valid_password = "password123";
    
    return $username === $valid_username && $password === $valid_password;
}