package com.arin.titik_suara.Util.Interface;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arin.titik_suara.Network.ApiService;
import com.arin.titik_suara.R;
import com.arin.titik_suara.Util.ApiResponse;
import com.arin.titik_suara.Util.RetrofitClient;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CobaActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imagePreview;
    private EditText etDeskripsi;
    private Spinner spinnerKategori;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coba_pengaduan);

        imagePreview = findViewById(R.id.imagePreview);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        Button btnPilihGambar = findViewById(R.id.btnPilihGambar);
        Button btnKirim = findViewById(R.id.btnKirim);

        // Spinner data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.kategori_pengaduan, android.R.layout.simple_spinner_item);
        spinnerKategori.setAdapter(adapter);

        btnPilihGambar.setOnClickListener(v -> openImageChooser());
        btnKirim.setOnClickListener(v -> uploadPengaduan());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
        }
    }

    private void uploadPengaduan() {
        String deskripsi = etDeskripsi.getText().toString().trim();
        String kategori = spinnerKategori.getSelectedItem().toString();

        if (deskripsi.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(FileUtils.getPath(this, imageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("gambar", file.getName(), requestFile);

        RequestBody deskripsiBody = RequestBody.create(MediaType.parse("multipart/form-data"), deskripsi);
        RequestBody kategoriBody = RequestBody.create(MediaType.parse("multipart/form-data"), kategori);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.uploadPengaduan(deskripsiBody, kategoriBody, body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CobaActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CobaActivity.this, "Gagal mengunggah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CobaActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}