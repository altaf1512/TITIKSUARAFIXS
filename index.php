<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="login.css">
    <title>Titik Suara</title>
</head>
<body>
    <div class="login-container">
        <div class="login-logo">
            <img src="logo.png" alt="Logo" width="200" height="200">
        </div>
        <form class="login-form" action="login.php" method="post"> <!-- Set action to dashboard.php -->
            <div class="form-group">
                <label for="username" class="custom-label">Username</label>
                <input type="text" class="form-control" id="username" name="username" placeholder="Username" required>
            </div>
            <div class="form-group">
                <label for="password" class="custom-label">Password</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="Password" required>
            </div>
            <br>
            <div class="d-flex justify-content-center"> 
                <button type="submit" class="btn btn-danger btn-block my-custom-button">Login</button>
            </div>
        </form>
    </div>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
</body>
</html>