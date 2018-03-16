package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;

/**
 * Created by Eldon on 3/15/2018.
 */

public class AlbumSort implements SortStrategy {
    public ArrayList<Song> sort(ArrayList<Song> input) {
        ArrayList<Song> albumSorted = input;
        //insertsort algorithm
        for(int i = 1; i < albumSorted.size(); i++) {
            String val = albumSorted.get(i).getAlbum();
            int j = i - 1;
            while( j > -1 && albumSorted.get(j).getAlbum().compareTo(val) > 0) {
                albumSorted.set(j + 1, albumSorted.get(j));
                j--;
            }
            albumSorted.set(j+1, albumSorted.get(i));
        }
        return albumSorted;
    }
}
