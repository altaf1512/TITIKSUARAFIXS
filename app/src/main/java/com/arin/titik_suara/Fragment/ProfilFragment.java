package com.arin.titik_suara.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.graphics.Outline;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.arin.titik_suara.LoginActivity;
import com.arin.titik_suara.R;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfilFragment extends Fragment {
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;

    TextView btnLogout, profileName, profileJabatan;
    ImageView profileIcon, cameraIcon;
    Button btnChangePassword;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        // Initialize SharedPreferences - use the same name as in LoginActivity
        sharedPreferences = getActivity().getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        initializeViews(view);
        setupClickListeners();
        loadProfileData();

        return view;
    }

    private void initializeViews(View view) {
        profileIcon = view.findViewById(R.id.profile_icon);
        profileName = view.findViewById(R.id.profile_name);
        profileJabatan = view.findViewById(R.id.profile_jabatan);
//        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);
        cameraIcon = view.findViewById(R.id.camera_icon);

        // Set circular outline for profile icon
        profileIcon.setClipToOutline(true);
        profileIcon.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
    }

    private void loadProfileData() {
        // Get user data from SharedPreferences
        String namaLengkap = sharedPreferences.getString("nama_lengkap", "");
        String jabatan = sharedPreferences.getString("peran", "");
        String fotoProfile = sharedPreferences.getString("foto_profil", "");

        // Set the data to views
        if (!TextUtils.isEmpty(namaLengkap)) {
            profileName.setText(namaLengkap);
        }

        if (!TextUtils.isEmpty(jabatan)) {
            profileJabatan.setText("Jabatan: " + jabatan);
        }

        // Load profile picture if available
        if (!TextUtils.isEmpty(fotoProfile)) {
            Glide.with(requireContext())
                    .load(Uri.parse(fotoProfile))
                    .circleCrop()
                    .placeholder(R.drawable.baseline_person_24)
                    .error(R.drawable.baseline_person_24)
                    .into(profileIcon);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            if (data != null) {
                imageUri = data.getData();

                // Save the new profile picture URI to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("foto_profil", imageUri.toString());
                editor.apply();

                // Update the profile picture
                Glide.with(requireContext())
                        .load(imageUri)
                        .circleCrop()
                        .into(profileIcon);
            }
        }
    }


    private void setupClickListeners() {
        cameraIcon.setOnClickListener(v -> openGallery());
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> logout())
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logout() {
        // Clear all stored user data
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
}