package pro.siper.myzuka;

import android.content.Context;
import android.content.Intent;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siper on 29.01.2017.
 */

public class MainPresenter extends MvpBasePresenter<MainView> implements DataChangesListener {
    private MainModel model;
    private Context context;

    public MainPresenter(Context context) {
        this.model = new MainModel(this);
        this.context = context;
        loadSongs();
    }

    public void loadSongs() {
        if(Utils.isOnline(context) && Utils.hasPermissions(context, Constants.PERMISSIONS)) {
            model.loadData("https://myzuka.fm");
        } else {
            if(isViewAttached()) {
                getView().showErrorMessage();
            }
        }
    }

    public void loadSuggestions(String query) {
        if(Utils.isOnline(context) && Utils.hasPermissions(context, Constants.PERMISSIONS)) {
            model.loadSuggestions("https://myzuka.fm/Search/Suggestions?term=" + query);
        } else {
            if(isViewAttached()) {
                getView().showErrorMessage();
            }
        }
    }

    public void search(String query) {
        if(model != null) {
            if(Utils.isOnline(context)) {
                model.search(query);
            } else {
                if(isViewAttached()) {
                    getView().showErrorMessage();
                }
            }
        }
    }

    public void downloadSong(int position) {
        Song song = model.getSongs().get(position);

        Intent intent = new Intent(context, FileDownloaderService.class);
        intent.putExtra(Constants.PATH, "/");
        intent.putExtra(Constants.FILENAME, song.artist + " - " + song.title + ".mp3");
        intent.putExtra(Constants.URL, song.url);

        context.startService(intent);
    }

    public void downloadSong(MusicSuggestion song) {
        Intent intent = new Intent(context, FileDownloaderService.class);
        intent.putExtra(Constants.PATH, "/");
        intent.putExtra(Constants.FILENAME, song.getBody() + ".mp3");
        intent.putExtra(Constants.URL, "http://myzuka.fm" + song.getUrl());

        context.startService(intent);
    }

    @Override
    public void onSuggestionsChanged(List<MusicSuggestion> suggestions) {
        if(isViewAttached()) {
            getView().showSuggestions(suggestions);
        }
    }

    @Override
    public void noSuggestions() {
        if(isViewAttached()) {
            getView().hideProgress();
        }
    }

    @Override
    public void onDatasetChanged(ArrayList<Song> mDataset) {
        if(mDataset.isEmpty()) {
            if(isViewAttached()) {
                getView().showNoResultsMessage();
            }
        } else {
            if(isViewAttached()) {
                getView().showSongsList(mDataset);
            }
        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if(!retainInstance) {
            if(model != null) {
                model.cancelLoadingData();
                model.cancelLoadingSuggestions();
            }
        }
    }
}
