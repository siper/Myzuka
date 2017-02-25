package pro.siper.myzuka;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siper on 29.01.2017.
 */

public class MainModel implements MusicCallback, SuggestionsCallback {
    private ArrayList<Song> mDataset;
    private DataChangesListener listener;
    private LoadData dataLoader;
    private LoadSuggestions suggestionsLoader;
    private boolean isSearching = false;

    public static final String TAG = "MainModel";

    public MainModel(DataChangesListener listener) {
        this.listener = listener;
        this.dataLoader = new LoadData(this);
        this.suggestionsLoader = new LoadSuggestions(this);
        this.mDataset = new ArrayList<>();
    }

    public void loadData(String url) {
        if(dataLoader != null) {
            dataLoader = new LoadData(this);
            dataLoader.execute(url);
        }
    }

    public void loadSuggestions(String url) {
        cancelLoadingSuggestions();
        if(suggestionsLoader != null) {
            suggestionsLoader = new LoadSuggestions(this);
            suggestionsLoader.execute(url);
        }
    }

    public void search(String query) {
        if(dataLoader != null && !query.isEmpty() && query.length() > 2) {
            cancelLoadingData();
            isSearching = true;
            dataLoader = new LoadData(this);
            dataLoader.execute("https://myzuka.fm/Search?searchText=" + query);
        }
    }

    public void cancelLoadingData() {
        if(dataLoader != null) {
            dataLoader.cancel(true);
            isSearching = false;
        }
    }

    public void cancelLoadingSuggestions() {
        if(suggestionsLoader != null) {
            suggestionsLoader.cancel(true);
            isSearching = false;
        }
    }


    public ArrayList<Song> getSongs() {
        return mDataset;
    }

    @Override
    public void onSuggestionsLoaded(List<MusicSuggestion> suggestions) {
        if(suggestions != null) {
            listener.onSuggestionsChanged(suggestions);
        } else {
            listener.noSuggestions();
        }
    }

    @Override
    public void onMusicLoaded(Document doc) {
        mDataset.clear();
        if(doc != null) {
            if(isSearching) {
                try {
                    Elements tables = doc.select("tbody");
                    Element table = tables.get(tables.size() - 1);
                    Elements rows = table.select("tr");
                    for(int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements cols = row.select("td");
                        mDataset.add(new Song(cols.get(0).select("a").text(),
                                cols.get(1).select("a").text(), cols.get(1).select("a").attr("href")));
                    }
                } catch (Exception e) {
                    if(BuildConfig.DEBUG) {
                        Log.d(TAG, "Dataset cleared");
                    }
                }

                isSearching = false;
            } else {
                Elements elements = doc.getElementsByAttribute("data-url");
                for(int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    mDataset.add(new Song(element.attr("data-title"), element.attr("data-url")));
                }
            }
        }
        listener.onDatasetChanged(mDataset);
    }
}
