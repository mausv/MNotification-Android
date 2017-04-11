package com.mausv.mnotification;

import java.util.ArrayList;

/**
 * Created by mausv on 11/23/2016.
 */

class MIntent {
    private int id;
    private String title;
    private String body;
    private String subMessage;
    private ArrayList<Integer> days;
    private MNotificationTimer timer;
    private MNotificationAction action;

    public MIntent(int id, String title, String body, String subMessage, ArrayList<Integer> daysAsJson, MNotificationTimer timer, MNotificationAction action) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.subMessage = subMessage;
        this.days = daysAsJson;
        this.timer = timer;
    }

    public MNotificationAction getAction() {
        return action;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getSubMessage() {
        return subMessage;
    }

    public ArrayList<Integer> getDays() {
        return days;
    }

    public MNotificationTimer getTimer() {
        return timer;
    }

    @Override
    public String toString() {
        return "MIntent{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", subMessage='" + subMessage + '\'' +
                ", days=" + days +
                ", timer=" + timer +
                '}';
    }
}
