package pro.siper.myzuka;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by siper on 17.01.2017.
 */

public class DownloadFile extends AsyncTask<String, String, File> {
    DownloadCallbacks callbacks;
    String path;
    String filename;

    public DownloadFile(String path, String filename, DownloadCallbacks callbacks) {
        this.callbacks = callbacks;
        this.path = path;
        this.filename = filename;
    }

    @Override
    protected File doInBackground(String... strings) {
        int count;
        try {
            URL url = new URL(strings[0]);
            URLConnection conection = url.openConnection();
            conection.connect();

            long total = conection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            OutputStream output = new FileOutputStream(
                    Environment.getExternalStorageDirectory().toString() + path + filename);

            byte data[] = new byte[1024];

            long downloaded = 0;

            while ((count = input.read(data)) != -1) {
                downloaded += count;

                callbacks.onProgressUpdated((int)((total * 100) / total), total, downloaded);

                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            callbacks.onDownloadFailed(e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        callbacks.onDownloadCompleted(file);
    }
}
