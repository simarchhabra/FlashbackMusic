package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;

/**
 * Created by Eldon on 3/15/2018.
 */

public interface SortStrategy {
    public ArrayList<Song> sort(ArrayList<Song> input);
}
