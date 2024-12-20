package com.arin.titik_suara.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.arin.titik_suara.Model.PengaduanModel;
import com.arin.titik_suara.R;
import com.arin.titik_suara.Util.DataApi;
import com.arin.titik_suara.Util.Interface.PengaduanInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenImageFragment extends Fragment {

    private ImageView photoView;
    private List<PengaduanModel> pengaduanModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_image_all, container, false);
        photoView = view.findViewById(R.id.photoView);

        // Initialize API interface
        PengaduanInterface pengaduanInterface = DataApi.getClient().create(PengaduanInterface.class);
        String idPengaduan = getArguments() != null ? getArguments().getString("id_pengaduan") : null;

        // Fetch pengaduan details based on the ID
        if (idPengaduan != null) {
            pengaduanInterface.getPengaduanById(idPengaduan).enqueue(new Callback<List<PengaduanModel>>() {
                @Override
                public void onResponse(Call<List<PengaduanModel>> call, Response<List<PengaduanModel>> response) {
                    pengaduanModelList = response.body();

                    if (pengaduanModelList != null && !pengaduanModelList.isEmpty()) {
                        String field = getArguments().getString("field");
                        String imageUrl = null;

                        // Choose the correct image based on the "field" argument
                        switch (field != null ? field : "") {
                            case "foto":
                                imageUrl = pengaduanModelList.get(0).getFoto();
                                break;
                            default:
                                break;
                        }

                        // Load the image into the ImageView using Glide
                        if (imageUrl != null) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(photoView);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<PengaduanModel>> call, Throwable t) {
                    // Handle failure (e.g., log the error, show a message)
                }
            });
        }

        return view;
    }
}