package com.prakhara.turntunes;

public class Song {

    private String song;
    private String img;
    private String url;
    private int song_id;

    //Empty for the purposes of Firebase
    public Song() { }

    //Getter methods
    public String getSong() {
        return song;
    }

    public String getImg() {
        return img;
    }

    public String getUrl() {
        return url;
    }

    public int getSong_id() {
        return song_id;
    }

    //Setter Methods
    public void setSong(String songName) {
        song = songName;
    }

    public void setImg(String imgPath) {
        img = imgPath;
    }

    public void setUrl(String songUrl) {
        url = songUrl;
    }

    public void setSong_id(int songId) {
        song_id = songId;
    }
}
