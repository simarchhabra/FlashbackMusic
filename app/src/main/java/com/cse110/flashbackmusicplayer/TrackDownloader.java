package com.cse110.flashbackmusicplayer;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;


public class TrackDownloader extends AsyncTask<String, Void, String> {

    private Activity activity;
    private DownloadManager manager;

    public TrackDownloader(Activity activity) {
        this.activity = activity;
        manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        activity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected String doInBackground(String... params) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(params[0]));
        manager.enqueue(request);

        return null;
    }


    private BroadcastReceiver receiver = new BroadcastReceiver(){
        // https://stackoverflow.com/questions/13321984/android-downloadmanager-get-filename
        // How to set up a bundle of extras to get the name of the downloaded song.
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            Bundle extras = intent.getExtras();
            query.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor cursor = manager.query(query);
            if (cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                String uris = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    Toast.makeText(activity, "Finished downloading " + title, Toast.LENGTH_SHORT).show();
                    activity.unregisterReceiver(receiver);
                }
                if (status==DownloadManager.STATUS_FAILED) {
                    Uri uri = Uri.parse(uris);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    manager.enqueue(request);
                }

            }
        }
    };
}
