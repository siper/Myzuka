package pro.siper.myzuka;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siper on 29.01.2017.
 */

public interface DataChangesListener {
    void onDatasetChanged(ArrayList<Song> mDataset);
    void onSuggestionsChanged(List<MusicSuggestion> suggestions);
    void noSuggestions();
}
