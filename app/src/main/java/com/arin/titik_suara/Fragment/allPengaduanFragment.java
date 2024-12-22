package com.arin.titik_suara.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arin.titik_suara.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class allPengaduanFragment extends Fragment {
    private static final String TAG = "MyPengaduanFragment";

    private RecyclerView rvPengaduan;
    private TextView tvEmpty;
    private ProgressDialog progressDialog;
    private List<PengaduanItem> pengaduanList;
    private PengaduanListAdapter adapter;
    private Button btnedit;

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

        String url = "http://192.168.1.7/API/get_pengaduan.php";

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
        );

        request.setTag(TAG);

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
        if (getContext() != null) {
            VolleyConnection.getInstance(getContext()).cancelAllRequests(TAG);
        }
    }

    private void setupEditButton(PengaduanItem item) {
        btnedit.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_edit_pengaduan, null);
            dialogBuilder.setView(dialogView);

            EditText etDeskripsi = dialogView.findViewById(R.id.et_deskripsi);
            Spinner spinnerKategori = dialogView.findViewById(R.id.spinner_kategori);
            Button btnSimpan = dialogView.findViewById(R.id.btn_simpan);
            Button btnBatal = dialogView.findViewById(R.id.btn_batal);

            etDeskripsi.setText(item.getDeskripsi());

            ArrayAdapter<CharSequence> kategorAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.kategori_array,
                    android.R.layout.simple_spinner_item
            );
            kategorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerKategori.setAdapter(kategorAdapter);

            int currentKategoriIndex = kategorAdapter.getPosition(item.getKategori());
            spinnerKategori.setSelection(currentKategoriIndex);

            AlertDialog dialog = dialogBuilder.create();

            btnSimpan.setOnClickListener(v1 -> {
                String newDeskripsi = etDeskripsi.getText().toString().trim();
                String newKategori = spinnerKategori.getSelectedItem().toString();

                if (newDeskripsi.isEmpty()) {
                    etDeskripsi.setError("Deskripsi tidak boleh kosong");
                    return;
                }

                updatePengaduan(item.getId(), newDeskripsi, newKategori, dialog);
            });

            btnBatal.setOnClickListener(v12 -> dialog.dismiss());

            dialog.show();
        });
    }

    private void updatePengaduan(String id, String deskripsi, String kategori, AlertDialog dialog) {
        progressDialog.setMessage("Mengupdate pengaduan...");
        progressDialog.show();

        String url = "http://192.168.1.21/API/update_pengaduan.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            fetchData();
                            dialog.dismiss();
                            Toast.makeText(requireContext(), "Pengaduan berhasil diupdate", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = jsonResponse.getString("message");
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error", e);
                        Toast.makeText(requireContext(), "Gagal memproses respons", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Update error", error);
                    Toast.makeText(requireContext(), "Gagal mengupdate pengaduan", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pengaduan", id);
                params.put("deskripsi", deskripsi);
                params.put("kategori", kategori);
                return params;
            }
        };

        VolleyConnection.getInstance(requireContext()).addToRequestQueue(stringRequest);
    }
}
