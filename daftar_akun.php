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
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manajemen Pengguna</title>
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
            <div class="menu-item">
                <a href="dashboard.php">Dashboard</a>
            </div>
            <div class="menu-item">
                <a href="daftar_pengaduan.php">Daftar Pengaduan</a>
            </div>
            <div class="menu-item">
                <a href="daftar_akun.php" style="color:#ff4757">Daftar Akun</a>
            </div>
            <div class="menu-item">
                <a href="laporan_pengaduan.php">Laporan Pengaduan</a>
            </div>
            <div class="menu-item">
                <a href="setting.php">Setting</a>
            </div>

        </div>

        <!-- Main Content -->
        <div class="content mt-4">
            <h1>Manajemen Pengguna</h1>

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

            <!-- Add User Button -->
            <div class="mb-3">
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addUserModal">
                    Tambah Pengguna Baru
                </button>
            </div>

            <!-- User List Table -->
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nama Lengkap</th>
                        <th>Jabatan</th>
                        <th>Alamat</th>
                        <th>No. Telp</th>
                        <th>Peran</th>
                        <th>Foto Profil</th>
                        <th>Dibuat Pada</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <?php while ($row = $result->fetch_assoc()): ?>
                        <tr>
                            <td><?php echo htmlspecialchars($row['id_pengguna']); ?></td>
                            <td><?php echo htmlspecialchars($row['nama_lengkap']); ?></td>
                            <td><?php echo htmlspecialchars($row['jabatan']); ?></td>
                            <td><?php echo htmlspecialchars($row['alamat']); ?></td>
                            <td><?php echo htmlspecialchars($row['no_telp']); ?></td>
                            <td><?php echo htmlspecialchars($row['peran']); ?></td>
                            <td>
                                <?php if ($row['foto_profil']): ?>
                                    <img src="get_image.php?id=<?php echo $row['id_pengguna']; ?>" height="50" alt="Profile Photo">
                                <?php endif; ?>
                            </td>
                            <td><?php echo $row['dibuat_pada']; ?></td>
                            <td>
                                <button class="btn btn-sm btn-info" onclick="editUser(<?php echo $row['id_pengguna']; ?>)">Edit</button>
                                <button class="btn btn-sm btn-danger" onclick="deleteUser(<?php echo $row['id_pengguna']; ?>)">Delete</button>
                            </td>
                        </tr>
                    <?php endwhile; ?>
                </tbody>
            </table>
        </div>

        <!-- Add User Modal -->
        <div class="modal fade" id="addUserModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Tambah Pengguna Baru</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form method="POST" enctype="multipart/form-data">
                            <div class="mb-3">
                                <label class="form-label">Nama Lengkap:</label>
                                <input type="text" name="nama_lengkap" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Kata Sandi:</label>
                                <input type="password" name="kata_sandi" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Jabatan:</label>
                                <input type="text" name="jabatan" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Alamat:</label>
                                <textarea name="alamat" class="form-control" required></textarea>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">No. Telp:</label>
                                <input type="text" name="no_telp" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Peran:</label>
                                <select name="peran" class="form-control" required>
                                    <option value="admin">Admin</option>
                                    <option value="karyawan">Karyawan</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Foto Profil:</label>
                                <input type="file" name="foto_profil" class="form-control" accept="image/*">
                            </div>
                            <button type="submit" name="submit" class="btn btn-primary">Simpan</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JavaScript Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- JavaScript Functions for Edit and Delete Actions -->
    <script>
        function deleteUser(id) {
            if (confirm('Apakah Anda yakin ingin menghapus pengguna ini?')) {
                window.location.href = 'delete_user.php?id=' + id;
            }
        }

        function editUser(id) {
            window.location.href = 'edit_user.php?id=' + id;
        }
    </script>
</body>

</html>