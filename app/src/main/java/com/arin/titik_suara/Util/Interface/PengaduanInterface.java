package com.arin.titik_suara.Util.Interface;

import com.arin.titik_suara.Model.PengaduanModel;
import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PengaduanInterface {
    // Get kategori
    @GET("user/pengaduan/getkategori")
    Call<List<PengaduanModel>> getKategori();

    // Create pengaduan dengan lampiran
    @Multipart
    @POST("user/pengaduan/create")
    Call<PengaduanModel> createPengaduan(
            @PartMap Map<String, RequestBody> textData,
            @Part MultipartBody.Part image
    );

    // Get semua pengaduan
    @GET("user/pengaduan/getAllPengaduan")
    Call<List<PengaduanModel>> getAllPengaduan();

    // Get pengaduan berdasarkan karyawan (pelapor)
    @GET("user/pengaduan/getPengaduanByUserId")
    Call<List<PengaduanModel>> getMyPengaduan(
            @Query("id_pengaduan") String idPengaduan,
            @Query("kategori") String kategori
    );

    // Update pengaduan
    @FormUrlEncoded
    @POST("user/pengaduan/update")
    Call<PengaduanModel> updatePengaduan(
            @Field("deskripsi") String deskripsi,
            @Field("id_pengaduan") String idPengaduan,
            @Field("kategori") String kategori,
            @Field("id_admin") String idAdmin  // Menggunakan id_admin untuk pengelolaan oleh admin
    );

    // Update gambar pengaduan
    @GET("pengaduan/image/{id}/{field}")
    Call<PengaduanModel> getImage(
            @Path("id") String id,
            @Path("field") String field
    );

    @Multipart
    @POST("pengaduan/updateImage")
    Call<PengaduanModel> updateImage(
            @PartMap Map<String, RequestBody> params,
            @Part MultipartBody.Part image
    );

    // Get pengaduan berdasarkan ID pengaduan
    @GET("user/pengaduan/getPengaduanById")
    Call<List<PengaduanModel>> getPengaduanById(
            @Query("id_pengaduan") String idPengaduan
    );

    // Filter pengaduan berdasarkan parameter yang diberikan
    @GET("user/pengaduan/filterpengaduan")
    Call<List<PengaduanModel>> filterPengaduan(
            @Query("id_pengguna") String idPengguna,  // Dapat digunakan oleh admin atau karyawan
            @Query("kategori") String kategori,
            @Query("date_start") String dateStart,
            @Query("date_end") String dateEnd
    );

    // Get pengaduan berdasarkan status (newly added)
    @GET("user/pengaduan/getPengaduanByStatus")
    Call<List<PengaduanModel>> getPengaduanByStatus(
            @Query("id_pengguna") String idPengguna,
            @Query("status") String status
    );
}