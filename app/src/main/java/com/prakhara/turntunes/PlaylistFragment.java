package com.prakhara.turntunes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Song> playlist = new ArrayList<Song>();
    private static Firebase playlistRef;

    public PlaylistFragment() { }

    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        String partyName = getArguments().getString("PARTY_URL");
        playlistRef = new Firebase(partyName + "/playlist");
        setUpFirebaseRef();
    }
    //http://androidphonesreviewmu3.blogspot.ca/2016/01/adding-items-in-listview-from-firebase.html
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.partyPlaylist);
        mRecyclerView.setHasFixedSize(true); // Change this is the card sizes change

        mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PlaylistRecyclerAdapter(playlist);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void setUpFirebaseRef() {
        playlistRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Song song = dataSnapshot.getValue(Song.class);
                playlist.add(song);
                mAdapter.notifyDataSetChanged();
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
    }
}
