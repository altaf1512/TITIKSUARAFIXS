<?php
// tests/LoginTest.php

use PHPUnit\Framework\TestCase;

// Impor file auth.php untuk mengakses fungsi validateUser()
require_once 'auth.php';

class LoginTest extends TestCase
{
    public function testValidCredentials()
    {
        $username = "admin";
        $password = "password123";
        $this->assertTrue(validateUser($username, $password));
    }

    public function testInvalidUsername()
    {
        $username = "wrongUsername";
        $password = "password123";
        $this->assertFalse(validateUser($username, $password));
    }

    public function testInvalidPassword()
    {
        $username = "admin";
        $password = "wrongPassword";
        $this->assertFalse(validateUser($username, $password));
    }

    public function testInvalidCredentials()
    {
        $username = "wrongUsername";
        $password = "wrongPassword";
        $this->assertFalse(validateUser($username, $password));
    }
}