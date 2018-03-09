package com.cse110.flashbackmusicplayer;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Amritansh on 3/8/2018.
 */

public class FirebaseManager implements DBObserver {

    public FirebaseManager () {

    }

    @Override
    public void update(Song song) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myFirebaseRef = database.getReference();

        HashMap<String, String> map = new HashMap<String, String>();

        map.put("Title", song.getTitle());
        map.put("Artist", song.getArtist());
        map.put("Album", song.getAlbum());
        map.put("Track Number", song.getTrackNumber());
        //map.put("Locations", song.getLocations().toString());

        myFirebaseRef.child(song.getTitle()).setValue(map);

    }

    public void search(String title) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myFirebaseRef = database.getReference();

        Query queryRef = myFirebaseRef.orderByChild("filename").equalTo(title);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
//                if (snapshot == null || snapshot.getValue() == null) {
//                    //Toast.makeText(MainActivity.this, "No record found", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    //Toast.makeText(MainActivity.this, snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Faile to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });

    }
}
