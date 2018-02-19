package com.cse110.flashbackmusicplayer;


import android.location.Location;
import android.location.LocationManager;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
@LargeTest
@RunWith(AndroidJUnit4.class)

/**
 * Created by Amritansh on 2/17/2018.
 */

public class Test_flashback_change_cond {


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    //To test flashback
    static MockUserState state;
    @Before
    public void setup() {
        state = new MockUserState();
    }

    @Test
    public void testFlashbackCond (){

        Location loc = new Location(LocationManager.GPS_PROVIDER);

        //setting up some random values
        state.setDayOfWeek(2);
        state.setDate("2/23/2018");
        loc.setLongitude(12.0); loc.setLatitude(-114.5);
        state.setTimeSegment(TimeSegment.EVENING);
        state.setSystemTime(1);
        state.setLocation(loc);
        state.setTime("10:20:00");

        //The problem should be in these inititializations.
        //Changing the state and db in mainactivity to the required ones here
        //mActivityTestRule.getActivity().userState = state;
        //mActivityTestRule.getActivity().songDB = new SongDatabase(state);
        Song s1 = new Song("after_the_storm.mp3", "After The Storm", "Origins - The Best of Terry Oldfield", "Terry Oldfield", "6", null);
        Song s2 = new Song("flight_of_the_eagle.mp3", "Flight of the Eagle", "Origins - The Best of Terry Oldfield", "Terry Oldfield", "2", null);
        mActivityTestRule.getActivity().songDB.insert(s1);
        mActivityTestRule.getActivity().songDB.insert(s2);



        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.tracksDisplayButton), withText("Tracks"),
                        isDisplayed()));
        appCompatButton.perform(click());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.songsView)))
                .atPosition(0);
        appCompatTextView.perform(click());



        ViewInteraction backButton = onView(
                allOf(withId(R.id.backButton), withText("Back"), isDisplayed()));
        backButton.perform(click());

        state.setTimeSegment(TimeSegment.MORNING);

        appCompatButton.perform(click());

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.songsView)))
                .atPosition(5);
        appCompatTextView2.perform(click());

        backButton.perform(click());

        state.setTimeSegment(TimeSegment.EVENING);

        ViewInteraction appFlashButton = onView(
                allOf(withId(R.id.switchMode), withText("Toggle Flashback"), isDisplayed()));
        appFlashButton.perform(click());

        //TODO
        // No song playes on flashback mode. FIX test

        ViewInteraction titleView = onView(
                allOf(withId(R.id.songTitle), isDisplayed()));
        titleView.check(matches(withText("After the Storm\nTerry Oldfield")));

        ViewInteraction backButton2 = onView(
                allOf(withId(R.id.exit), withText("Back"), isDisplayed()));
        backButton2.perform(click());


    }

}
