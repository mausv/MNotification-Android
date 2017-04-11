package com.mausv.mnotificationtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mausv.mnotification.MNotificationAction;
import com.mausv.mnotification.MNotificationManager;
import com.mausv.mnotification.MNotificationTimer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Manager for the notifications
        MNotificationManager mNotificationManager = new MNotificationManager(MainActivity.this);

        // Every day at 07:50:00
//        MNotificationTimer mNotificationTimerIn = new MNotificationTimer(16, 7, 0, 100);

        // Every hour
        MNotificationTimer mNotificationTimerIn = new MNotificationTimer(1*1000);
        MNotificationAction action = new CustomNotificationAction();
        mNotificationManager.scheduleNotification(1, "Exgerm", "Locación", "Enciende tu locación.", mNotificationTimerIn, action, true);

    }
}
