package com.arin.titik_suara.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arin.titik_suara.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CobaFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner spinnerKategori;
    private EditText etDeskripsi;
    private ImageView imagePreview;
    private Uri imageUri;
    private Button btnPilihGambar, btnKirim;

    private String[] kategoriArray = {"Fasilitas", "Peralatan", "Kebersihan", "Keamanan", "Kenakalan Siswa", "Lainnya"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coba, container, false);

        spinnerKategori = view.findViewById(R.id.spinnerKategori);
        etDeskripsi = view.findViewById(R.id.etDeskripsi);
        imagePreview = view.findViewById(R.id.imagePreview);
        btnPilihGambar = view.findViewById(R.id.btnPilihGambar);
        btnKirim = view.findViewById(R.id.btnKirim);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, kategoriArray);
        spinnerKategori.setAdapter(adapter);

        btnPilihGambar.setOnClickListener(v -> pilihGambar());
        btnKirim.setOnClickListener(v -> kirimPengaduan());

        return view;
    }

    private void pilihGambar() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
        }
    }

    private void kirimPengaduan() {
        String url = "http://192.168.1.7/API/post_pengaduan.php"; // Pastikan URL valid

        String deskripsi = etDeskripsi.getText().toString();
        String kategori = spinnerKategori.getSelectedItem().toString();

        if (TextUtils.isEmpty(deskripsi) || imageUri == null) {
            Toast.makeText(getContext(), "Deskripsi dan gambar wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageBase64 = encodeImageToBase64(imageUri);
        if (imageBase64 == null) {
            Toast.makeText(getContext(), "Gagal mengonversi gambar!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("deskripsi", deskripsi);
        params.put("kategori", kategori);
        params.put("bukti_pengaduan", imageBase64);  // Mengganti image menjadi bukti_pengaduan

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Kesalahan parsing respons JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Kesalahan koneksi atau respon null", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(getContext()).add(request);
    }



    private String encodeImageToBase64(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
