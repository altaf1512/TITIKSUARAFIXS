package com.arin.titik_suara.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.arin.titik_suara.Adapter.MyNotifAdapter;
import com.arin.titik_suara.Model.NotificationModel;
import com.arin.titik_suara.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class NotifFragment extends Fragment {

    private RecyclerView rvMyNotifications;
    private TextView titleEmpty;
    private MyNotifAdapter myNotifAdapter;
    private List<NotificationModel> notificationList;

    private static final String API_URL = "http://192.168.1.21/API/notif.php?id_pengguna=1"; // Ganti dengan URL API Anda dan ID Pengguna

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        rvMyNotifications = view.findViewById(R.id.rvMyPengaduan);
        titleEmpty = view.findViewById(R.id.titleEmpty);

        notificationList = new ArrayList<>();
        myNotifAdapter = new MyNotifAdapter(getContext(), notificationList);

        rvMyNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyNotifications.setAdapter(myNotifAdapter);

        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        // Membuat request menggunakan Volley
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL,  // URL API yang Anda buat
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            notificationList.clear(); // Bersihkan daftar sebelumnya

                            // Loop untuk mengambil data dari response JSON
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject notifObject = response.getJSONObject(i);
                                String category = notifObject.getString("category");
                                String message = notifObject.getString("message");
                                long timestamp = notifObject.getLong("timestamp");
                                boolean isRead = notifObject.getBoolean("isRead");

                                // Membuat model notifikasi dan menambahkannya ke list
                                NotificationModel notification = new NotificationModel(category, message, timestamp);
                                notification.setRead(isRead);
                                notificationList.add(notification);
                            }

                            // Periksa apakah notificationList kosong
                            if (notificationList.isEmpty()) {
                                titleEmpty.setVisibility(View.VISIBLE);
                                rvMyNotifications.setVisibility(View.GONE);
                            } else {
                                titleEmpty.setVisibility(View.GONE);
                                rvMyNotifications.setVisibility(View.VISIBLE);
                                myNotifAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing notifications", Toast.LENGTH_SHORT).show();
                            Log.d("NotifFragment", "Response from server: " + response.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (getActivity() == null) {
                            Log.e("NotifFragment", "Context is null, cannot show Toast");
                            return;
                        }

                        String errorMessage = "Terjadi kesalahan: " + (error.getMessage() != null ? error.getMessage() : "Tidak diketahui");
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("NotifFragment", errorMessage);
                    }
                }
        );

        // Menambahkan request ke request queue
        requestQueue.add(jsonArrayRequest);
    }


    public void addNewNotification(String category, String message) {
        NotificationModel newNotification = new NotificationModel(category, message, System.currentTimeMillis());
        notificationList.add(0, newNotification); // Add to the top of the list
        myNotifAdapter.notifyItemInserted(0);
        rvMyNotifications.scrollToPosition(0); // Scroll to the top to show the new notification

        if (titleEmpty.getVisibility() == View.VISIBLE) {
            titleEmpty.setVisibility(View.GONE);
            rvMyNotifications.setVisibility(View.VISIBLE);
        }
    }
}
