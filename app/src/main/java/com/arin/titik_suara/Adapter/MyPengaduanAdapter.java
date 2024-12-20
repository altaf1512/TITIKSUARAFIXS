package com.arin.titik_suara.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.arin.titik_suara.Fragment.DetailPengaduanFragment;
import com.arin.titik_suara.Fragment.EditPengaduanFragment;
import com.arin.titik_suara.Model.PengaduanModel;
import com.arin.titik_suara.R;

import java.util.List;

public class MyPengaduanAdapter extends RecyclerView.Adapter<MyPengaduanAdapter.ViewHolder> {
    private final Context context;
    private final List<PengaduanModel> pengaduanModelList;

    public MyPengaduanAdapter(Context context, List<PengaduanModel> pengaduanModelList) {
        this.context = context;
        this.pengaduanModelList = pengaduanModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_all_pengaduan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PengaduanModel currentPengaduan = pengaduanModelList.get(position);

        // Set the description of the report
        holder.tvIsiLaporan.setText(currentPengaduan.getDeskripsi());

        // Load the photo using Glide
        Glide.with(context)
                .load(currentPengaduan.getFoto())
                .override(200, 200)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .dontAnimate()
                .fitCenter()
                .thumbnail(0.5f)
                .centerCrop()
                .into(holder.imgPengaduan);

        // Update the status of the report in the UI
        updateStatusUI(holder, currentPengaduan);

        // Handling the click event for showing the options dialog
        holder.cvPengaduanItem.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.layout_option_menu);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            Button btnEditPengaduan = dialog.findViewById(R.id.btnEditMyPengaduan);
            Button btnDetailPengaduan = dialog.findViewById(R.id.btnDetailPengaduan);
            dialog.show();

            // Show edit option only if the status is "belum_ditanggapi"
            if ("belum_ditanggapi".equals(currentPengaduan.getStatusPengaduan())) {
                btnEditPengaduan.setVisibility(View.VISIBLE);
            } else {
                btnEditPengaduan.setVisibility(View.GONE);
            }

            // Edit button click listener
            btnEditPengaduan.setOnClickListener(v1 -> {
                dialog.dismiss();
                navigateToFragment(new EditPengaduanFragment(), currentPengaduan);
            });

            // Detail button click listener
            btnDetailPengaduan.setOnClickListener(v1 -> {
                dialog.dismiss();
                navigateToFragment(new DetailPengaduanFragment(), currentPengaduan);
            });
        });
    }

    private void navigateToFragment(Fragment fragment, PengaduanModel pengaduan) {
        // Pass the current pengaduan details to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("id_pengaduan", pengaduan.getIdPengaduan());
        bundle.putString("deskripsi", pengaduan.getDeskripsi());
        bundle.putString("kategori", pengaduan.getKategori());
        bundle.putString("foto", pengaduan.getFoto());
        bundle.putString("status_pengaduan", pengaduan.getStatusPengaduan());
        fragment.setArguments(bundle);

        // Replace the fragment
        FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateStatusUI(ViewHolder holder, PengaduanModel pengaduan) {
        String status = pengaduan.getStatusPengaduan();
        int color, statusIcon;

        switch (status) {
            case "proses":
                color = context.getResources().getColor(R.color.main);
                statusIcon = R.drawable.ic_diajukan;
                break;
            case "selesai":
                color = context.getResources().getColor(R.color.red);
                statusIcon = R.drawable.ic_selesai;
                break;
            case "valid":
                color = context.getResources().getColor(R.color.green);
                statusIcon = R.drawable.ic_osis;
                break;
            case "pengerjaan":
                color = context.getResources().getColor(R.color.white);
                statusIcon = R.drawable.ic_proses;
                break;
            case "belum_ditanggapi":
            case "tidak_valid":
                color = context.getResources().getColor(R.color.gray);
                statusIcon = R.drawable.ic_tolak;
                break;
            default:
                color = context.getResources().getColor(R.color.white);
                statusIcon = R.drawable.ic_pengaduan;
                break;
        }

        // Set the status text and UI elements according to the status
        holder.tvStatusPengaduan.setTextColor(context.getResources().getColor(R.color.white));
        holder.tvStatusPengaduan.setText(status);
        holder.vPengaduan.setBackgroundColor(color);
        holder.cvTanggapanStatus.setCardBackgroundColor(color);
        holder.icStatus.setImageDrawable(context.getDrawable(statusIcon));
    }

    @Override
    public int getItemCount() {
        return pengaduanModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIsiLaporan, tvStatusPengaduan;
        ImageView imgPengaduan, icStatus;
        CardView cvPengaduanItem, cvTanggapanStatus;
        View vPengaduan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIsiLaporan = itemView.findViewById(R.id.txtDeskripsiPengaduan);
            tvStatusPengaduan = itemView.findViewById(R.id.tvStatusPengaduan);
            imgPengaduan = itemView.findViewById(R.id.imgPengaduan);
            icStatus = itemView.findViewById(R.id.icStatus);
            cvPengaduanItem = itemView.findViewById(R.id.cvPengaduanItem);
            vPengaduan = itemView.findViewById(R.id.vPengaduan);
        }
    }
}