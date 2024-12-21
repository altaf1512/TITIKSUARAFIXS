package com.arin.titik_suara.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
    private ProgressBar progressBar; // Added ProgressBar for loading feedback

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
        String deskripsi = etDeskripsi.getText().toString().trim();
        String kategori = spinnerKategori.getSelectedItem().toString();

        if (TextUtils.isEmpty(deskripsi)) {
            Toast.makeText(getActivity(), "Deskripsi wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(getActivity(), "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

            String url = "http://192.168.1.21/API/post_pengaduan.php";  // Fixed URL

            Map<String, String> params = new HashMap<>();
            params.put("deskripsi", deskripsi);
            params.put("kategori", kategori);
            params.put("image", encodedImage);

            // Show progress bar before sending

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                    response -> {
                        // Hide progress bar after response

                        Toast.makeText(getActivity(), "Pengaduan berhasil dikirim!", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        // Hide progress bar on error

                        Toast.makeText(getActivity(), "Terjadi kesalahan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(getActivity()).add(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
