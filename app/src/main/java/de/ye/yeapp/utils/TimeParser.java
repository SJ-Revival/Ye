package de.ye.yeapp.utils;

public class TimeParser {

    public TimeParser() {

    }

    public float combineSecondsAndMilliseconds(int seconds, int milliseconds) {
        return (seconds * 1000) + milliseconds;
    }

}
