package com.cse110.flashbackmusicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.cse110.flashbackmusicplayer.MainActivity.musicSystem;
import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class TrackDisplayActivity extends AppCompatActivity {

    SeekBar sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_track_display);

        // Get the name of the song we are playing.
        String name = getIntent().getExtras().getString("NAME");
        Song song = songDB.get(name);

        // Draw the song metadata on the screen.
        displayTrack(song);
        displayHistory(song);

        // Pause or play the song.
        final Button pauseButton = (Button) findViewById(R.id.pauseButton);
        if(musicSystem.isPaused()) {
            pauseButton.setBackgroundResource(R.drawable.playwhite);
        }
        else{
            pauseButton.setBackgroundResource(R.drawable.pausewhite);
        }
        pauseButton.setOnClickListener( view -> {
                musicSystem.togglePause();
                if(musicSystem.isPaused()) {
                    pauseButton.setBackgroundResource(R.drawable.pausewhite);
                }
                else{
                    pauseButton.setBackgroundResource(R.drawable.playwhite);
                }
            }
        );

        // Go back to the selection screen.
        final Button switchScreen = (Button) findViewById(R.id.backButton);
        switchScreen.setOnClickListener(view -> finish());
    }

    private void displayHistory(Song song){
        TextView songHistory = (TextView) findViewById(R.id.history);

        // Get the formatted strings describing when the track was last played.
        String place = song.getPlace();
        String time = song.getTime();
        String date = song.getDate();

        // If any of these do not exist, then the track is being played for the first time.
        if (time == null || date == null) {
            // Don't write anything.
            songHistory.setText("");
        }
        else if (place == null) {
            String songTitleStr= "Last Played: "+song.getTime()+", " + song.getDate();
            songHistory.setText(songTitleStr);
        }
        else {
            String songTitleStr= "Last Played: "+ song.getPlace()+ "\n"+song.getTime()+", " + song.getDate();
            songHistory.setText(songTitleStr);
        }
    }

    private void displayTrack(Song song) {
        // Access and display title, artist metadata
        TextView songTitle = (TextView) findViewById(R.id.songTitle);
        String songTitleStr= song.getTitle();
        if(song.getArtist()!=null)
        {
            songTitleStr = songTitleStr+ "\n"+ song.getArtist();
        }
        songTitle.setText(songTitleStr);

        // access and display album, track number metadata
        TextView songAlbum = (TextView) findViewById(R.id.songAlbum);
        String songAlbumStr = song.getAlbum() + "\nTrack #: " + song.getTrackNumber();
        songAlbum.setText(songAlbumStr);

        // create image for album cover
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
            fav_dislike.setBackgroundResource(R.drawable.favouritewhite);
        }
        else if (!song.isFavorited() && song.isDisliked()) {
            // Change the image of the button.
            fav_dislike.setBackgroundResource(R.drawable.dislikewhite);
        }
        else if (!song.isDisliked() && !song.isFavorited()) {
            // Change the image of the button.
            fav_dislike.setBackgroundResource(R.drawable.neutralwhite);
        }
        fav_dislike.setOnClickListener(view -> {
                // If it's neutral, favorite.
                if (!song.isFavorited() && !song.isDisliked()) {
                    // Transition to the new state.
                    song.setFavorited(true);
                    song.setDisliked(false);
                    // Change the image of the button.
                    fav_dislike.setBackgroundResource(R.drawable.favouritewhite);
                }
                // Else if it's favorited, dislike.
                else if (song.isFavorited() && !song.isDisliked()) {
                    // Transition to the new state.
                    song.setFavorited(false);
                    song.setDisliked(true);
                    // Change the image of the button.
                    fav_dislike.setBackgroundResource(R.drawable.dislikewhite);
                    // We have to skip this song because it is disliked.
                    musicSystem.skipTrack();
                    // Finish this activity and return us back to the main screen.
                    finish();
                }
                // Else if it's disliked, return it to neutral.
                else if (song.isDisliked() && !song.isFavorited()) {
                    // Transition to the new state.
                    song.setFavorited(false);
                    song.setDisliked(false);
                    // Change the image of the button.
                    fav_dislike.setBackgroundResource(R.drawable.neutralwhite);
                    // Play the song.
                    musicSystem.playTrack(song.getTitle());
                }
                // This should never happen, unless we set the states wrong.
                else {
                    throw new IllegalStateException("Cannot have song both favorited and disliked");
                }
        });
    }

}
