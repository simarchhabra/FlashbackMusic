package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;


public class ArtistSort implements SortStrategy{
    public ArrayList<String> sort(List<String> input) {
        ArrayList<String> artSorted = new ArrayList<>(input);
        for(int i = 1; i < artSorted.size(); i++) {
            Song song = songDB.get(artSorted.get(i));
            String val = song.getAlbum();
            int j = i - 1;
            while( j > -1 && song.getArtist().compareTo(val) > 0) {
                artSorted.set(j + 1, artSorted.get(j));
                j--;
            }
            artSorted.set(j+1, artSorted.get(i));
        }
        return artSorted;
    }
}
