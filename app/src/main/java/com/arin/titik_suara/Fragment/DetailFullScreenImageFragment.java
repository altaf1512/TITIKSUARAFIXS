package com.arin.titik_suara.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.arin.titik_suara.R;

public class DetailFullScreenImageFragment extends Fragment {
    ImageView photoView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_full_screen_image, container, false);

        // Safely retrieve the image URL from the arguments
        String image = null;
        if (getArguments() != null) {
            image = getArguments().getString("image");
        }

        // Check if the image URL is not null or empty
        if (image != null && !image.isEmpty()) {
            photoView = view.findViewById(R.id.photoView);

            // Load the image into the ImageView using Glide
            Glide.with(requireContext())  // Using requireContext() instead of getContext() for safety
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // Avoid caching the image
                    .skipMemoryCache(true)  // Skip memory cache for faster loading
                    .into(photoView);
        }

        return view;
    }
}