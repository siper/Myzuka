package pro.siper.myzuka;

/**
 * Created by siper on 17.01.2017.
 */

public class Song {
    String title;
    String artist;
    String url;

    public Song(String data, String url) {
        this.url = url.replace("Play", "Download");
        String[] raw = data.split(" - ");
        this.artist = raw[0];
        this.title = raw[1];
    }
}
