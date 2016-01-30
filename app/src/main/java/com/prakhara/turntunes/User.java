package com.prakhara.turntunes;

public class User {

    private boolean host;
    private String hostKey;

    public User() {
        host = false;
        hostKey = null;
    }

    public User(boolean isHost) {
        if (isHost) {
            host = true;
        } else {
            host = false;
            hostKey = null;
        }
    }

    public boolean getRole() {
        return host;
    }

    public boolean isHost (String key) {
        if (hostKey.equals(key))
            return true;
        return false;
    }

}
