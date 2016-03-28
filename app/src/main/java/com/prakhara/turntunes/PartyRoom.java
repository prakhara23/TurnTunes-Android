package com.prakhara.turntunes;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Queue;

public class PartyRoom extends MainActivity {

    private static Firebase playlist;
    private static Firebase nowPlaying;
    private static User user;
    private Queue<Song> songQueue;
    private MediaPlayer songPlayer;


    //Soundcloud api keys
    //http://api.soundcloud.com/tracks.json?client_id=77ccdf65d566bdc8bc276ec2f7a6c1fb&q=back%20to%20back%20drake&limit=10
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_room);

        // Set up Tab Layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.partyToolbar);
        setSupportActionBar(toolbar);

        // ViewPager allows management of the lifecycle of pages (used with fragments to switch pages)
        ViewPager viewPager = (ViewPager) findViewById(R.id.partyViewPager);
        setupViewPager(viewPager);

        // Create the tabs that correspond with each page
        TabLayout tabLayout = (TabLayout) findViewById(R.id.partyTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // Set up party details
        Intent intent = getIntent();
        String partyName = intent.getStringExtra("PARTY_NAME");
        boolean host = intent.getExtras().getBoolean("HOST_ID");
        user = new User(host, partyName);
        playlist = new Firebase(MAIN_URL + partyName + "/playlist");
        nowPlaying = new Firebase(MAIN_URL + partyName + "/now-playing");
        setTitle("Party: " + partyName);

        setUpComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add the search icon and menu options to the ActionBar
        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem addSong = menu.findItem(R.id.search); // The menu item
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(addSong);
        searchView.setQueryHint("Add Song From Soundcloud"); // Try and get the resource XML to show this hint

        // What to do when the user submits their search query from the action bar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // When user submits the query, send the query to Soundcloud
                query = query.replace(" ", "+");
                String url = "http://api.soundcloud.com/tracks?q=" + query + "&format=json&client_id=77ccdf65d566bdc8bc276ec2f7a6c1fb&limit=10";
                Log.i("PartyRoom", query);
                // Make the call to Soundcloud and handle the response
                new AsyncRequest().execute(url);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        // Release the MediaPlayer when activity is destroyed to prevent memory leak
        songPlayer.release();
        songPlayer = null;
        super.onDestroy();
    }

    private void setUpComponents() {
        // Set the Media Player
        //http://developer.android.com/guide/topics/media/mediaplayer.html
        songPlayer = new MediaPlayer();
        songPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        songPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Need to stop the music player in order to move to the next song
                mp.stop();
                mp.reset();
            }
        });
        songPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        // Add listener to check when a new song is added (All songs in playlist gets added when Activity loads)
        playlist.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Song song = dataSnapshot.getValue(Song.class);
                Log.i("FIREBASE SONG Line 111", song.getSong());
            }

            // Don't need any of the following methods for now
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        //Looks like we don't need it
        //http://stackoverflow.com/questions/27978078/how-to-separate-initial-data-load-from-incremental-children-with-firebase
        //this is for playlist addValueListenerjj

        // Add listener so that when the song changes, the device is notified
        nowPlaying.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Song nowPlaying = dataSnapshot.getValue(Song.class);
                    Log.i("SONG", nowPlaying.toString());
                    changeSong(nowPlaying);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    private void changeSong(Song song) {
        //Testing to play music
        if (songPlayer.isPlaying()) {
            songPlayer.stop();
            songPlayer.reset();
        }
        try {
            songPlayer.setDataSource(song.getUrl());
            // Ensure that is is Async so that the UI doesn't lock while the MP is preparing
            songPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager (ViewPager view) {
        // Create adapter to populate the pages inside of our ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PlaylistFragment(), "PLAYLIST");
        adapter.addFragment(new NowPlayingFragment(), "NOW PLAYING");
        view.setAdapter(adapter);
    }


}
