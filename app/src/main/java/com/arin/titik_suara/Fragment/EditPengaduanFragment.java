package com.arin.titik_suara.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPengaduanFragment extends Fragment {

    Spinner spKategoriPengaduan;
    private final String[] kategoriPengaduanList = {
            "Fasilitas",
            "Peralatan",
            "Kebersihan",
            "Keamanan",
            "Kenakalan Siswa",
            "Lainnya"
    };

    Button btnImgPicker1, btnSubmit, btnEditImagePicker, btnEditSimpanPengaduan, btnCancel;
    EditText etDetailImage1, etPathImage, et_deskripsi;
    SharedPreferences sharedPreferences;
    List<PengaduanModel> pengaduanModelList;

    private File file1;
    String id_pengguna, selectedKategori, id_pengaduan, foto1;
    ImageView ivImg1, ivPreviewImg;
    Dialog dialog;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coba_pengaduan, container, false);

        initializeViews(view);
        setupInitialData();
        setupSpinner();
        setupImagePickers();
        setupSubmitButton();

        return view;
    }

    private void initializeViews(View view) {
        spKategoriPengaduan = view.findViewById(R.id.spinnerKategoriPengaduan);
        btnImgPicker1 = view.findViewById(R.id.btnImgPicker1);
        btnSubmit = view.findViewById(R.id.btnSubmitPengaduan);
        et_deskripsi = view.findViewById(R.id.etDeskripsiPengaduan);
        ivImg1 = view.findViewById(R.id.img1);

        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.layout_edit_image);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);

        btnEditImagePicker = dialog.findViewById(R.id.btnEditImagePicker);
        etPathImage = dialog.findViewById(R.id.etPathImage);
        ivPreviewImg = dialog.findViewById(R.id.ivPreviewImg);
        btnEditSimpanPengaduan = dialog.findViewById(R.id.btnEditSimpanPengaduan);
        btnCancel = dialog.findViewById(R.id.btnEditCancel);
    }

    private void setupInitialData() {
        sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        id_pengguna = sharedPreferences.getString("user_id", null);
        id_pengaduan = getArguments().getString("id_pengguna");

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Mengubah data");
        progressDialog.setCancelable(false);

        et_deskripsi.setText(getArguments().getString("deskripsi_pengaduan"));

        fullScreenImage(id_pengaduan, ivImg1, "foto");

        displayImage();
        checkPermissions();
    }

    private void displayImage() {
    }

    private void setupSpinner() {
        ArrayAdapter<String> kategoriPengaduanAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                kategoriPengaduanList
        );
        kategoriPengaduanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategoriPengaduan.setAdapter(kategoriPengaduanAdapter);

        spKategoriPengaduan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedKategori = kategoriPengaduanList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupImagePickers() {
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            etPathImage.setText("");
            Glide.with(requireContext())
                    .load(R.drawable.baseline_calendar_month_24)
                    .into(ivPreviewImg);
        });

        setupImagePicker(btnImgPicker1, ivImg1);
    }

    private void setupImagePicker(Button picker, ImageView targetView) {
        picker.setOnClickListener(v -> {
            dialog.show();

            btnEditImagePicker.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            });

            btnEditSimpanPengaduan.setOnClickListener(view -> {
                if (etPathImage.getText().toString().isEmpty()) {
                    Toasty.error(requireContext(), "Anda belum memiliki gambar", Toasty.LENGTH_SHORT).show();
                    return;
                }
                updateImage(targetView);
            });
        });
    }

    private void updateImage(ImageView targetView) {
        progressDialog.show();

        RequestBody idPengaduanBody = RequestBody.create(MediaType.parse("text/plain"), id_pengaduan);
        RequestBody fieldBody = RequestBody.create(MediaType.parse("text/plain"), "foto");

        Map<String, RequestBody> map = new HashMap<>();
        map.put("id_pengaduan", idPengaduanBody);
        map.put("field", fieldBody);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file1);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("foto", file1.getName(), requestFile);

        PengaduanInterface pengaduanInterface = DataApi.getClient().create(PengaduanInterface.class);
        pengaduanInterface.updateImage(map, fileToUpload)
                .enqueue(new Callback<PengaduanModel>() {
                    @Override
                    public void onResponse(Call<PengaduanModel> call, Response<PengaduanModel> response) {
                        if (response.isSuccessful()) {
                            updateImageSuccess(targetView);
                        } else {
                            updateImageError();
                        }
                    }

                    @Override
                    public void onFailure(Call<PengaduanModel> call, Throwable t) {
                        Toasty.error(requireContext(), "Periksa koneksi anda", Toasty.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateImageSuccess(ImageView targetView) {
        progressDialog.dismiss();
        Toasty.success(requireContext(), "Gambar berhasil diupdate", Toasty.LENGTH_SHORT).show();
        loadImage(targetView, file1.getAbsolutePath());
    }

    private void updateImageError() {
        progressDialog.dismiss();
        Toasty.error(requireContext(), "Gagal mengupdate gambar", Toasty.LENGTH_SHORT).show();
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            progressDialog.show();
            submitUpdatePengaduan();
        });
    }

    private void submitUpdatePengaduan() {
        PengaduanInterface pengaduanInterface = DataApi.getClient().create(PengaduanInterface.class);
        pengaduanInterface.updatePengaduan(
                id_pengaduan,
                selectedKategori,
                et_deskripsi.getText().toString(),
                foto1
        ).enqueue(new Callback<PengaduanModel>() {
            @Override
            public void onResponse(Call<PengaduanModel> call, Response<PengaduanModel> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toasty.success(requireContext(), "Berhasil mengubah data", Toasty.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toasty.error(requireContext(), "Gagal mengubah data", Toasty.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PengaduanModel> call, Throwable t) {
                progressDialog.dismiss();
                Toasty.error(requireContext(), "Periksa koneksi anda", Toasty.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1 && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = requireContext().getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                file1 = new File(filePath);
                etPathImage.setText(filePath);
                Glide.with(requireContext())
                        .load(file1)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(ivPreviewImg);
                cursor.close();
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void fullScreenImage(String id, ImageView view, String field) {
        PengaduanInterface pengaduanInterface = DataApi.getClient().create(PengaduanInterface.class);
        pengaduanInterface.getImage(id, field).enqueue(new Callback<PengaduanModel>() {
            @Override
            public void onResponse(Call<PengaduanModel> call, Response<PengaduanModel> response) {
                if (response.isSuccessful()) {
                    loadImage(view, response.body().getImage());
                }
            }

            @Override
            public void onFailure(Call<PengaduanModel> call, Throwable t) {
            }
        });
    }

    private void loadImage(ImageView view, String imageUrl) {
        Glide.with(requireContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(view);
    }
}