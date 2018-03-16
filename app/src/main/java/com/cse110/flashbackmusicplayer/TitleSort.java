package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class TitleSort implements SortStrategy {
    public ArrayList<String> sort(List<String> input) {
        ArrayList<String> titleSorted = new ArrayList<>(input);
        for(int i = 1; i < titleSorted.size(); i++) {
            Song song = songDB.get(titleSorted.get(i));
            String val = song.getTitle();
            int j = i - 1;
            while( j > -1 && song.getTitle().compareTo(val) > 0) {
                titleSorted.set(j + 1, titleSorted.get(j));
                j--;
            }
            titleSorted.set(j+1, titleSorted.get(i));
        }
        return titleSorted;
    }
}
