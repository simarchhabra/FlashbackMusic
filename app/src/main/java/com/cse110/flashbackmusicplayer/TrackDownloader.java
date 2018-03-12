package com.cse110.flashbackmusicplayer;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;


public class TrackDownloader extends AsyncTask<String, Void, Long> {

    private DownloadManager manager;

    public TrackDownloader(DownloadManager manager) {
        this.manager = manager;

    }

    @Override
    protected Long doInBackground(String... params) {
        // Get the name of the file we are downloading using HTTP as described here:
        // https://stackoverflow.com/questions/33886576/using-android-downloadmanager-how-do-i-get-file-name
        HttpURLConnection con = null;
        String filename = "unknown-file";
        String extension = ".unknown";
        try {
            con = (HttpURLConnection) (new URL(params[0])).openConnection();
            String content = con.getHeaderField("Content-Disposition");
            String contentSplit[] = content.split(";");

            // Search for the content field that has the filename.
            for (String field : contentSplit) {
                if (field.contains("filename=")) {
                    filename = field.replace("filename=","").replace("\"", "").replace(";", "").trim();
                    extension = filename.substring(filename.lastIndexOf('.') + 1);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if this file already exists, and if it does, delete it.
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS  + File.separator + filename);
        if(file.exists()) {
            boolean result = file.delete();
            result = true;
        }

        Uri uri = Uri.parse(params[0]);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        request.setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);



        return manager.enqueue(request);
    }
}
