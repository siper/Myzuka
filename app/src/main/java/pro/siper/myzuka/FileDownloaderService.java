package pro.siper.myzuka;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;


public class FileDownloaderService extends IntentService {

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private int id = 666;
    String TAG = "FileDownloaderService";

    public FileDownloaderService() {
        super("FileDownloaderService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            id = NotificationId.getId();
            String filename = intent.getStringExtra(Constants.FILENAME);
            String link = intent.getStringExtra(Constants.URL);
            String path = intent.getStringExtra(Constants.PATH);

            if(!link.contains("Download")) {
                try {
                    Document doc = Jsoup.connect(link).header("Upgrade-Insecure-Requests", "1")
                            .userAgent(Constants.USER_AGENT).get();
                    Elements elements = doc.select("a[href*=/Download/]");
                    if(!elements.isEmpty()) {
                        Element elem = elements.get(0);
                        link = "http://myzuka.fm" + elem.attr("href");
                        downloadFile(link, path, filename);
                    } else {
                        notifyError();
                    }
                } catch (IOException e) {
                    notifyError();
                }
            } else {
                downloadFile(link, path, filename);
            }
        }
    }

    private void notifyError() {
        mNotificationBuilder.setContentText(getString(R.string.notification_download_error_file_deleted));
        mNotificationBuilder.setContentTitle(getString(R.string.notification_download_error));
        mNotificationManager.notify(id, mNotificationBuilder.build());
    }

    private void downloadFile(String link, String path, String filename) {
        mNotificationBuilder
                .setContentTitle(getString(R.string.notification_download_progress))
                .setContentText(filename.substring(0, filename.lastIndexOf(".")));
        mNotificationBuilder.setProgress(100, 0, true);
        startForeground(id, mNotificationBuilder.build());

        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int fileLength = connection.getContentLength();

            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(
                    Environment.getExternalStorageDirectory().toString() + path + filename);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) > 0) {
                total += count;

                mNotificationBuilder.setProgress(100, (int)((total * 100) / fileLength), false);
                mNotificationManager.notify(id, mNotificationBuilder.build());

                output.write(data, 0, count);
                output.flush();
            }

            mNotificationBuilder.setContentTitle(getString(R.string.notification_download_finished));
            output.close();
            input.close();
        } catch (IOException e) {
            mNotificationBuilder.setContentTitle(getString(R.string.notification_download_error));
        }
        mNotificationBuilder.setProgress(0, 0, false);
        stopForeground(true);
        mNotificationManager.notify(id, mNotificationBuilder.build());
    }
}
