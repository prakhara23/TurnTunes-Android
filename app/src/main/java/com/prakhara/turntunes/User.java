package com.prakhara.turntunes;

public class User {

    private boolean host;   // Determines if user is host or a guest.
    private int hostKey;    // TODO: implement the ability for host to still be host if they leave the room, or make sub-class HOST
    private String party;   // Name of the party they are part of.

    public User() {
        host = false;
        hostKey = 0;
    }

    public User(boolean isHost, String ownParty) {
        if (isHost) {
            host = true;
            hostKey = ownParty.hashCode();
        } else {
            host = false;
            hostKey = 0;
        }
        party = ownParty;
    }

    public boolean getRole() {
        return host;
    }

    public boolean isHost(int key) {
        return hostKey == key;
    }

}
