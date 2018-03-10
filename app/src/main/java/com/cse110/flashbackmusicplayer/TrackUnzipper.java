package com.cse110.flashbackmusicplayer;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Moi on 3/10/2018.
 */

class TrackUnzipper extends AsyncTask<String, Void, String> {

    private final AsyncTask<String, Void, String> asynctask;

    public TrackUnzipper(AsyncTask<String, Void, String> task) {
        this.asynctask = task;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String urlString = params[0];
            URL url = new URL(urlString);
            File file = new File(Environment.getExternalStorageDirectory() + "/" + urlString);

            // Open a connection to the URL with the tracks zip file.
            URLConnection urlConnection = url.openConnection();

            // Get a stream of the bytes from the URL.
            InputStream inputStream = urlConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            // Write the bytes to the file.
            FileOutputStream output = new FileOutputStream(file);
            int i = 0;
            while ((i = bufferedInputStream.read()) != -1) {
                output.write(i);
            }

            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return params[0];
    }

    @Override
    protected void onPostExecute(String result) {
        asynctask.execute(result);
    }
}
