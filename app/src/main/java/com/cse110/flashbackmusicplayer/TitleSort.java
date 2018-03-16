package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;

/**
 * Created by Eldon on 3/15/2018.
 */

public class TitleSort implements SortStrategy {
    public ArrayList<Song> sort(ArrayList<Song> input) {
        ArrayList<Song> titleSorted = input;
        for(int i = 1; i < titleSorted.size(); i++) {
            String val = titleSorted.get(i).getTitle();
            int j = i - 1;
            while( j > -1 && titleSorted.get(j).getTitle().compareTo(val) > 0) {
                titleSorted.set(j + 1, titleSorted.get(j));
                j--;
            }
            titleSorted.set(j+1, titleSorted.get(i));
        }
        return titleSorted;
    }
}
