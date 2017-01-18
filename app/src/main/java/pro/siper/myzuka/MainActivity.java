package pro.siper.myzuka;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView songsList = (RecyclerView) findViewById(R.id.songs_list);
        final ArrayList<Song> songs = new ArrayList<>();

        SongsAdapter adapter = new SongsAdapter(songs, new AdapterCallbacks() {
            @Override
            public void onClick(int position) {
                Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
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
        }).execute("https://myzuka.fm/Hits/Top100Weekly");

        if(!Utils.hasPermissions(this, Constants.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, 1);
        } else {
            Toast.makeText(this, "Нет прав на запись данных на карту памяти", Toast.LENGTH_SHORT).show();
        }
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
}
