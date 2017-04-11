package com.mausv.mnotification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Library to handle notifications easily
 * @author Mauricio Silva
 * @version 0.1.0
 */

public class MNotificationManager {
    /**
     * Main variables
     */
    private static final String TAG = "MNotificationManager";
    private Context context = null;
    private ArrayList<Integer> days;
    private static MDatabaseHandler db = null;

    /**
     * Identifiers of the parts of the notification
     */
    private static final String NOTIFICATION_ID = "n_id";
    private static final String NOTIFICATION_TITLE = "n_title";
    private static final String NOTIFICATION_BODY = "n_body";
    private static final String NOTIFICATION_SUB_MESSAGE = "n_sub_message";
    private static final String NOTIFICATION_DAYS = "n_days";

    /**
     * Identifiers for the clear method
     */
    public static final int ERROR = -1;
    public static final int CLEAR = 0;
    public static final int NOT_FOUND = 1;
    public static final int ADDED = 2;
    public static final int DELETED = 3;
    public static final int UPDATED = 4;


    public MNotificationManager(Context context) {
        this.context = context;
        db = new MDatabaseHandler(context);
        days = new ArrayList<>();
        days.add(Calendar.MONDAY);
        days.add(Calendar.TUESDAY);
        days.add(Calendar.WEDNESDAY);
        days.add(Calendar.THURSDAY);
        days.add(Calendar.FRIDAY);
        days.add(Calendar.SATURDAY);
        days.add(Calendar.SUNDAY);
    }

    /**
     * Exclude days from MNotificationManager
     * @param day Day(s) to exclude
     */
    public void exclude(Integer... day) {
        for (Integer dayObject : day) {
            for (int i = 0; i < days.size(); i++) {
                if(dayObject.equals(days.get(i))) {
                    days.set(i, -1);
                }
            }
        }
    }

    /**
     * Add to database for when the device gets rebooted
     * @param intentForReboot MIntent to save
     * @param context Context to handle operation
     */
    private static void saveForReboot(MIntent intentForReboot, Context context) {
        if(db == null) {
            db = new MDatabaseHandler(context);
        }

        if(db.getRebootNotification(intentForReboot.getId()) == null) {
            Log.d(TAG, "saveForReboot: added " + intentForReboot.getId());
            db.addRebootNotification(intentForReboot);
        } else {
            Log.d(TAG, "saveForReboot: updated notification");
            db.updateRebootNotification(intentForReboot);
        }
        Log.d(TAG, "saveForRebootSize:" + db.getRebootNotificationsCount());
    }

    /**
     * Public method to schedule a notification
     * @param notificationId Id to be set
     * @param title Title of the notification
     * @param body Body message of the notification
     * @param subMessage Message of the notification when pulled down
     * @param timer Timer that schedules the notification
     */
    public void scheduleNotification(int notificationId, String title, String body, String subMessage, MNotificationTimer timer, boolean shouldScheduleAfterReboot) {
        MIntent createdIntent = new MIntent(notificationId, title, body, subMessage, days, timer);
        scheduleNotification(createdIntent, context, shouldScheduleAfterReboot);
    }

    /**
     * Schedule a notification with an MIntent
     * @param savedIntent MIntent to schedule
     * @param context Context to handle operation
     */
    private static void scheduleNotification(MIntent savedIntent, Context context, boolean shouldRescheduleAfterReboot) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_ID, savedIntent.getId());
        notificationIntent.putExtra(NOTIFICATION_TITLE, savedIntent.getTitle());
        notificationIntent.putExtra(NOTIFICATION_BODY, savedIntent.getBody());
        notificationIntent.putExtra(NOTIFICATION_SUB_MESSAGE, savedIntent.getSubMessage());
        notificationIntent.putIntegerArrayListExtra(NOTIFICATION_DAYS, savedIntent.getDays());

        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context, savedIntent.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        MNotificationTimer timer = savedIntent.getTimer();
        if(timer != null) {
            //TODO: Fix timer after one day scheduling wrongly
            long timeToTriggerTimer = (timer.getTime() < System.currentTimeMillis() ? timer.getTime() + timer.getRepeatingIntervalInMilis() : timer.getTime());
            Log.d(TAG, "scheduleNotificationTime: " + timeToTriggerTimer);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeToTriggerTimer, timer.getRepeatingIntervalInMilis(), notificationPendingIntent);

            MIntent intentForReboot = new MIntent(
                    savedIntent.getId(),
                    savedIntent.getTitle(),
                    savedIntent.getBody(),
                    savedIntent.getSubMessage(),
                    savedIntent.getDays(),
                    savedIntent.getTimer()
            );

            if(shouldRescheduleAfterReboot) {
                saveForReboot(intentForReboot, context);
            }
        } else {
            throw new NullPointerException("MNotificationTimer is null. Did you pass an instantiated object of MNotificationTimer correctly?");
        }
        Log.d(TAG, "scheduleNotification: " + savedIntent.getTitle());
    }

    /**
     * BroadcastReceiver to handle notifications normally
     */
    public static class NotificationPublisher extends BroadcastReceiver {
        private final String TAG = "MNotificationManager";
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getStringExtra(NOTIFICATION_TITLE));
            MNotification mNotification = new MNotification(
                    intent.getIntExtra(NOTIFICATION_ID, 0),
                    intent.getStringExtra(NOTIFICATION_TITLE),
                    intent.getStringExtra(NOTIFICATION_BODY),
                    intent.getStringExtra(NOTIFICATION_SUB_MESSAGE),
                    context);
            mNotification.setNotificationBehavior(true, true);
            /**
             * Check if the day is within the excluded ones,
             * if it isn't, send the notification
             */
            if (intent.getIntegerArrayListExtra(NOTIFICATION_DAYS).contains(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))) {
                Log.d(TAG, "sendNotification: sent" + mNotification);
                sendNotification(mNotification.getId(), mNotification.getNotification(), context);
            } else {
                Log.d(TAG, "sendNotification: not sent");
            }

        }

        private void sendNotification(int id, Notification notification, Context context) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
            Log.d(TAG, "sendNotification: " + id + " = " + notification.toString());
        }
    }

    /**
     * BroadcastReceiver to handle boot notifications by scheduling them
     */
    public static class NotificationOnBoot extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Log.d(TAG, "onReceive: after boot");
                if(db == null){
                    db = new MDatabaseHandler(context);
                }
                for (MIntent mIntent : db.getRebootNotifications()) {
                    scheduleNotification(mIntent, context, true);
                }
            }
        }
    }

    /**
     * Public method to clear the notification
     * @param notificationId Id of the notification to clear
     * @return If the action was completed successfully with CLEAR or NOT_FOUND
     */
    public int clearScheduledNotification(int notificationId) {
        return clearNotification(notificationId);
    }

    /**
     * Private method to clear the notification
     * @param notificationId Id of the notification to clear
     * @return If the action was completed successfully with an int
     */
    private int clearNotification(int notificationId) {
        if(db == null){
            db = new MDatabaseHandler(context);
        }
        Log.d(TAG, "clearNotificationCount: " + db.getRebootNotificationsCount());
        MIntent notificationToRemove = db.getRebootNotification(notificationId);
        /**
         * If the notification to find is not null, proceed to remove
         */
        if(notificationToRemove != null) {
            db.removeRebootNotification(notificationToRemove);
            Log.d(TAG, "deleted: " + notificationId);
            return CLEAR;
        }
        return NOT_FOUND;
    }

}
