package com.cse110.flashbackmusicplayer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

class TrackLoader extends AsyncTask<String, Void, String> {

    private final Activity activity;

    public TrackLoader(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        String file;

        return null;
    }
}

