<?php
require_once 'config.php';

if (isset($_GET['id'])) {
    $id = (int)$_GET['id'];
    
    $stmt = $conn->prepare("SELECT foto_profil FROM pengguna WHERE id_pengguna = ?");
    $stmt->bind_param("i", $id);
    $stmt->execute();
    $stmt->store_result();
    
    if ($stmt->num_rows > 0) {
        $stmt->bind_result($foto_profil);
        $stmt->fetch();
        
        header("Content-Type: image/jpeg");
        echo $foto_profil;
    }
    $stmt->close();
}
?>