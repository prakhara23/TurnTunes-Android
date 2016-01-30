package com.prakhara.turntunes;

import android.content.Intent;
import android.os.Bundle;
import com.firebase.client.Firebase;
import java.util.Queue;

public class PartyRoom extends MainActivity {

    private static Firebase party;
    private static User user;
    private Queue<Song> songQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_room);

        Intent intent = getIntent();
        String partyName = intent.getStringExtra("PARTY_NAME");
        boolean host = intent.getExtras().getBoolean("HOST_ID");
        user = new User(host);
        party = new Firebase(MAIN_URL + partyName);
        setTitle("Party: " + partyName);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //Big function for a simple task, to capitalize all words in a string
//    public static String toTitleCase(String givenString) {
//        String[] arr = givenString.split(" ");
//        StringBuffer sb = new StringBuffer();
//
//        for (int i = 0; i < arr.length; i++) {
//            sb.append(Character.toUpperCase(arr[i].charAt(0)))
//                    .append(arr[i].substring(1)).append(" ");
//        }
//        return sb.toString().trim();
//    }
}
