package com.mausv.mnotification;

import java.util.Calendar;

/**
 * Created by mausv on 10/24/2016.
 */

public class MNotificationTimer {
    private Calendar calendar;
    private long repeatingIntervalInMilis;

    public MNotificationTimer(int hourToWake, int minuteToWake, int secondToWake, long repeatingIntervalInMilis) {
        this.calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourToWake);
        calendar.set(Calendar.MINUTE, minuteToWake);
        calendar.set(Calendar.SECOND, secondToWake);
        this.repeatingIntervalInMilis = repeatingIntervalInMilis;
    }

    public MNotificationTimer(long repeatingIntervalInMilis) {
        this.calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        this.repeatingIntervalInMilis = repeatingIntervalInMilis;
    }

    @Override
    public String toString() {
        return "MNotificationTimer{" +
                "calendar=" + calendar +
                ", repeatingIntervalInMilis=" + repeatingIntervalInMilis +
                '}';
    }

    public long getTime() {
        return calendar.getTimeInMillis();
    }

    public long getRepeatingIntervalInMilis() {
        return repeatingIntervalInMilis;
    }
}
