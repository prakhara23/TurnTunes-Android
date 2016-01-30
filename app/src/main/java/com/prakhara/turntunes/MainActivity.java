package com.prakhara.turntunes;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void joinParty(String party) {
        Intent intent = new Intent(this, PartyRoom.class);
        startActivity(intent);
    }

    public void requestParty(View v) {
        FragmentManager fm = getFragmentManager();
        JoinPartyDialog dialog = new JoinPartyDialog();
        dialog.show(fm, "Dialog");
    }
}
