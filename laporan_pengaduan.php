<?php
session_start(); // Start session if not already started
require_once 'config.php';
include 'navbar.php'; // Include the navbar at the top

// Perbarui query untuk mendapatkan data dengan nama status dan nama pengguna
$query = "
    SELECT 
        rp.id_riwayat,
        rp.id_pengaduan,
        sl.nama_status AS status_lama,
        sb.nama_status AS status_baru,
        p.nama_lengkap AS diubah_oleh,
        rp.catatan,
        rp.dibuat_pada
    FROM 
        riwayat_pengaduan rp
    LEFT JOIN 
        status_pengaduan sl ON rp.status_lama = sl.id_status
    LEFT JOIN 
        status_pengaduan sb ON rp.status_baru = sb.id_status
    LEFT JOIN 
        pengguna p ON rp.diubah_oleh = p.id_pengguna
    ORDER BY 
        rp.dibuat_pada DESC;
";

// Eksekusi query
$result = $conn->query($query);
?>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Riwayat Pengaduan</title>
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
        .sidebar a{
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
            <div class="table-container">
                <h1>Riwayat Pengaduan</h1>

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

                <!-- Riwayat Pengaduan Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-striped table-hover align-middle">
                        <thead>
                            <tr>
                                <th>ID Riwayat</th>
                                <th>ID Pengaduan</th>
                                <th>Status Lama</th>
                                <th>Status Baru</th>
                                <th>Diubah Oleh</th>
                                <th>Catatan</th>
                                <th>Dibuat Pada</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php while ($row = $result->fetch_assoc()): ?>
                                <tr>
                                    <td><?php echo htmlspecialchars($row['id_riwayat']); ?></td>
                                    <td><?php echo htmlspecialchars($row['id_pengaduan']); ?></td>
                                    <td><?php echo htmlspecialchars($row['status_lama']); ?></td>
                                    <td><?php echo htmlspecialchars($row['status_baru']); ?></td>
                                    <td><?php echo htmlspecialchars($row['diubah_oleh']); ?></td>
                                    <td><?php echo htmlspecialchars($row['catatan']); ?></td>
                                    <td><?php echo htmlspecialchars($row['dibuat_pada']); ?></td>
                                </tr>
                            <?php endwhile; ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JavaScript Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
