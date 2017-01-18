package pro.siper.myzuka;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by siper on 17.01.2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {
    private ArrayList<Song> songs;
    private AdapterCallbacks callbacks;

    public SongsAdapter(ArrayList<Song> songs, AdapterCallbacks callbacks) {
        this.songs = songs;
        this.callbacks = callbacks;
    }

    public Song getItem(int position) {
        if(songs != null) {
            return songs.get(position);
        }
        return null;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(v, callbacks);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Song song = getItem(position);

        holder.titleTextView.setText(song.title);
        holder.artistTextView.setText(song.artist);
    }

    @Override
    public int getItemCount() {
        if(songs != null) {
            return songs.size();
        }
        return 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        public TextView titleTextView;
        public TextView artistTextView;
        private AdapterCallbacks callbacks;

        public SongViewHolder(View itemView, AdapterCallbacks callbacks) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            artistTextView = (TextView) itemView.findViewById(R.id.artist);
            this.callbacks = callbacks;
        }

        @Override
        public void onClick(View view) {
            callbacks.onClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            callbacks.onLongClick(getAdapterPosition());
            return true;
        }
    }
}
