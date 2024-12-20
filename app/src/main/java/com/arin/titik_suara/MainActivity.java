package com.arin.titik_suara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.arin.titik_suara.Fragment.DashboardFragment;
import com.arin.titik_suara.Fragment.NotifFragment;
import com.arin.titik_suara.Fragment.allPengaduanFragment;
import com.arin.titik_suara.Fragment.ProfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initial fragment when app starts
        replace(new DashboardFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    replace(new DashboardFragment());
                    return true;
                } else if (item.getItemId() == R.id.pengaduan) {
                    replace(new allPengaduanFragment());
                    return true;
                } else if (item.getItemId() == R.id.notif) {
                    replace(new NotifFragment());
                    return true;
                } else if (item.getItemId() == R.id.profile) {
                    replace(new ProfilFragment());
                    return true;
                }
                return false;
            }
        });
    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if(fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
