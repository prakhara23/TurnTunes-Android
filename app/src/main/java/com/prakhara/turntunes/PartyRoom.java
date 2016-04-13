package com.prakhara.turntunes;

import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;

public class PartyRoom extends MainActivity {

    private String partyName;
    private Firebase nowPlaying;
    private User user;
    private MediaPlayer songPlayer;


    //Soundcloud api keys
    //http://api.soundcloud.com/tracks.json?client_id=77ccdf65d566bdc8bc276ec2f7a6c1fb&q=back%20to%20back%20drake&limit=10
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_room);

        // Set up party details
        Intent intent = getIntent();
        partyName = intent.getStringExtra("PARTY_NAME");
        boolean host = intent.getExtras().getBoolean("HOST_ID");
        user = new User(host, partyName);
        nowPlaying = new Firebase(MAIN_URL + partyName + "/now-playing");
        setUpComponents();

        // Set up Tab Layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.partyToolbar);
        toolbar.setTitle("Party: " + partyName);
        setSupportActionBar(toolbar); // Already have a toolbar, no need to attach it again

        // ViewPager allows management of the lifecycle of pages (used with fragments to switch pages)
        ViewPager viewPager = (ViewPager) findViewById(R.id.partyViewPager);
        setupViewPager(viewPager);

        // Create the tabs that correspond with each page
        TabLayout tabLayout = (TabLayout) findViewById(R.id.partyTabLayout);
        tabLayout.setupWithViewPager(viewPager);


        View bottomSheet = findViewById(R.id.partyNowPlaying);
        setupBottomSheet(bottomSheet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add the search icon and menu options to the ActionBar
        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem addSong = menu.findItem(R.id.search); // The menu item
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(addSong);
        searchView.setQueryHint("Add song from Soundcloud"); // Try and get the resource XML to show this hint

        // What to do when the user submits their search query from the action bar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSoundcloud(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            private void searchSoundcloud(String query) {
                // If we need live search, call this function in onQueryTextChange
                // When user submits the query, send the query to Soundcloud
                query = query.replace(" ", "+");
                String url = "http://api.soundcloud.com/tracks?q=" + query + "&format=json&client_id=77ccdf65d566bdc8bc276ec2f7a6c1fb&limit=10";
                Log.i("PartyRoom fun", query);
                // Make the call to Soundcloud and handle the response
                new AsyncRequest().execute(url);
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

        // Add listener so that when the song changes, the device is notified
        nowPlaying.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Song nowPlaying = dataSnapshot.getValue(Song.class);
                    Log.i("SONG", nowPlaying.toString());
                    changeSong(nowPlaying);

                    // Populate the now playing view
                    ImageView cover = (ImageView) findViewById(R.id.nowPlayingCover);
                    TextView songTitle = (TextView) findViewById(R.id.nowPlayingSong);
                    songTitle.setText(nowPlaying.getSong());
                    String img = nowPlaying.getImg();
                    if (img.equals("img/cover-art.png")) {
                        cover.setImageResource(R.drawable.cover_art);
                    } else {
                        new LoadSongImage(cover).execute(img);
                    }
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
        /* Sey up playlist fragment */
        Fragment playlistFragment = new PlaylistFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("PARTY_URL", MAIN_URL + partyName);
        playlistFragment.setArguments(bundle);

        Fragment chatFragment = new ChatFragment();
        adapter.addFragment(playlistFragment, "PLAYLIST");
        adapter.addFragment(chatFragment, "CHAT");
        view.setAdapter(adapter);
    }

    private void setupBottomSheet(View bottomSheet) {
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        final View partyRoom = findViewById(R.id.partyRoom);
        partyRoom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                partyRoom.getWindowVisibleDisplayFrame(r);
                int screenHeight = partyRoom.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    behavior.setHideable(true);
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                else {
                    behavior.setHideable(false);
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

}
