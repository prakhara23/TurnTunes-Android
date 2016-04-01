package com.prakhara.turntunes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Needed for HTTP request to the Soundcloud api, having the singleton class allows
 *  us to use the same object for Requests through the lifetime of app instead of creating a
 *  RequestQueue many times
 */
public class LoadSongImage extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    public LoadSongImage(ImageView songCover) {
        this.imageView = songCover;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap cover = null;
        try {
            // Retrieve the URL passed and then connect to the site
            InputStream image = new java.net.URL(params[0]).openStream();
            cover = BitmapFactory.decodeStream(image);
            image.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cover;
    }

    @Override
    protected void onPostExecute(Bitmap cover) {
        // The results of the search
        imageView.setImageBitmap(cover);
    }
}
