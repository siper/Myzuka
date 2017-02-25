package pro.siper.myzuka;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by siper on 25.02.2017.
 */

public class LoadSuggestions extends AsyncTask<String, String, List<MusicSuggestion>> {
    final String TAG = "GetSuggestions";

    private SuggestionsCallback listener;

    public LoadSuggestions(SuggestionsCallback listener) {
        this.listener = listener;
    }

    @Override
    protected List<MusicSuggestion> doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            Gson gson = new Gson();
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("accept", "application/json");
            connection.addRequestProperty("referer", "https://myzuka.fm/");

            InputStream it = new BufferedInputStream(connection.getInputStream());
            InputStreamReader read = new InputStreamReader(it);
            BufferedReader buff = new BufferedReader(read);
            StringBuilder dta = new StringBuilder();
            String chunks;
            while((chunks = buff.readLine()) != null)
            {
                dta.append(chunks);
            }

            MusicSuggestionModel[] suggestionsGson = gson.fromJson(dta.toString(),
                    MusicSuggestionModel[].class);
            List<MusicSuggestion> suggestions = new ArrayList<>();
            for(MusicSuggestionModel suggestion : suggestionsGson) {
                suggestions.add(new MusicSuggestion(suggestion));
            }
            return suggestions;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(List<MusicSuggestion> suggestions) {
        listener.onSuggestionsLoaded(suggestions);
    }
}