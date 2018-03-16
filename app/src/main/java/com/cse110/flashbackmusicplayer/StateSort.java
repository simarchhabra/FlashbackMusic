package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;

/**
 * Created by Eldon on 3/15/2018.
 */

public class StateSort implements SortStrategy {
    public ArrayList<Song> sort(ArrayList<Song> input) {
        ArrayList<Song> stateSorted = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            //adds all the favorited songs first, in the order they appear in the database
            if (input.get(i).isFavorited()) {
                stateSorted.add(input.get(i));
            }
        }
        for (int k = 0; k < input.size(); k++) {
            //adds all the nonfavorited songs last
            if (!input.get(k).isFavorited()) {
                stateSorted.add(input.get(k));
            }
        }
        return stateSorted;
    }
}
