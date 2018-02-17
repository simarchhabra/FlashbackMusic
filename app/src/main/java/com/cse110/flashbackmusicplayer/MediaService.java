package com.cse110.flashbackmusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.widget.SeekBar;

import java.io.IOException;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener {
    // different Action Strings possible for service
    private static final String ACTION_START = "START";
    private static final String ACTION_PLAY = "PLAY";
    private static final String ACTION_PAUSE = "PAUSE";
    private static final String ACTION_STOP = "STOP";
    // generic broadcast string
    public static final String ACTION_BROADCAST = "BROADCAST";
    public static final String ACTION_FINISHED = "FINISHED";

    // Broadcast intent
    Intent sendSong;
    Intent finished;
    // song to play
    Song song;
    // actual MediaPlayer instance
    MediaPlayer player = null;
    // Handler for sending broadcast messages
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        // Create intent
        sendSong = new Intent(ACTION_BROADCAST);
        finished = new Intent(ACTION_FINISHED);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            // Create player.
            if (intent.getAction().equals(ACTION_START)) {
                // Get the song we are playing.
                song = findSong(intent);
                // Create the media player.
                player = new MediaPlayer();
                // Record the state of the user when we started playing the song.
                UserState snapshot = UserState.snapshot();
                Uri source = Uri.parse("android.resource://" + getPackageName() + "/raw/" + song.getFilename());
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(getApplicationContext(), source);
                    // work in background (even after locking screen)
                    player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    player.setOnPreparedListener(this);
                    player.prepareAsync(); // prepare async to not block main thread
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Notify the caller of this service when it is complete.
                player.setOnCompletionListener(mediaPlayer -> {
                            sendBroadcast(finished);
                            // Record the time this song was finished.
                            song.startedPlaying(snapshot);
                        }
                );

                // Notify the Music system of the successful start.
                sendSong.putExtra("NAME", song.getTitle());
                sendSong.putExtra("PAUSED", false);
                sendBroadcast(sendSong);
            }
            // Pause player.
            else if (intent.getAction().equals(ACTION_PAUSE)) {
                player.pause();
                // Notify the Music system of the successful start.
                sendSong.putExtra("NAME", song.getTitle());
                sendSong.putExtra("PAUSED", true);
                sendBroadcast(sendSong);
            }
            // Resume player.
            else if (intent.getAction().equals(ACTION_PLAY)){
                player.start();
                // Notify the Music system of the successful start.
                sendSong.putExtra("NAME", song.getTitle());
                sendSong.putExtra("PAUSED", false);
                sendBroadcast(sendSong);
            }
            // Stop playing the current song.
            else if (intent.getAction().equals(ACTION_STOP)) {
                sendBroadcast(finished);
                player.stop();
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    // create runnable object to broadcast name of song being played
    private Runnable sendName = new Runnable() {
        @Override
        public void run() {
            if (song != null) {

            }
        }
    };
    
    private Song findSong(Intent intent) {
        String name = intent.getExtras().getString("NAME");
        return songDB.get(name);
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
        /// TODO: Get rid of below line. It is correctly written on line 71. This is for testing.
        song.startedPlaying(UserState.getInstance());
    }


    @Override
    public IBinder onBind(Intent intent) {
        // No binding necessary
        return null;
    }

    private void getDuration()
    {
        player.getDuration();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
        }
    }
}
