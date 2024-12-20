package com.arin.titik_suara.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyConnection {
    private static VolleyConnection instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private Context context;

    private VolleyConnection(Context context) {
        this.context = context.getApplicationContext();
        requestQueue = getRequestQueue();

        // Setup ImageLoader with LruCache for efficient image loading
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    // Singleton method to get instance
    public static synchronized VolleyConnection getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyConnection(context);
        }
        return instance;
    }

    // Get RequestQueue, create if not exists
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }


    // Add request to queue
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    // Get ImageLoader
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    // Optional: Cancel all pending requests with a specific tag
    public void cancelAllRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
}

}
