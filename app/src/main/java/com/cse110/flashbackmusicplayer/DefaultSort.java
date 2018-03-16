package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;

/**
 * Created by Eldon on 3/15/2018.
 */

public class DefaultSort implements SortStrategy {

    public ArrayList<Song> sort(ArrayList<Song> input) {
        ArrayList<Song> sorted = input;
        String date, year, month, day;
        String date2, year2, month2, day2;
        for(int i = 1; i < sorted.size(); i++) {
            //initialize all the vars to compare with
            date = sorted.get(i).getDate();
            year = date.substring(date.length()-4, date.length());
            month = date.substring(date.indexOf('/')+1, date.length()-5);
            day = date.substring(0, date.indexOf('/'));
            int j = i - 1;

            while( j > -1) {
                date2 = sorted.get(j).getDate();

                year2 = date2.substring(date2.length()-4, date2.length());
                //if the year is the same
                if(year2.compareTo(year) == 0) {
                    month2 = date2.substring(date2.indexOf('/')+1, date2.length()-5);
                    //if the months are the same
                    if(month2.compareTo(month) == 0) {
                        day2 = date2.substring(0, date2.indexOf('/'));
                        //if the days are the same
                        if(day2.compareTo(day) == 0) {
                            //the time can't be the same, so this is the final checker
                            if(sorted.get(j).getTime().compareTo(sorted.get(i).getTime()) > 0) {
                                sorted.set(j + 1, sorted.get(j));
                            }
                        }
                        else if(day2.compareTo(day) > 0) {
                            sorted.set(j + 1, sorted.get(j));
                        }
                    }
                    else if(month2.compareTo(month) > 0) {
                        sorted.set(j + 1, sorted.get(j));
                    }
                }
                else if(year2.compareTo(year) > 0) {
                    sorted.set(j + 1, sorted.get(j));
                }
                j--;
            }
            sorted.set(j + 1, sorted.get(i));
        }
        return sorted;
    }
}
