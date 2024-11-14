<?php
require_once 'config.php';

if (isset($_GET['id'])) {
    $id = (int)$_GET['id'];
    
    $stmt = $conn->prepare("DELETE FROM pengguna WHERE id_pengguna = ?");
    $stmt->bind_param("i", $id);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Pengguna berhasil dihapus!";
    } else {
        $_SESSION['error'] = "Error menghapus pengguna: " . $stmt->error;
    }
    
    $stmt->close();
    header("Location: daftar_akun.php");
    exit();
}
?>