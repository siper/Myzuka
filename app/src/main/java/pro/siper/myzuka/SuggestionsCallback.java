package pro.siper.myzuka;

import java.util.List;

/**
 * Created by siper on 25.02.2017.
 */

public interface SuggestionsCallback {
    void onSuggestionsLoaded(List<MusicSuggestion> suggestions);
}
