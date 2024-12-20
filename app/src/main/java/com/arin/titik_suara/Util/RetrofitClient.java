package com.arin.titik_suara.Util;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.1.21//API/"; // Ganti dengan URL server yang benar
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface ApiService {
        @Multipart
        @POST("post_pengaduan.php")
        Call<ResponseBody> postPengaduan(
                @Part("deskripsi") RequestBody deskripsi,
                @Part("kategori") RequestBody kategori,
                @Part MultipartBody.Part gambar, // File gambar
                @Part("id_karyawan") RequestBody idKaryawan,
                @Part("id_admin") RequestBody idAdmin
        );
    }
}