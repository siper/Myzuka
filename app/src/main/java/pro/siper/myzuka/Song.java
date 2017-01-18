package pro.siper.myzuka;

import android.util.Log;

/**
 * Created by siper on 17.01.2017.
 */

public class Song {
    String title;
    String artist;
    String url;

    public Song(String data, String url) {
        this.url = "http://myzuka.fm" + url.replace("Play", "Download");
        String[] raw = data.split(" - ");
        this.artist = raw[0];
        this.title = raw[1];
    }

    public Song(String artist, String title, String url) {
        this.url = "http://myzuka.fm" + url;
        this.artist = artist;
        this.title = title;
    }
}
