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
import static com.cse110.flashbackmusicplayer.MainActivity.userState;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener {
    // different Action Strings possible for service
    private static final String ACTION_START = "START";
    private static final String ACTION_PLAY = "PLAY";
    private static final String ACTION_PAUSE = "PAUSE";
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
                song = findSong(intent);
                player = new MediaPlayer();
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
            }
            // Pause player.
            else if (intent.getAction().equals(ACTION_PAUSE)) {
                player.pause();
            }
            // Resume player.
            else if (intent.getAction().equals(ACTION_PLAY)){
                player.start();
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Notify the caller of this service when it is complete.
        player.setOnCompletionListener(mediaPlayer -> sendBroadcast(finished));

        // Remove any pending posts in queue.
        handler.removeCallbacks(sendName);
        // Post to thread.
        handler.post(sendName);

        return START_STICKY;
    }

    // create runnable object to broadcast name of song being played
    private Runnable sendName = new Runnable() {
        @Override
        public void run() {
            if (song != null) {
                sendSong.putExtra("NAME", song.getTitle());
                sendSong.putExtra("PAUSED", !player.isPlaying() && player.getCurrentPosition() > 1);
                sendBroadcast(sendSong);
            }
        }
    };
    
    private Song findSong(Intent intent) {
        String name = intent.getExtras().getString("NAME");
        return songDB.get(name);
    }

    public void onPrepared(MediaPlayer player) {
        song.startedPlaying(userState);
        player.start();
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
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
        }
    }
}
