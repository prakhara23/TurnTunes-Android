package com.prakhara.turntunes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaylistFragment extends Fragment {

    public PlaylistFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        return view;
    }

    public void insertSong(Song song) {

    }
}
