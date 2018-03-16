package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;

/**
 * Created by Eldon on 3/15/2018.
 */

public class ArtistSort implements SortStrategy{
    public ArrayList<Song> sort(ArrayList<Song> input) {
        ArrayList<Song> artSorted = input;
        for(int i = 1; i < artSorted.size(); i++) {
            String val = artSorted.get(i).getAlbum();
            int j = i - 1;
            while( j > -1 && artSorted.get(j).getArtist().compareTo(val) > 0) {
                artSorted.set(j + 1, artSorted.get(j));
                j--;
            }
            artSorted.set(j+1, artSorted.get(i));
        }
        return artSorted;
    }
}
