package com.yeBerlin.app.util;

public class Util {

    public Util () {

    }

    public int combineSecondsAndMilliseconds(int seconds, int milliseconds) {
        seconds = seconds * 1000;
        return seconds + milliseconds;
    }

}
