package pro.siper.myzuka;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MvpActivity<MainView, MainPresenter>
        implements MainView, FloatingSearchView.OnSearchListener, AdapterCallbacks,
        FloatingSearchView.OnQueryChangeListener, SearchSuggestionsAdapter.OnBindSuggestionCallback {
    final String TAG = "MainActivity";

    RecyclerView songsList;
    FloatingSearchView floatingSearchView;
    SongsAdapter adapter;
    LinearLayout error;
    LinearLayout noResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsList = (RecyclerView) findViewById(R.id.songs_list);
        floatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        error = (LinearLayout) findViewById(R.id.error);
        noResults = (LinearLayout) findViewById(R.id.no_results);
        floatingSearchView.setOnSearchListener(this);
        floatingSearchView.setOnQueryChangeListener(this);
        floatingSearchView.setOnBindSuggestionCallback(this);
        floatingSearchView.showProgress();

        adapter = new SongsAdapter(this);
        songsList.setAdapter(adapter);

        songsList.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .size(1)
                        .margin(16, 16)
                        .build());

        if(!Utils.hasPermissions(this, Constants.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, 1);
        }
    }

    @Override
    public void onSearchAction(String currentQuery) {
        floatingSearchView.showProgress();
        presenter.search(currentQuery);
    }

    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        MusicSuggestion suggestion = (MusicSuggestion) searchSuggestion;
        switch (suggestion.getIcon()) {
            case R.drawable.ic_audiotrack_black_24dp:
                presenter.downloadSong(suggestion);
                break;
            default:
                presenter.search(suggestion.getBody());
                floatingSearchView.showProgress();
        }
        floatingSearchView.clearSuggestions();
    }

    @Override
    public void onSearchTextChanged(String oldQuery, String newQuery) {
        if(TextUtils.isEmpty(newQuery) || newQuery.length() < 2) {
            floatingSearchView.clearSuggestions();
            floatingSearchView.hideProgress();
        } else {
            floatingSearchView.showProgress();
            presenter.loadSuggestions(newQuery);
        }
    }

    @Override
    public void showSuggestions(List<MusicSuggestion> suggestions) {
        floatingSearchView.swapSuggestions(suggestions);
        floatingSearchView.hideProgress();
    }

    @Override
    public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView,
                                 SearchSuggestion item, int itemPosition) {
        MusicSuggestion musicSuggestion = (MusicSuggestion) item;
        textView.setText(musicSuggestion.getBody());
        leftIcon.setImageResource(musicSuggestion.getIcon());
        leftIcon.setAlpha((float) 0.54);
    }

    @Override
    public void onClick(int position) {
        presenter.downloadSong(position);
    }

    @Override
    public void onLongClick(int position) {

    }

    @Override
    public void showErrorMessage() {
        showError();
        floatingSearchView.hideProgress();
    }

    @Override
    public void showNoResultsMessage() {
        showNoResults();
        floatingSearchView.hideProgress();
    }

    @Override
    public void showSongsList(ArrayList<Song> songs) {
        hideAllErrors();
        adapter.setSongs(songs);
        floatingSearchView.hideProgress();
    }

    @Override
    public void hideProgress() {
        floatingSearchView.hideProgress();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            hideError();
            presenter.loadSongs();
        } else {
            showError();
        }
    }

    private void showError() {
        hideNoResults();
        songsList.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        songsList.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
    }

    private void showNoResults() {
        hideError();
        songsList.setVisibility(View.GONE);
        noResults.setVisibility(View.VISIBLE);
    }

    private void hideNoResults() {
        songsList.setVisibility(View.VISIBLE);
        noResults.setVisibility(View.GONE);
    }

    private void hideAllErrors() {
        noResults.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
    }
}
