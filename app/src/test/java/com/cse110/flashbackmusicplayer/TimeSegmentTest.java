package com.cse110.flashbackmusicplayer;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeSegmentTest {
    @Test
    public void getStartHour() throws Exception {
        // The three TimeSegments that are defined in the file.
        TimeSegment morning = TimeSegment.MORNING;
        assertEquals(5, morning.getStartHour());

        TimeSegment noon = TimeSegment.NOON;
        assertEquals(11, noon.getStartHour());

        TimeSegment evening = TimeSegment.EVENING;
        assertEquals(17, evening.getStartHour());
    }

    @Test
    public void getEndHour() throws Exception {
        // The three TimeSegments that are defined in the file.
        TimeSegment morning = TimeSegment.MORNING;
        assertEquals(11, morning.getEndHour());

        TimeSegment noon = TimeSegment.NOON;
        assertEquals(17, noon.getEndHour());

        TimeSegment evening = TimeSegment.EVENING;
        assertEquals(5, evening.getEndHour());
    }

    @Test
    public void getIndex() throws Exception {
        // The three TimeSegments that are defined in the file.
        TimeSegment morning = TimeSegment.MORNING;
        assertEquals(0, morning.getIndex());

        TimeSegment noon = TimeSegment.NOON;
        assertEquals(1, noon.getIndex());

        TimeSegment evening = TimeSegment.EVENING;
        assertEquals(2, evening.getIndex());
    }

}