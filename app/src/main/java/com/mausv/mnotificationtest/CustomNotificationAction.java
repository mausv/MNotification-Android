package com.mausv.mnotificationtest;

import android.util.Log;

import com.mausv.mnotification.MNotificationAction;

/**
 * Created by mausv on 4/11/2017.
 */

public class CustomNotificationAction implements MNotificationAction {
    @Override
    public void execute() {
        Log.d("MNotification", "CustomNotificationAction executed!");
    }
}
