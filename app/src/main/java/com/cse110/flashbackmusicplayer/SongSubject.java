package com.cse110.flashbackmusicplayer;

import java.util.Observer;

/**
 * Created by Amritansh on 3/8/2018.
 */

public interface SongSubject {
    public void registerObserver(DBObserver o);
    public void removeObserver(Observer o);
    public void notifyObservers();
}
