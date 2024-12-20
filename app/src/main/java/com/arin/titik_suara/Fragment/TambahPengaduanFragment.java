package com.arin.titik_suara.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.arin.titik_suara.Network.ApiClient;
import com.arin.titik_suara.Network.ApiService;
import com.arin.titik_suara.R;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahPengaduanFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etIsiLaporan;
    private Spinner spinnerJenisKerusakan;
    private ImageView ivPreviewImg;
    private Button btnPilihGambar, btnKirim;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tambah_pengaduan_baru, container, false);

        // Inisialisasi komponen
        etIsiLaporan = view.findViewById(R.id.etIsiLaporan);
        spinnerJenisKerusakan = view.findViewById(R.id.spinnerJenisKerusakan);
        ivPreviewImg = view.findViewById(R.id.ivPreviewImg);
        btnPilihGambar = view.findViewById(R.id.btnPilihGambar);
        btnKirim = view.findViewById(R.id.btnKirim);

        setupSpinner();

        // Event listener untuk tombol pilih gambar
        btnPilihGambar.setOnClickListener(v -> openImagePicker());

        // Event listener untuk tombol kirim
        btnKirim.setOnClickListener(v -> submitPengaduan());

        return view;
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.jenis_kerusakan_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJenisKerusakan.setAdapter(adapter);
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                ivPreviewImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitPengaduan() {
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat RequestBody untuk deskripsi, kategori, dan gambar
        RequestBody deskripsi = RequestBody.create(
                MultipartBody.FORM, etIsiLaporan.getText().toString());
        RequestBody kategori = RequestBody.create(
                MultipartBody.FORM, spinnerJenisKerusakan.getSelectedItem().toString());

        // Mengambil file dari Uri
        File file = new File(getPathFromUri(selectedImageUri));
        if (!file.exists()) {
            Toast.makeText(requireContext(), "Gagal membaca file gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part gambar = MultipartBody.Part.createFormData("gambar", file.getName(), fileBody);

        // Mengirim data ke server melalui Retrofit
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.submitPengaduan(deskripsi, kategori, gambar).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Pengaduan berhasil dikirim", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Gagal mengirim pengaduan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}
