package com.arin.titik_suara.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arin.titik_suara.Fragment.PengaduanItem;
import com.arin.titik_suara.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PengaduanListAdapter extends RecyclerView.Adapter<PengaduanListAdapter.ViewHolder> {
    private Context context;
    private List<PengaduanItem> pengaduanList;

    public PengaduanListAdapter(Context context, List<PengaduanItem> pengaduanList) {
        this.context = context;
        this.pengaduanList = pengaduanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pengaduan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PengaduanItem item = pengaduanList.get(position);

        holder.tvDeskripsi.setText(item.getDeskripsi());
        holder.tvKategori.setText("Kategori: " + item.getKategori());
        holder.tvStatus.setText("Status: " + getStatusText(item.getStatus()));
        holder.tvTanggal.setText("Dibuat: " + formatDate(item.getDibuat_pada()));

    }

    @Override
    public int getItemCount() {
        return pengaduanList.size();
    }

    private String getStatusText(int status) {
        switch(status) {
            case 1: return "Menunggu";
            case 2: return "Diproses";
            case 3: return "Selesai";
            default: return "Unknown";
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id", "ID"));
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeskripsi, tvKategori, tvStatus, tvTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeskripsi = itemView.findViewById(R.id.tv_deskripsi);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
        }
    }
}