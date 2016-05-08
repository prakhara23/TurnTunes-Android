package com.prakhara.turntunes;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.Firebase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchResults extends ListActivity {

    SongResultsAdapter listAdapter;
    Firebase nowPlaying;
    ArrayList<Song> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Setup the adapter
        searchResults = new ArrayList<Song>();
        listAdapter = new SongResultsAdapter(this, searchResults);
        setListAdapter(listAdapter);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //setNowPlayingRef(intent.getStringExtra("MAIN_URL"), intent.getStringExtra("PARTY_NAME"));
            searchSoundcloud(query);
        }
    }

    private void searchSoundcloud(String query) {
        // If we need live search, call this function in onQueryTextChange
        // When user submits the query, send the query to Soundcloud
        query = query.replace(" ", "+");
        String url = "http://api.soundcloud.com/tracks?q=" + query + "&format=json&client_id=77ccdf65d566bdc8bc276ec2f7a6c1fb&limit=10";
        Log.i("PartyRoom fun", query);
        // Make the call to Soundcloud and handle the response
        RequestSongs search = new RequestSongs(this);
        search.execute(url);
    }

    private void addSong(Song song) {
        Map<String, String> nowPlayingSong = new HashMap<String, String>();
        nowPlayingSong.put("song", song.getSong());
        nowPlayingSong.put("img", song.getImg());
        nowPlayingSong.put("url", song.getUrl());
        nowPlaying.setValue(nowPlayingSong);
    }

    public void addResult(Song song) {
        listAdapter.add(song);
    }

    public void setNowPlayingRef(String mainUrl, String partyName) {
        nowPlaying = new Firebase(mainUrl + partyName + "/now-playing");
    }

    public class SongResultsAdapter extends ArrayAdapter<Song> {

        public SongResultsAdapter(Context context, ArrayList<Song> results) {
            super(context, 0, results);
        }

        @Override
        public View getView(int position, View resultsView, ViewGroup parent) {
            // Get the data item for this position
            final Song song = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (resultsView == null) {
                resultsView = LayoutInflater.from(getContext()).inflate(R.layout.song_card_view, parent, false);
            }
            // Lookup view for data population
            TextView songName = (TextView) resultsView.findViewById(R.id.playlistSong);
            ImageView cover = (ImageView) resultsView.findViewById(R.id.playlistCover);
            // Populate the data into the template view using the data object
            songName.setText(song.getSong());
            String coverUrl = song.getImg();

            if (coverUrl.equals("img/cover-art.png")) {
                cover.setImageResource(R.drawable.cover_art);
            } else {
                new LoadSongImage(cover).execute(coverUrl);
            }
            resultsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSong(song);
                }
            });
            // Return the completed view to render on screen
            return resultsView;
        }

    }
}
