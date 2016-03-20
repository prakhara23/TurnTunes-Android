package com.prakhara.turntunes;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    protected static final String MAIN_URL = "https://dazzling-torch-8949.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
    }

    private void joinPartyExists(final String party, final boolean user) {
        Firebase main = new Firebase(MAIN_URL);
        main.child(party).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    //party does not exist, do something else
                    getSnackBar("Party does not exist");
                } else {
                    loadParty(party, user);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    private void loadParty(String party, boolean isHost) {
        Intent intent = new Intent(MainActivity.this, PartyRoom.class);
        intent.putExtra("PARTY_NAME", party);
        intent.putExtra("HOST_ID", isHost);
        startActivity(intent);
    }

    private void getSnackBar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snack = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);
        snack.show();
    }

    //To generate a random string for party name
    private String generateRandomKey() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public void joinParty(String party) {
        if (party.matches("")) {
            getSnackBar("Please enter a party name");
        } else {
            joinPartyExists(party, false);
        }
    }

    public void requestParty(View v) {
        FragmentManager fm = getFragmentManager();
        JoinPartyDialog dialog = new JoinPartyDialog();
        dialog.show(fm, "Dialog");
    }

    public void hostParty(View v) {
        loadParty(generateRandomKey(), true);
    }
}
