package pro.siper.myzuka;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by siper on 25.02.2017.
 */

public class MusicSuggestion implements SearchSuggestion {
    private String title;
    private String type;
    private String url;

    public MusicSuggestion(Parcel in) {
        this.title = in.readString();
        this.type = in.readString();
    }

    public MusicSuggestion(MusicSuggestionModel model) {
        this.title = model.getLabel();
        this.type = model.getCategory();
        this.url = model.getUrl();
    }

    public static final Creator<MusicSuggestion> CREATOR = new Creator<MusicSuggestion>() {
        @Override
        public MusicSuggestion createFromParcel(Parcel in) {
            return new MusicSuggestion(in);
        }

        @Override
        public MusicSuggestion[] newArray(int size) {
            return new MusicSuggestion[size];
        }
    };

    @Override
    public String getBody() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getIcon() {
        switch (type) {
            case "Исполнители":
                return R.drawable.ic_person_black_24dp;
            case "Альбомы":
                return R.drawable.ic_album_black_24dp;
            case "Тексты песен":
                return R.drawable.ic_queue_music_black_24dp;
            default:
                return R.drawable.ic_audiotrack_black_24dp;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(type);
    }
}
