package com.arin.titik_suara.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.arin.titik_suara.R;
import com.arin.titik_suara.Model.NotificationModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyNotifAdapter extends RecyclerView.Adapter<MyNotifAdapter.ViewHolder> {
    private Context context;
    private List<NotificationModel> notificationList;

    public MyNotifAdapter(Context context, List<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_all_notifikasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);
        holder.txtKategoriNotifikasi.setText(notification.getCategory());
        holder.txtisiNotifikasi.setText(notification.getMessage());

        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(notification.getTimestamp()));
        holder.txtTimestamp.setText(formattedDate);

        if (!notification.isRead()) {
            // Highlight unread notifications
            holder.txtKategoriNotifikasi.setTypeface(null, Typeface.BOLD);
            holder.txtisiNotifikasi.setTypeface(null, Typeface.BOLD);
            holder.cvPengaduanItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.unread_notification));
        } else {
            // Reset style for read notifications
            holder.txtKategoriNotifikasi.setTypeface(null, Typeface.NORMAL);
            holder.txtisiNotifikasi.setTypeface(null, Typeface.NORMAL);
            holder.cvPengaduanItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.read_notification));
        }

        holder.itemView.setOnClickListener(v -> {
            if (!notification.isRead()) {
                notification.setRead(true);
                notifyItemChanged(position);
                // TODO: Update the read status in your data source (e.g., database)
            }
            // TODO: Handle notification click (e.g., open detail view)
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtKategoriNotifikasi, txtisiNotifikasi, txtTimestamp;
        CardView cvPengaduanItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtKategoriNotifikasi = itemView.findViewById(R.id.txtKategoriNotifikasi);
            txtisiNotifikasi = itemView.findViewById(R.id.txtisiNotifikasi);
            txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
            cvPengaduanItem = itemView.findViewById(R.id.cvPengaduanItem);
        }
    }
}
