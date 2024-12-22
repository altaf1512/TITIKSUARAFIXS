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


import com.android.volley.DefaultRetryPolicy;
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

    private static final String TAG = "NotifFragment";
    private RecyclerView rvMyNotifications;
    private TextView titleEmpty;
    private MyNotifAdapter myNotifAdapter;
    private List<NotificationModel> notificationList;

    private RequestQueue requestQueue;

    private static final String API_URL = "http://192.168.1.7/API/notif.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Initialize views
        rvMyNotifications = view.findViewById(R.id.rvMyPengaduan);
        titleEmpty = view.findViewById(R.id.titleEmpty);


        // Initialize request queue
        requestQueue = Volley.newRequestQueue(requireContext());

        // Setup RecyclerView
        setupRecyclerView();



        // Load initial data
        loadNotifications();

        return view;
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();
        myNotifAdapter = new MyNotifAdapter(getContext(), notificationList);
        rvMyNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyNotifications.setHasFixedSize(true);
        rvMyNotifications.setAdapter(myNotifAdapter);
    }



    private void loadNotifications() {


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            notificationList.clear();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject notifObject = response.getJSONObject(i);

                                NotificationModel notification = new NotificationModel(
                                        notifObject.getInt("id"),
                                        notifObject.getInt("user_id"),
                                        notifObject.getString("message"),
                                        notifObject.getLong("timestamp")
                                );
                                notification.setRead(notifObject.getBoolean("isRead"));
                                notificationList.add(notification);
                            }

                            updateUI();
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            showError("Error parsing notifications");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Network error: " + error.getMessage());
                        showError("Network error occurred");

                    }
                }
        );

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonArrayRequest);
    }

    private void updateUI() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (notificationList.isEmpty()) {
                    titleEmpty.setVisibility(View.VISIBLE);
                    rvMyNotifications.setVisibility(View.GONE);
                } else {
                    titleEmpty.setVisibility(View.GONE);
                    rvMyNotifications.setVisibility(View.VISIBLE);
                }
                myNotifAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showError(final String message) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    public void addNewNotification(NotificationModel notification) {
        if (notification != null) {
            notificationList.add(0, notification);
            updateUI();
            rvMyNotifications.scrollToPosition(0);
        }
    }
}