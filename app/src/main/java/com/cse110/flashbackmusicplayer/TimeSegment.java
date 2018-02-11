package com.cse110.flashbackmusicplayer;

public enum TimeSegment {

    MORNING(0, 5, 11),
    NOON(1, 11,17),
    EVENING(2, 17,5);

    // The total number of time segments defined below.
    static final public int numSegments = 3;

    TimeSegment(int index, int startHour, int endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.index = index;
    }

    private int startHour, endHour;
    private int index;

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getIndex() {
        return index;
    }
}
