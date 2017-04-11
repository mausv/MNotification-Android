package com.mausv.mnotification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by mausv on 10/24/2016.
 */


class MNotification {
    private Context context;
    private int id;
    private Notification notification;
    private NotificationCompat.Builder notificationBuilder;
    private MNotificationAction action;

    public MNotification (int id, String title, String body, String subMessage, Context context, Class destination, MNotificationAction action) {
        this.id = id;
        this.context = context;
        this.action = action;
        if(context != null) {
            notificationBuilder = new NotificationCompat.Builder(context);
//            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
//            notificationBuilder.addAction(R.drawable.ic_menu_attendance, subMessage, null);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(body);

            Intent resultIntent = new Intent(context, destination);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(destination);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            notificationBuilder.setContentIntent(resultPendingIntent);
            notificationBuilder.setAutoCancel(true);
        } else {
            throw new NullPointerException("Missing context. Did you create an instance of MNotificationManager and pass the right Context?");
        }
    }

    //TODO: Add method to include subActions
    //TODO: Add method to manage cancel
    //TODO: Add method to manage icons
    //TODO: Add method to manage Intent to trigger when clicking notification

    public Notification getNotification(){
        notification = notificationBuilder.build();
        return notification;
    }

    public int getId() {
        return id;
    }

    public void executeAction(){
        if (action != null){
            action.execute();
        }
    }

    public void setNotificationBehavior(boolean enableVibration, boolean enableSound) {
        if(enableVibration) {
            enableVibration();
        }
        if(enableSound) {
            enableSound();
        }

        /**
         * Reconstruct notification
         */
        this.notification = notificationBuilder.build();
    }

    private void enableVibration() {
        notificationBuilder.setVibrate(new long[] { 500, 1000 });
    }

    private void enableSound() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(uri);
    }
}
