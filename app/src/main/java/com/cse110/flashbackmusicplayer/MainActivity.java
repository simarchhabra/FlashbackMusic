package com.cse110.flashbackmusicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // we should eliminate this, use vv
    // String selectedFromList = (lv.getItemAtPosition(position));
    List<String> songsList;

    ListView songsView;

    ListAdapter adapter;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsView = (ListView) findViewById(R.id.songsView);

        songsList = new ArrayList<>();

        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            songsList.add(fields[i].getName());
        }


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songsList);
        songsView.setAdapter(adapter);

        songsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                // remove first parameter call, use line below instead
                // String selectedFromList = (lv.getItemAtPosition(position));
                int resID = getResources().getIdentifier(songsList.get(i), "raw", getPackageName());

                mediaPlayer = MediaPlayer.create(MainActivity.this, resID);

                mediaPlayer.start();
            }
        });
    }

}
