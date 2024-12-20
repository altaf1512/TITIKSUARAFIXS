package com.arin.titik_suara.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.arin.titik_suara.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPengaduanFragment extends Fragment {
    private static final String TAG = "MyPengaduanFragment";
    private static final String BASE_URL = "http://192.168.1.21//API/get_pengaduan.php";

    private RecyclerView rvPengaduan;
    private TextView tvEmpty;
    private ProgressDialog progressDialog;
    private List<PengaduanItem> pengaduanList;
    private PengaduanListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_pengaduan, container, false);

        // Inisialisasi views
        initViews(view);

        // Load data
        fetchData();

        return view;
    }

    private void initViews(View view) {
        rvPengaduan = view.findViewById(R.id.rv_pengaduan);
        tvEmpty = view.findViewById(R.id.tv_empty);

        // Setup RecyclerView
        pengaduanList = new ArrayList<>();
        adapter = new PengaduanListAdapter(requireContext(), pengaduanList);
        rvPengaduan.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPengaduan.setAdapter(adapter);

        // Setup Progress Dialog
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Memuat data...");
        progressDialog.setCancelable(false);
    }

    private void fetchData() {
        progressDialog.show();

        String url = "http://192.168.1.21/API/get_pengaduan.php";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "Response received: " + response.toString());

                    try {
                        pengaduanList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            PengaduanItem item = new PengaduanItem(
                                    obj.getString("id_pengaduan"),
                                    obj.getString("deskripsi"),
                                    obj.getString("kategori"),
                                    obj.getString("dibuat_pada"),
                                    obj.getInt("id_status")
                            );
                            pengaduanList.add(item);
                        }

                        adapter.notifyDataSetChanged();
                        updateUI();


                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error", e);
                        showError("Error parsing data: " + e.getMessage());
                    }

                },
                this::handleError
        ) {
            // Optional: Tambahkan header atau parameter tambahan jika diperlukan
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                // Tambahkan header authorization jika diperlukan
                // headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // Tambahkan tag untuk memudahkan pembatalan request jika diperlukan
        request.setTag(TAG);

        // Gunakan VolleyConnection untuk menambahkan request
        VolleyConnection.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void handleError(VolleyError error) {
        Log.e(TAG, "Volley Error Details:", error);

        if (error.networkResponse != null) {
            Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
            Log.e(TAG, "Error Message: " + new String(error.networkResponse.data));
        }

        String errorMessage = error.getMessage() != null
                ? error.getMessage()
                : "Tidak dapat mengambil data";

        showError(errorMessage);
    }
    private void showError(String errorMessage) {
        progressDialog.dismiss();
        tvEmpty.setText(errorMessage);
        tvEmpty.setVisibility(View.VISIBLE);
        rvPengaduan.setVisibility(View.GONE);
    }

    private void updateUI() {
        if (pengaduanList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvPengaduan.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvPengaduan.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Batalkan semua request dengan tag ini jika fragment dihancurkan
        if (getContext() != null) {
            VolleyConnection.getInstance(getContext()).cancelAllRequests(TAG);
        }
    }
}