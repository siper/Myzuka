package pro.siper.myzuka;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class LoadData extends AsyncTask<String, String, Document> {
    final String TAG = "GetData";

    private MusicCallback listener;

    public LoadData(MusicCallback listener) {
        this.listener = listener;
    }

    @Override
    protected Document doInBackground(String... strings) {
        try {
            return Jsoup.connect(strings[0])
                    .header("Upgrade-Insecure-Requests", "1")
                    .userAgent(Constants.USER_AGENT).get();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Document doc) {
        listener.onMusicLoaded(doc);
    }
}
