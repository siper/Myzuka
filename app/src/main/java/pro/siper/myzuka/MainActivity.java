package pro.siper.myzuka;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MaterialSearchView.OnQueryTextListener {
    final String TAG = "MainActivity";
    ArrayList<Song> songs = new ArrayList<>();
    RecyclerView songsList;
    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        songsList = (RecyclerView) findViewById(R.id.songs_list);

        final String path = "/";
        SongsAdapter adapter = new SongsAdapter(songs, new AdapterCallbacks() {
            @Override
            public void onClick(int position) {
                Song song = songs.get(position);

                Intent intent = new Intent(MainActivity.this, FileDownloaderService.class);
                intent.putExtra(Constants.PATH, path);
                intent.putExtra(Constants.FILENAME, song.artist + " - " + song.title + ".mp3");
                intent.putExtra(Constants.URL, song.url);

                startService(intent);
            }

            @Override
            public void onLongClick(int position) {
                Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });
        songsList.setAdapter(adapter);

        songsList.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .size(1)
                        .margin(16, 16)
                        .build());

        new LoadData(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(Document doc) {
                Elements elements = doc.getElementsByAttribute("data-url");
                for(int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    songs.add(new Song(element.attr("data-title"), element.attr("data-url")));
                    songsList.getAdapter().notifyItemInserted(i);
                }
            }
        }).execute("https://myzuka.fm/");

        if(!Utils.hasPermissions(this, Constants.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView.setMenuItem(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "Submit");
        if(query.length() > 0) {
            new LoadData(new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(Document doc) {
                    songs.clear();
                    songsList.getAdapter().notifyDataSetChanged();
                    Elements tables = doc.select("tbody");
                    Element table = tables.get(tables.size() - 1);
                    Elements rows = table.select("tr");
                    for(int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements cols = row.select("td");
                        songs.add(new Song(cols.get(0).select("a").text(),
                                cols.get(1).select("a").text(), cols.get(1).select("a").attr("href")));
                        songsList.getAdapter().notifyItemInserted(i);
                    }
                }
            }).execute("https://myzuka.fm/Search?searchText=" + query);
        } else {
            new LoadData(new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(Document doc) {
                    Elements elements = doc.getElementsByAttribute("data-url");
                    songs.clear();
                    for(int i = 0; i < elements.size(); i++) {
                        Element element = elements.get(i);
                        songs.add(new Song(element.attr("data-title"), element.attr("data-url")));
                        songsList.getAdapter().notifyItemInserted(i);
                    }
                }
            }).execute("https://myzuka.fm/");
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "Change");
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            Toast.makeText(this, "Права предоставлены", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Права не предоставлены", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNoResults() {
        //
    }

    private void hideNoresults() {
        //
    }
}
