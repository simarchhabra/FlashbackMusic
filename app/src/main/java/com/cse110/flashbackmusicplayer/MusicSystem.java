package com.cse110.flashbackmusicplayer;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.concurrent.Callable;


public class MusicSystem {

    private Activity root;
    private String currSong = null;
    private boolean isPaused = false;

    // Classes to receiver messages from the service.
    BroadcastReceiver broadcastReceiver;
    BroadcastReceiver onCompletionListener;


    public MusicSystem(Activity root) {
        this.root = root;
    }

    public void playTrack(String toPlay) {
        // create an intent for MediaService
        Intent serviceIntent = new Intent(root, MediaService.class);
        // Create a receiver to store the name of the current song being played
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                currSong = intent.getStringExtra("NAME");
                isPaused = intent.getBooleanExtra("PAUSED", false);
            }
        };
        root.registerReceiver(broadcastReceiver, new IntentFilter(MediaService.ACTION_BROADCAST));

        // If song is already playing and a different one chosen, stop playback and create
        // new service for playback
        ServicePlaybackState playbackState = checkSongPlaying(currSong, toPlay);
        if (playbackState == ServicePlaybackState.DIFF_SONG) {
            root.unregisterReceiver(broadcastReceiver);
            root.stopService(serviceIntent);
            serviceIntent = new Intent(root, MediaService.class);
            serviceIntent.setAction("START");
            serviceIntent.putExtra("NAME", toPlay);
            root.startService(serviceIntent);
            isPaused = false;
        }
        // if no song is currently being played, start new service for playback
        else if (playbackState == ServicePlaybackState.NO_SONG){
            serviceIntent = new Intent(root, MediaService.class);
            serviceIntent.setAction("START");
            serviceIntent.putExtra("NAME", toPlay);
            root.startService(serviceIntent);
            isPaused = false;
        }
    }


    public void playTracks(Callable<Song> func) {
        try {
            // Get the song we want to play.
            Song song = func.call();

            // Play the song.
            if (song != null) playTrack(song.getTitle());
            else return;

            // Record when the song is done playing.
            onCompletionListener = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Get the song we want to play.
                    Song song = null;
                    try {
                        // Play the song.
                        song = func.call();
                        if (song != null) playTrack(song.getTitle());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            root.registerReceiver(onCompletionListener, new IntentFilter(MediaService.ACTION_FINISHED));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Enum for possible states of song playback
    private enum ServicePlaybackState{
        NO_SONG, SAME_SONG, DIFF_SONG
    };

    /**
     * Determines if the user-clicked song is the same as the one already playing or if no
     * song is currently playing
     */
    private ServicePlaybackState checkSongPlaying(String playing, String toPlay) {
        Log.d("Check", "Goes in function checkSongPlaying");
        if (playing != null) {
            Log.d("Access", "Knows current Song playing");
            if (playing.equals(toPlay)) {
                return ServicePlaybackState.SAME_SONG;
            }
            return ServicePlaybackState.DIFF_SONG;
        }
        Log.d("Access", "Does not know current song playing");
        return ServicePlaybackState.NO_SONG;
    }

    /**
     * Method to enable play and pause
     */
    public void togglePause(){
        Intent serviceIntent = new Intent(root, MediaService.class);
        if (isPaused) {
            serviceIntent.setAction("PLAY");
        }
        else {
            serviceIntent.setAction("PAUSE");
        }
        root.startService(serviceIntent);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void destroy() {
        Intent serviceIntent = new Intent(root, MediaService.class);
        root.unregisterReceiver(onCompletionListener);
        root.unregisterReceiver(broadcastReceiver);
        root.stopService(serviceIntent);
    }

}
