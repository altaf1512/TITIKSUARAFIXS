package com.arin.titik_suara.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.arin.titik_suara.R;

public class FullScreenImageAllFragment extends Fragment {

    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_image_all, container, false);

        // Initialize ImageView
        imageView = view.findViewById(R.id.photoView);

        // Load image using Glide from the provided argument
        String imageUrl = getArguments() != null ? getArguments().getString("image") : null;
        if (imageUrl != null) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
        }

        return view;
    }
}
