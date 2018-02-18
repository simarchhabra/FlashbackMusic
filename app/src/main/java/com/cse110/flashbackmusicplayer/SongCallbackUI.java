package com.cse110.flashbackmusicplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.cse110.flashbackmusicplayer.MainActivity.musicSystem;
import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class SongCallbackUI implements SongCallback {

    private Activity activity;

    public SongCallbackUI(Activity activity) {
        this.activity = activity;

        // Display the UI on the activity screen.
        redraw();

        // Get the name of the song we are playing.
        String name = activity.getIntent().getExtras().getString("TRACK_NAME");
        Song song = songDB.get(name);

        // Change the like status button when it is clicked.
        Button fav_dislike = (Button) activity.findViewById(R.id.fav_dis_button);
        fav_dislike.setOnClickListener(view -> {
            switch (song.getLikedStatus()) {
                case NEUTRAL:
                    song.setLikedStatus(Song.LIKED_STATUS.FAVORITED);
                    break;
                case DISLIKED:
                    song.setLikedStatus(Song.LIKED_STATUS.NEUTRAL);
                    musicSystem.playTrack(song.getTitle());
                    break;
                case FAVORITED:
                    song.setLikedStatus(Song.LIKED_STATUS.DISLIKED);
                    musicSystem.skipTrack();
                    break;
            }
            setLikedStatus(song.getLikedStatus());
        });

        // Pause or play the song.
        final Button pauseButton = (Button) activity.findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener( view -> musicSystem.togglePause() );

        // Go back to the selection screen.
        final Button switchScreen = (Button) activity.findViewById(R.id.backButton);
        switchScreen.setOnClickListener(view -> activity.finish());

        // If the user moves the seekbar, update the progress of the song.
        SeekBar seekBar = (SeekBar) activity.findViewById(R.id.slideTrack);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicSystem.seekTime(seekBar.getProgress());
            }
        });
    }

    @Override
    public void setSeekbarTo(int progress, int max) {
        // Get the seekbar.
        SeekBar seekBar = (SeekBar) activity.findViewById(R.id.slideTrack);
        seekBar.setMax(0);
        seekBar.setMax(max);
        seekBar.setProgress(progress);
    }

    @Override
    public void setPaused(boolean paused) {
        Button pauseButton = (Button) activity.findViewById(R.id.pauseButton);
        if (paused) pauseButton.setBackgroundResource(R.drawable.playwhite);
        else pauseButton.setBackgroundResource(R.drawable.pausewhite);
    }

    @Override
    public void setLikedStatus(Song.LIKED_STATUS likedStatus) {
        Button fav_dislike = (Button) activity.findViewById(R.id.fav_dis_button);
        switch (likedStatus) {
            case NEUTRAL:
                fav_dislike.setBackgroundResource(R.drawable.neutralwhite);
                break;
            case DISLIKED:
                fav_dislike.setBackgroundResource(R.drawable.dislikewhite);
                break;
            case FAVORITED:
                fav_dislike.setBackgroundResource(R.drawable.favouritewhite);
                break;
        }
    }

    @Override
    public void redraw() {
        // Get the name of the song we are playing.
        String name = activity.getIntent().getExtras().getString("TRACK_NAME");
        Song song = songDB.get(name);

        TextView songHistory = (TextView) activity.findViewById(R.id.history);

        if (song != null) {

            // Get the formatted strings describing when the track was last played.
            String place = song.getPlace();
            String time = song.getTime();
            String date = song.getDate();

            // If any of these do not exist, then the track is being played for the first time.
            if (time == null || date == null) {
                // Don't write anything.
                songHistory.setText("");
            } else if (place == null) {
                String songTitleStr = "Last Played: " + song.getTime() + ", " + song.getDate();
                songHistory.setText(songTitleStr);
            } else {
                String songTitleStr = "Last Played: " + song.getPlace() + "\n" + song.getTime() + ", " + song.getDate();
                songHistory.setText(songTitleStr);
            }

            // Access and display title and artist metadata.
            TextView songTitle = (TextView) activity.findViewById(R.id.songTitle);
            String songTitleStr = song.getTitle();
            if (song.getArtist() != null) {
                songTitleStr = songTitleStr + "\n" + song.getArtist();
            }
            songTitle.setText(songTitleStr);

            // Access and display album and track number metadata
            TextView songAlbum = (TextView) activity.findViewById(R.id.songAlbum);
            String songAlbumStr = song.getAlbum() + "\nTrack #: " + song.getTrackNumber();
            songAlbum.setText(songAlbumStr);

            // Create and display the image for album cover.
            ImageView albumcover = (ImageView) activity.findViewById(R.id.album_cover);
            byte[] albumArtData = song.getAlbumCover();
            if (albumArtData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArtData, 0, albumArtData.length);
                albumcover.setImageBitmap(bitmap);
                albumcover.setAdjustViewBounds(true);
            } else {
                albumcover.setImageResource(R.drawable.nocover);
                albumcover.setAdjustViewBounds(true);
            }

            // Set the image of the like/dislike button.
            setLikedStatus(song.getLikedStatus());

            // Make the pause button a pause or a play symbol.
            setPaused(musicSystem.isPaused());
        }
        else {
            // If the song doesn't exist, just display a message.
            Toast.makeText(activity, "There are no available songs", Toast.LENGTH_LONG).show();
        }
    }
}
