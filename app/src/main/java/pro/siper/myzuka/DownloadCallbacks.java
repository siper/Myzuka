package pro.siper.myzuka;

import java.io.File;

/**
 * Created by siper on 17.01.2017.
 */

public interface DownloadCallbacks {
    void onDownloadCompleted(File file);
    void onProgressUpdated(int progress, long totalBytes, long downloadedBytes);
    void onDownloadFailed(String message);
}
