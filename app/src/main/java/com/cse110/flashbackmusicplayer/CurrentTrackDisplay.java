package com.cse110.flashbackmusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class CurrentTrackDisplay extends AppCompatActivity {

    Button switchScreen;
    Button pauseButton;
    boolean pauseDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_track_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        switchScreen = (Button) findViewById(R.id.backButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        /* TODO: switching display to current track display
                        //launchPlayPause();
                        if(pauseDisplayed) {
                            pauseButton.setBackground(drawable.play);
                            pauseDisplayed = false;
                        }
                        else{
                            pauseButton.setBackground(drawable.pause);
                            pauseDisplayed = true;
                        } */
                    }
                }
        );


        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * TODO: Method to enable play and pause by passing intent to pause or play mediaPlayer
     * to MainActivity.java
     */
    public void launchPlayPause(){
        Intent intent2 = new Intent(this, MainActivity.class);
        startActivity(intent2);
    }

}
