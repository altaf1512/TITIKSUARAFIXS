package com.arin.titik_suara;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText et_username, et_password;

    SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String URL_LOGIN = "http://192.168.1.21/API/login.php"; // Ganti dengan URL API login Anda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        // Periksa apakah user sudah login sebelumnya
        if (sharedPreferences.getBoolean("Login", false)) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void btnLogin(View view) {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            Toasty.error(this, "Username dan password tidak boleh kosong!", Toasty.LENGTH_SHORT).show();
        } else {
            loginUser(username, password);
        }
    }

    private void loginUser(String username, String password) {
        // Membuat permintaan HTTP menggunakan Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parsing respon sebagai JSONObject
                            JSONObject jsonObject = new JSONObject(response);

                            // Ambil nilai success dan message dari respon
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");

                            if (success) {
                                // Jika login berhasil
                                Toasty.success(LoginActivity.this, "Login berhasil!", Toasty.LENGTH_SHORT).show();

                                // Simpan status login di SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("login", true);
                                editor.putString("nama_lengkap", username);
                                editor.apply();

                                // Pindah ke MainActivity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                // Jika login gagal
                                Toasty.error(LoginActivity.this, message, Toasty.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toasty.error(LoginActivity.this, "Kesalahan parsing data!", Toasty.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(LoginActivity.this, "Gagal terhubung ke server!", Toasty.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Kirimkan parameter ke server (username & password)
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        // Tambahkan request ke RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}