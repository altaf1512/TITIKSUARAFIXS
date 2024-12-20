package com.arin.titik_suara.Fragment;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.arin.titik_suara.R;

public class DetailPengaduanFragment extends Fragment {

    TextView tvDeskripsiPengaduan, tvStatusPengaduan1;
    CardView cvStatusPengaduan;
    ImageView ivFoto1, ivFoto2, ivFoto3, icStatus;
    EditText etDeskripsiPengaduan, etKategori, etTanggal, etKelurahan;
    String idPengaduan, deskripsiPengaduan, namaKelurahan, statusPengaduan, tglPengaduan, foto1, foto2, foto3, kategori;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_pengaduan, container, false);

        // Safely retrieve data from arguments
        if (getArguments() != null) {
            idPengaduan = getArguments().getString("id_pengaduan");
            deskripsiPengaduan = getArguments().getString("deskripsi");
            namaKelurahan = getArguments().getString("kelurahan");
            statusPengaduan = getArguments().getString("status_pengaduan");
            tglPengaduan = getArguments().getString("dibuat_pada");
            foto1 = getArguments().getString("foto");
            foto2 = getArguments().getString("foto1");
            foto3 = getArguments().getString("foto2");
            kategori = getArguments().getString("kategori");
        }

        // Initialize view elements
        tvDeskripsiPengaduan = view.findViewById(R.id.etDeskripsiPengaduan);
        tvStatusPengaduan1 = view.findViewById(R.id.tvStatusPengaduan);
        etDeskripsiPengaduan = view.findViewById(R.id.etDeskripsiPengaduan);
        etKategori = view.findViewById(R.id.etKategoriPengaduan);
        etKelurahan = view.findViewById(R.id.etKelurahan);
        etTanggal = view.findViewById(R.id.etTanggal);
        cvStatusPengaduan = view.findViewById(R.id.cvStatusPengaduan);
        icStatus = view.findViewById(R.id.icStatus);
        ivFoto1 = view.findViewById(R.id.foto1);
        ivFoto2 = view.findViewById(R.id.foto2);
        ivFoto3 = view.findViewById(R.id.foto3);

        // Displaying the details of the report
        tvDeskripsiPengaduan.setText(deskripsiPengaduan);
        etDeskripsiPengaduan.setText(deskripsiPengaduan);
        etTanggal.setText(tglPengaduan);
        etKelurahan.setText(namaKelurahan);
        etKategori.setText(kategori);

        // Adjusting the status of the report
        setStatusPengaduan();

        // Display images from the attachments
        setImage(ivFoto1, foto1);
        setImage(ivFoto2, foto2);
        setImage(ivFoto3, foto3);

        // Set up full-screen image functionality
        fullScreenImage(ivFoto1, foto1);
        fullScreenImage(ivFoto2, foto2);
        fullScreenImage(ivFoto3, foto3);

        // Disable the EditText fields to make them non-editable
        etDeskripsiPengaduan.setEnabled(false);
        etKategori.setEnabled(false);
        etKelurahan.setEnabled(false);
        etTanggal.setEnabled(false);

        return view;
    }

    private void setStatusPengaduan() {
        // Change the UI elements based on the status of the report
        if (statusPengaduan != null) {
            switch (statusPengaduan) {
                case "selesai":
                    cvStatusPengaduan.setCardBackgroundColor(getResources().getColor(R.color.unread_notification));
                    tvStatusPengaduan1.setTextColor(getResources().getColor(R.color.white));
                    icStatus.setImageDrawable(getResources().getDrawable(R.drawable.baseline_calendar_month_24));
                    tvStatusPengaduan1.setText(statusPengaduan);
                    break;
                case "proses":
                    cvStatusPengaduan.setCardBackgroundColor(getResources().getColor(R.color.main));
                    icStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_proses));
                    tvStatusPengaduan1.setText(statusPengaduan);
                    break;
                case "valid":
                    cvStatusPengaduan.setCardBackgroundColor(getResources().getColor(R.color.green));
                    tvStatusPengaduan1.setTextColor(getResources().getColor(R.color.white));
                    icStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_osis));
                    tvStatusPengaduan1.setText(statusPengaduan);
                    break;
                case "pengerjaan":
                    cvStatusPengaduan.setCardBackgroundColor(getResources().getColor(R.color.red));
                    icStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_dashboard));
                    tvStatusPengaduan1.setText(statusPengaduan);
                    break;
                case "belum_ditanggapi":
                    cvStatusPengaduan.setCardBackgroundColor(getResources().getColor(R.color.gray));
                    tvStatusPengaduan1.setTextColor(getResources().getColor(R.color.black));
                    icStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_tolak));
                    tvStatusPengaduan1.setText("Belum ditanggapi");
                    break;
                case "tidak_valid":
                    cvStatusPengaduan.setCardBackgroundColor(getResources().getColor(R.color.gray));
                    tvStatusPengaduan1.setTextColor(getResources().getColor(R.color.black));
                    icStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_tolak));
                    tvStatusPengaduan1.setText("Tidak valid");
                    break;
                default:
                    break;
            }
        }
    }

    private void setImage(ImageView image, String resource) {
        Glide.with(requireContext())  // Using requireContext() to avoid null context
                .load(resource)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()
                .into(image);
    }

    private void fullScreenImage(ImageView img, String resource) {
        img.setOnClickListener(v -> {
            Fragment f = new DetailFullScreenImageFragment();
            Bundle b = new Bundle();
            b.putString("image", resource);
            f.setArguments(b);
            getParentFragmentManager()  // Using getParentFragmentManager() instead of getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, f)
                    .addToBackStack(null)
                    .commit();
        });
    }
}