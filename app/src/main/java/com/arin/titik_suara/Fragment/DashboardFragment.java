package com.arin.titik_suara.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.arin.titik_suara.Adapter.SliderAdapter;
import com.arin.titik_suara.Model.PengaduanModel;
import com.arin.titik_suara.R;
import com.arin.titik_suara.Util.DataApi;
import com.arin.titik_suara.Util.Interface.PengaduanInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    FloatingActionButton fabAdd;
    TextView tvUsername, tvPengaduanDiajukan;
    SharedPreferences sharedPreferences;
    PengaduanInterface pengaduanInterface;
    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;
    private Handler handler;
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize Views
        fabAdd = view.findViewById(R.id.fabAdd);
        tvUsername = view.findViewById(R.id.title1);
        tvPengaduanDiajukan = view.findViewById(R.id.tv_pengaduan_diajukan);
        sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);

        // Set username as greeting
        String username = sharedPreferences.getString("nama_lengkap", "");
        tvUsername.setText("Halo " + username);

        // Initialize the ViewPager2 for the image slider
        viewPager = view.findViewById(R.id.viewPager);
        List<Integer> sliderImages = Arrays.asList(
                R.drawable.image1,  // Replace with your drawable resources
                R.drawable.image2,
                R.drawable.image3
        );
        sliderAdapter = new SliderAdapter(sliderImages);
        viewPager.setAdapter(sliderAdapter);

        // Set up the handler and runnable to change the page every 3 seconds
        handler = new Handler();
        runnable = new Runnable() {
            int currentItem = 0;

            @Override
            public void run() {
                if (currentItem < sliderImages.size()) {
                    viewPager.setCurrentItem(currentItem, true);
                    currentItem++;
                } else {
                    currentItem = 0;
                }
                handler.postDelayed(this, 3000); // Repeat every 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000); // Initial delay of 3 seconds

        // Retrieve total counts for each status
        getPengaduanCount("diajukan", tvPengaduanDiajukan);

        // Set up Floating Action Button to navigate to create pengaduan fragment
        fabAdd.setOnClickListener(v -> getFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new CobaFragment()) // Replace with your desired fragment
                .addToBackStack(null)
                .commit());

        return view;
    }

    private void getPengaduanCount(String status, TextView tvTotal) {
        pengaduanInterface = DataApi.getClient().create(PengaduanInterface.class);
        String userId = sharedPreferences.getString("user_id", null);
        pengaduanInterface.getPengaduanByStatus(userId, status).enqueue(new Callback<List<PengaduanModel>>() {
            @Override
            public void onResponse(Call<List<PengaduanModel>> call, Response<List<PengaduanModel>> response) {
                if (response.body() != null) {
                    List<PengaduanModel> pengaduanList = response.body();
                    tvTotal.setText(String.valueOf(pengaduanList.size()));
                } else {
                    tvTotal.setText("0");
                }
            }

            @Override
            public void onFailure(Call<List<PengaduanModel>> call, Throwable t) {
                tvTotal.setText("Error");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable); // Remove handler callbacks to avoid memory leaks
    }
}
