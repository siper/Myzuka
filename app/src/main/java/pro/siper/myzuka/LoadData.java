package pro.siper.myzuka;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class LoadData extends AsyncTask<String, String, Document> {
    final String TAG = "GetData";

    private OnTaskCompleted listener;

    public LoadData(OnTaskCompleted listner) {
        this.listener = listner;
    }

    @Override
    protected Document doInBackground(String... strings) {
        try {
            Document doc = Jsoup.connect(strings[0]).header("Upgrade-Insecure-Requests", "1").userAgent(Constants.USER_AGENT).get();
            return doc;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Document doc) {
        listener.onTaskCompleted(doc);
    }
}
