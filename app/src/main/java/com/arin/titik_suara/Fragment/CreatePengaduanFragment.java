package com.arin.titik_suara.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.arin.titik_suara.Model.PengaduanModel;
import com.arin.titik_suara.R;
import com.arin.titik_suara.Util.DataApi;
import com.arin.titik_suara.Util.Interface.PengaduanInterface;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePengaduanFragment extends Fragment {

    Spinner spKategoriPengaduan;
    String[] kategoriPengaduan = {"Fasilitas", "Peralatan", "Kebersihan", "Keamanan", "Kenakalan Siswa", "Lainnya"};

    Button btnImagePicker1, btnSubmit;
    EditText etDetailImage1, etDeskripsi;
    SharedPreferences sharedPreferences;

    private File file1;
    String selectedKategoriPengaduan, id_pengguna;
    ImageView ivPrev1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_pengaduan, container, false);

        sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        id_pengguna = sharedPreferences.getString("user_id", null);

        spKategoriPengaduan = view.findViewById(R.id.spinnerKategoriPengaduan);
        btnImagePicker1 = view.findViewById(R.id.btnImgPicker1);
        etDetailImage1 = view.findViewById(R.id.edtImgFile1);
        etDeskripsi = view.findViewById(R.id.etDeskripsiPengaduan);
        ivPrev1 = view.findViewById(R.id.imgPrev1);
        btnSubmit = view.findViewById(R.id.btnSubmitPengaduan);

        // Adapter for spinner
        ArrayAdapter<String> kategoriPengaduanAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, kategoriPengaduan);
        kategoriPengaduanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategoriPengaduan.setAdapter(kategoriPengaduanAdapter);

        spKategoriPengaduan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedKategoriPengaduan = kategoriPengaduan[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Request permission for storage access
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
        }

        // Image picker click listeners
        btnImagePicker1.setOnClickListener(v -> pickImage(1));

        // Submit button click listener
        btnSubmit.setOnClickListener(v -> submitPengaduan());

        return view;
    }

    // Method to pick image
    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    // Submit complaint
    private void submitPengaduan() {
        if (file1 == null ||
                etDeskripsi.getText().toString().isEmpty()) {
            Toasty.error(getContext(), "Harap melengkapi semua data!", Toasty.LENGTH_SHORT).show();
            return;
        }

        Map<String, RequestBody> map = new HashMap<>();
        map.put("id_pengguna", createPartFromString(id_pengguna));
        map.put("kategori", createPartFromString(selectedKategoriPengaduan));
        map.put("deskripsi", createPartFromString(etDeskripsi.getText().toString()));

        MultipartBody.Part fileToUpload1 = prepareFilePart("foto", file1);

        PengaduanInterface pengaduanInterface = DataApi.getClient().create(PengaduanInterface.class);
        pengaduanInterface.createPengaduan(map, fileToUpload1)
                .enqueue(new Callback<PengaduanModel>() {
                    @Override
                    public void onResponse(Call<PengaduanModel> call, Response<PengaduanModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toasty.success(getContext(), "Pengaduan berhasil dibuat!", Toasty.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, new DashboardFragment())
                                    .commit();
                        } else {
                            Toasty.error(getContext(), "Gagal membuat pengaduan!", Toasty.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PengaduanModel> call, Throwable t) {
                        Toasty.error(getContext(), "Gagal menghubungi server!", Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    // Helper to create RequestBody from string
    private RequestBody createPartFromString(String data) {
        return RequestBody.create(MediaType.parse("text/plain"), data);
    }

    // Helper to prepare MultipartBody.Part for file upload
    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toasty.success(getContext(), "Izin akses diberikan", Toasty.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            File file = new File(getRealPathFromUri(uri));
            switch (requestCode) {
                case 1:
                    ivPrev1.setVisibility(View.VISIBLE);
                    ivPrev1.setImageURI(uri);
                    file1 = file;
                    etDetailImage1.setText(file1.getName());
                    break;
            }
        }
    }

    // Get real path from Uri
    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(proj[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return null;
        }
    }
}