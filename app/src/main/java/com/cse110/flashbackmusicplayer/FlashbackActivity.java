package com.cse110.flashbackmusicplayer;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class FlashbackActivity extends AppCompatActivity {

    // In charge of handling all requests to play music.
    MusicSystem musicSystem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);

        // Create the system that will play all the music.
        musicSystem = new MusicSystem(FlashbackActivity.this);

        // Create the play list.
        songDB.generateFlashbackList();

        // Play the song with the nighest priority.
        musicSystem.playTracks(this::nextSong);

        final Button pauseButton = (Button) findViewById(R.id.pauseButton);
        if(musicSystem.isPaused()) {
            pauseButton.setBackgroundResource(R.drawable.play);
        }
        else{
            pauseButton.setBackgroundResource(R.drawable.pause);
        }
        pauseButton.setOnClickListener( view -> {
                musicSystem.togglePause();
                if(musicSystem.isPaused()) {
                    pauseButton.setBackgroundResource(R.drawable.pause);
                }
                else{
                    pauseButton.setBackgroundResource(R.drawable.play);
                }
            }
        );

        // Return back to normal mode.
        Button switchScreen = (Button) findViewById(R.id.switchBack);
        switchScreen.setOnClickListener(view -> finish());

        // Go to the next song.
        final Button skip = (Button) findViewById(R.id.nextButton);
        skip.setOnClickListener(view -> {
            musicSystem.skipTrack();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicSystem.destroy();
    }

    public Song nextSong() {
        // If there are no songs in the flashback playlist or the state of the user has changed
        // since the last time the playlist was generated, attempt to generate it again.
        if (songDB.isEmpty() || songDB.hasStateChanged()) {
            songDB.generateFlashbackList();

            // If the list is still empty, then there are no possible songs to play.
            if (songDB.isEmpty()) return null;
        }

        // Get the very first song that we will play.
        Song next = songDB.top(); songDB.pop();

        // Draw the metadata for the song.
        displaySong(next);
        displayHistory(next);

        return next;
    }

    private void displayHistory(Song song){
        TextView songHistory = (TextView) findViewById(R.id.history);

        // Get the formatted strings describing when the track was last played.
        String place = song.getPlace();
        String time = song.getTime();
        String date = song.getDate();

        // If any of these do not exist, then the track is being played for the first time.
        if (place == null || time == null || date == null) {
            // Don't write anything.
            songHistory.setText("");
        }
        else {
            String songTitleStr= "Last Played: "+ song.getPlace()+ "\n"+song.getTime()+", " + song.getDate();
            songHistory.setText(songTitleStr);
        }
    }

    private void displaySong(Song song) {
        // Access and display title, artist metadata
        TextView songTitle = (TextView) findViewById(R.id.songTitle);
        String songTitleStr= song.getTitle();
        if(song.getArtist()!=null)
        {
            songTitleStr = songTitleStr+ "\n"+ song.getArtist();
        }
        songTitle.setText(songTitleStr);

        // Access and display album, track number metadata
        TextView songAlbum = (TextView) findViewById(R.id.songAlbum);
        String songAlbumStr = song.getAlbum() + "\nTrack #: " + song.getTrackNumber();
        songAlbum.setText(songAlbumStr);

        // Create image for album cover
        ImageView albumcover = (ImageView) findViewById(R.id.album_cover);
        byte[] albumArtData = song.getAlbumCover();
        if (albumArtData != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumArtData, 0, albumArtData.length);
            albumcover.setImageBitmap(bitmap);
            albumcover.setAdjustViewBounds(true);
        }
        else {
            albumcover.setImageResource(R.drawable.nocover);
            albumcover.setAdjustViewBounds(true);
        }

        // Set the image of the like/dislike button.
        Button fav_dislike = (Button) findViewById(R.id.fav_dis_button);
        if (song.isFavorited() && !song.isDisliked()) {
            // Change the image of the button.
            fav_dislike.setBackgroundResource(R.drawable.favourite);
        }
        else if (!song.isFavorited() && song.isDisliked()) {
            // Change the image of the button.
            fav_dislike.setBackgroundResource(R.drawable.dislike);
        }
        else if (!song.isDisliked() && !song.isFavorited()) {
            // Change the image of the button.
            fav_dislike.setBackgroundResource(R.drawable.neutral);
        }
        fav_dislike.setOnClickListener(view -> {
            // If it's neutral, favorite.
            if (!song.isFavorited() && !song.isDisliked()) {
                // Transition to the new state.
                song.setFavorited(true);
                song.setDisliked(false);
                // Change the image of the button.
                fav_dislike.setBackgroundResource(R.drawable.favourite);
            }
            // Else if it's favorited, dislike.
            else if (song.isFavorited() && !song.isDisliked()) {
                // Transition to the new state.
                song.setFavorited(false);
                song.setDisliked(true);
                // Change the image of the button.
                fav_dislike.setBackgroundResource(R.drawable.dislike);
                // We have to skip this song because it is disliked.
                musicSystem.skipTrack();
            }
            // Else if it's disliked, return it to neutral.
            else if (song.isDisliked() && !song.isFavorited()) {
                // Transition to the new state.
                song.setFavorited(false);
                song.setDisliked(false);
                // Change the image of the button.
                fav_dislike.setBackgroundResource(R.drawable.neutral);
            }
            // This should never happen, unless we set the states wrong.
            else {
                throw new IllegalStateException("Cannot have song both favorited and disliked");
            }
        });
    }

}
