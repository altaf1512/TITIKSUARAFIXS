package com.arin.titik_suara;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Menggunakan Handler untuk delay sebelum melanjutkan ke LoginActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent untuk berpindah ke LoginActivity
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Menutup SplashScreen agar tidak bisa kembali ke SplashScreen
            }
        }, 3000); // 3000 milidetik = 3 detik
    }
}
