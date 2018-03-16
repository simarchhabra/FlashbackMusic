package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;


public class AlbumSort implements SortStrategy {
    public ArrayList<String> sort(List<String> input) {
        ArrayList<String> albumSorted = new ArrayList<>(input);
        //insertsort algorithm
        for(int i = 1; i < albumSorted.size(); i++) {
            Song song = songDB.get(albumSorted.get(i));
            String val = song.getAlbum();
            int j = i - 1;
            while( j > -1 && song.getAlbum().compareTo(val) > 0) {
                albumSorted.set(j + 1, albumSorted.get(j));
                j--;
            }
            albumSorted.set(j+1, albumSorted.get(i));
        }
        return albumSorted;
    }
}
