<?php
session_start(); // Start session if not already started
require_once 'config.php';
include 'navbar.php'; // Include the navbar at the top

// Your existing CRUD code for inserting and displaying users
// ...


// Check if form is submitted
if (isset($_POST['submit'])) {
    $nama_lengkap = escape_input($conn, $_POST['nama_lengkap']);
    $kata_sandi = password_hash($_POST['kata_sandi'], PASSWORD_DEFAULT);
    $jabatan = escape_input($conn, $_POST['jabatan']);
    $alamat = escape_input($conn, $_POST['alamat']);
    $no_telp = escape_input($conn, $_POST['no_telp']);
    $peran = escape_input($conn, $_POST['peran']);
    
    // Handle file upload
    $foto_profil = null;
    if (isset($_FILES['foto_profil']) && $_FILES['foto_profil']['size'] > 0) {
        $foto_profil = addslashes(file_get_contents($_FILES['foto_profil']['tmp_name']));
    }
    
    $sql = "INSERT INTO pengguna (nama_lengkap, kata_sandi, jabatan, alamat, no_telp, peran, foto_profil, dibuat_pada) 
            VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
            
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssssss", $nama_lengkap, $kata_sandi, $jabatan, $alamat, $no_telp, $peran, $foto_profil);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "Pengguna berhasil ditambahkan!";
        header("Location: daftar_akun.php");
        exit();
    } else {
        $_SESSION['error'] = "Error: " . $stmt->error;
    }
    $stmt->close();
}

// Get all users
$result = $conn->query("SELECT * FROM pengguna ORDER BY dibuat_pada DESC");
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        body {
            background-color: #f5f5f5;
        }

        .container {
            display: flex;
            min-height: 100vh;
            margin-left: 220px;

        }

        /* Sidebar Styles */
        .sidebar {
            width: 250px;
            background-color: white;
            padding: 20px;
            box-shadow: 2px 0 5px rgba(0,0,0,0.1);
            min-height: 100vh;
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
            color: #ff4757;
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
            transition: background-color 0.3s
            
        }

            .menu-item:hover {
            background-color: #fff1f1;
            color: #ff4757;
        }

            .menu-item.active {
                background-color: #fff1f1;
                color: #ff4757;
        }

        

        /* Main Content Styles */
        .main-content {
            flex: 1;
            padding: 20px;
            background-color: #ffe4e4;
        }

        .stats-container {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background-color: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
        }

        .icon-pengaduan {
            background-color: #fff1f1;
            color: #ff4757;
        }

        .icon-unread {
            background-color: #fff3cd;
            color: #ffa502;
        }

        .icon-new {
            background-color: #d4edda;
            color: #2ed573;
        }

        .icon-total {
            background-color: #cce5ff;
            color: #1e90ff;
        }

        .stat-info h3 {
            font-size: 24px;
            margin-bottom: 5px;
        }

        .stat-info p {
            color: #666;
            margin: 0;
        }

        /* Recent Reports Section - Updated for horizontal layout */
        .recent-reports {
            background-color: white;
            padding: 20px;
            border-radius: 12px;
            margin-bottom: 20px;
        }

        .recent-reports h2 {
            margin-bottom: 15px;
            color: #333;
        }

        .reports-container {
            display: flex;
            gap: 20px;
            overflow-x: auto;
            padding-bottom: 10px;
        }

        .report-item {
            background-color: white;
            border-radius: 12px;
            padding: 15px;
            min-width: 250px;
            border: 1px solid #eee;
        }

        .user-info {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }

        .user-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 15px;
            background-color: #f0f0f0;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #666;
        }

        .user-details h4 {
            color: #333;
            margin-bottom: 4px;
        }

        .user-details p {
            color: #666;
            font-size: 14px;
        }

        .report-status {
            display: block;
            padding: 8px 12px;
            border-radius: 8px;
            font-size: 14px;
            background-color: #f8f9fa;
            color: #666;
            text-align: center;
        }

        .status-pending {
            background-color: #fff3cd;
            color: #856404;
        }

        .status-completed {
            background-color: #d4edda;
            color: #155724;
        }

        .status-processing {
            background-color: #cce5ff;
            color: #004085;
        }

        /* New Accounts Section */
        .new-accounts {
            background-color: white;
            padding: 20px;
            border-radius: 12px;
        }

        .accounts-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0 10px;
            margin-top: -10px;
        }

        .accounts-table tr {
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }

        .accounts-table td {
            padding: 15px;
            border: none;
            background-color: white;
        }

        .accounts-table tr td:first-child {
            border-radius: 12px 0 0 12px;
        }

        .accounts-table tr td:last-child {
            border-radius: 0 12px 12px 0;
        }

        .user-cell {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .table-user-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background-color: #f0f0f0;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #666;
        }

        .table-user-info h4 {
            margin: 0;
            color: #333;
            font-size: 14px;
        }

        .table-user-info p {
            margin: 4px 0 0 0;
            color: #666;
            font-size: 12px;
        }

        .status-badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
            text-align: center;
            display: inline-block;
        }

        .status-active {
            background-color: #d4edda;
            color: #155724;
        }

        .status-pending {
            background-color: #fff3cd;
            color: #856404;
        }

    </style>
</head>
<body>
    <div class="container">
        <!-- Sidebar -->
        <div class="sidebar">
    <div class="logo">
        <img src="logo.png" alt="Logo">
    </div>
    <div class="menu-item ">
        <a href="dashboard.php"style="color:#ff4757"> Dashboard </a>
    </div>
    <div class="menu-item">
        <a href="daftar_pengaduan.php"> Daftar Pengaduan </a> 
    </div>
    <div class="menu-item">
        <a href="daftar_akun.php"> Daftar Akun</a>
    </div>
    <div class="menu-item">
        <a href="laporan_pengaduan.php"> Laporan Pengaduan </a>
    </div>
    <div class="menu-item">
        <a href="setting.php"> Setting </a>
    </div>

</div>
        <!-- Main Content -->
        <div class="main-content">
    <!-- Stats Cards -->
    <div class="stats-container">
        <div class="stat-card">
            <div class="stat-icon icon-pengaduan">
                <i class="fas fa-file-alt"></i>
            </div>
            <div class="stat-info">
                <h3>12</h3>
                <p>Pengaduan</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon icon-unread">
                <i class="fas fa-envelope"></i>
            </div>
            <div class="stat-info">
                <h3>6</h3>
                <p>Belum Dilihat</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon icon-new">
                <i class="fas fa-plus-circle"></i>
            </div>
            <div class="stat-info">
                <h3>3</h3>
                <p>Pengaduan Baru</p>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon icon-total">
                <i class="fas fa-clipboard-list"></i>
            </div>
            <div class="stat-info">
                <h3>15</h3>
                <p>Pengaduan</p>
            </div>
        </div>
    </div>

    <!-- Recent Reports -->
    <div class="recent-reports">
        <h2>Pengaduan Terbaru</h2>
        <div class="reports-container">
            <div class="report-item">
                <div class="user-info">
                    <div class="user-icon">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="user-details">
                        <h4>Ryujin</h4>
                        <p>22 Jan 2024</p>
                    </div>
                </div>
                <div class="report-status status-pending">Belum Dibaca</div>
            </div>
            <div class="report-item">
                <div class="user-info">
                    <div class="user-icon">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="user-details">
                        <h4>Maulana</h4>
                        <p>22 Jan 2024</p>
                    </div>
                </div>
                <div class="report-status status-completed">Selesai</div>
            </div>
            <div class="report-item">
                <div class="user-info">
                    <div class="user-icon">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="user-details">
                        <h4>Lann</h4>
                        <p>22 Jan 2024</p>
                    </div>
                </div>
                <div class="report-status status-processing">Diproses</div>
            </div>
        </div>
    </div>
    

    <!-- New Accounts Section -->
    <div class="new-accounts">
        <h2>Akun Terbaru</h2>
        <table class="accounts-table">
            <tbody>
                <?php while ($row = $result->fetch_assoc()): ?>
                    <tr>
                        <td style="width: 50%;">
                            <div class="user-cell">
                                <div class="table-user-icon">
                                    <i class="fas fa-user"></i>
                                </div>
                                <div class="table-user-info">
                                    <h4><?php echo htmlspecialchars($row['nama_lengkap']); ?></h4>
                                    <p><?php echo htmlspecialchars($row['jabatan']); ?></p>
                                </div>
                            </div>
                        </td>
                        <td><?php echo htmlspecialchars($row['peran']); ?></td>
                        <td><?php echo htmlspecialchars($row['dibuat_pada']); ?></td>
                    </tr>
                <?php endwhile; ?>
            </tbody>
        </table>

        
    </div>
    
    
</div>

    </div>
</body>
</html>