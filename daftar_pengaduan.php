<?php
session_start(); // Start session if not already started
require_once 'config.php';
include 'navbar.php'; // Include the navbar at the top

// Proses pengajuan pengaduan
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_pengaduan = strtoupper("PGD" . date("YmdHis")); // ID pengaduan otomatis
    $status_pengaduan = $_POST['status_pengaduan'];
    $kategori = $_POST['kategori']; // Mengambil kategori yang dipilih
    $catatan = $_POST['catatan'];
    $id_pengguna = $_SESSION['id_pengguna']; // Mengambil ID pengguna dari session

    // Query untuk memasukkan pengaduan baru
    $query = "INSERT INTO pengaduan (id_pengaduan, status_pengaduan, kategori, catatan, id_pelapor) 
              VALUES (?, ?, ?, ?, ?)";

    $stmt = $conn->prepare($query);
    $stmt->bind_param("sssss", $id_pengaduan, $status_pengaduan, $kategori, $catatan, $id_pengguna);

    if ($stmt->execute()) {
        $_SESSION['success'] = "Pengaduan berhasil diajukan!";
        header("Location: daftar_pengaduan.php");
        exit();
    } else {
        $_SESSION['error'] = "Terjadi kesalahan. Pengaduan gagal diajukan.";
    }
}
?>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ajukan Pengaduan</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f9f9f9;
            font-family: Arial, sans-serif;
        }

        .container {
            display: flex;
            margin-left: 320px;
        }

        /* Sidebar Styles */
        .sidebar {
            width: 250px;
            background-color: white;
            padding: 20px;
            box-shadow: 2px 0 5px rgba(0,0,0,0.1);
            position: fixed;    
            top: 0;
            left: 0;
            height: 100vh;
            color: white;
            padding-top: 20px;
        }
        .sidebar a {
            text-decoration: none;
        }

        .logo {
            color: white;
            font-size: 24px;
            margin-bottom: 30px;
            display: flex;
            align-items: center;
        }

        .logo img {
            width: 140px;
            height: 140px;
            margin-right: 10px;
        }

        .menu-item {
            padding: 12px 15px;
            margin: 5px 0;
            border-radius: 8px;
            color: #666;
            display: flex;
            align-items: center;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .menu-item:hover {
            background-color: #fff1f1;
            color: #ff4757;
        }

        .menu-item.active {
            background-color: #fff1f1;
            color: #ff4757;
        }

        /* Main Content */
        .content {
            flex: 1;
            padding: 20px;
            background-color: #f9f9f9;
        }

        /* Form Styling */
        .form-container {
            background-color: #ffffff;
            padding: 20px;
            border-radius: 20px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        }

        .form-container h1 {
            margin-bottom: 20px;
            color: #343a40;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            font-weight: bold;
        }

        .btn-submit {
            background-color: #4CAF50;
            color: white;
        }

        .btn-submit:hover {
            background-color: #45a049;
        }

        /* Table Styling */
        .table-container {
            background-color: #ffffff;
            padding: 20px;
            border-radius: 20px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        }

        .table-container h1 {
            margin-bottom: 20px;
            color: #343a40;
        }

        .table th {
            background-color: #ECCBCB;
            color: white;
        }

        .table-hover tbody tr:hover {
            background-color: #f1f1f1;
        }

        .table-responsive {
            overflow-x: auto;
        }

        @media (max-width: 768px) {
            .container {
                flex-direction: column;
                margin-left: 0;
            }

            .sidebar {
                position: relative;
                width: 100%;
                height: auto;
                border-radius: 0;
            }

            .content {
                margin-top: 20px;
            }
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="logo">
            <img src="logo.png" alt="Logo">
        </div>
        <div class="menu-item">
            <a href="dashboard.php">Dashboard</a>
        </div>
        <div class="menu-item">
            <a href="daftar_pengaduan.php">Daftar Pengaduan</a>
        </div>
        <div class="menu-item">
            <a href="daftar_akun.php">Daftar Akun</a>
        </div>
        <div class="menu-item active">
            <a href="riwayat_pengaduan.php">Laporan Pengaduan</a>
        </div>
        <div class="menu-item">
            <a href="setting.php">Setting</a>
        </div>
    </div>

    <!-- Main Content -->
    <div class="container">
        <div class="content">
            <div class="form-container">
                <h1>Ajukan Pengaduan</h1>

                <!-- Success/Error Messages -->
                <?php if (isset($_SESSION['success'])): ?>
                    <div class="alert alert-success">
                        <?php 
                            echo $_SESSION['success']; 
                            unset($_SESSION['success']);
                        ?>
                    </div>
                <?php endif; ?>

                <?php if (isset($_SESSION['error'])): ?>
                    <div class="alert alert-danger">
                        <?php 
                            echo $_SESSION['error']; 
                            unset($_SESSION['error']);
                        ?>
                    </div>
                <?php endif; ?>

                <!-- Form untuk Mendaftar Pengaduan -->
                <form action="" method="POST">
                    <div class="form-group">
                        <label for="kategori">Kategori Pengaduan</label>
                        <select class="form-control" id="kategori" name="kategori" required>
                            <option value="Fasilitas">Fasilitas</option>
                            <option value="Peralatan">Peralatan</option>
                            <option value="Kebersihan">Kebersihan</option>
                            <option value="Keamanan">Keamanan</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="status_pengaduan">Status Pengaduan</label>
                        <select class="form-control" id="status_pengaduan" name="status_pengaduan" required>
                            <?php
                            // Ambil daftar status dari database
                            $statusQuery = "SELECT * FROM status_pengaduan";
                            $statusResult = $conn->query($statusQuery);

                            while ($status = $statusResult->fetch_assoc()) {
                                echo "<option value='{$status['id_status']}'>{$status['nama_status']}</option>";
                            }
                            ?>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="catatan">Catatan</label>
                        <textarea class="form-control" id="catatan" name="catatan" rows="4" required></textarea>
                    </div>

                    <button type="submit" class="btn btn-submit">Ajukan Pengaduan</button>
                </form>
            </div>
        </div>
    </div>

    <!-- Bootstrap JavaScript Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
