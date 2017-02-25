package pro.siper.myzuka;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siper on 29.01.2017.
 */

public interface MainView extends MvpView {
    void showSongsList(ArrayList<Song> songs);
    void showSuggestions(List<MusicSuggestion> suggestions);
    void showErrorMessage();
    void showNoResultsMessage();
    void hideProgress();
}
