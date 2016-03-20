package com.prakhara.turntunes;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Needed for HTTP request to the Soundcloud api, having the singleton class allows
 *  us to use the same object for Requests through the lifetime of app instead of creating a
 *  RequestQueue many times
 */
public class GetSongs extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection soundcloud = null;
        BufferedReader content = null;

        try {
            // Retrieve the URL passed and then connect to the site
            URL url  = new URL(params[0]);
            soundcloud = (HttpURLConnection) url.openConnection();
            soundcloud.connect();

            // Prepare the reading of the data from Soundcloud API
            InputStream stream = soundcloud.getInputStream();
            content = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();

            // Read each line of the result
            String lineData;
            while((lineData = content.readLine()) != null) {
                buffer.append(lineData);
            }
            //Return the JSON
            Log.i("PartyRoom", buffer.toString());

            // Turn the JSON response into a string so we can turn it into an array
            String searchResults = buffer.toString();
            // The results of the search
            JSONArray results = new JSONArray(searchResults);

            for (int i = 0; i < results.length(); i++) {
                // Turn each song from the response into a java native JSON Obj
                JSONObject song = results.getJSONObject(i);
                Log.i("PartyRoom", song.getString("title"));
                Log.i("PartyRoom", song.getJSONObject("user").getString("username"));
            }

            // Return the results
            return searchResults;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Always ensure that the HTTP connection is disconnected after it has been used
            if (soundcloud != null) {
                soundcloud.disconnect();
            }
            try {
                if (content != null) {
                    content.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
