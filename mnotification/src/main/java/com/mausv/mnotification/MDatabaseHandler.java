package com.mausv.mnotification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by mausv on 11/25/2016.
 */

class MDatabaseHandler extends SQLiteOpenHelper{

    /**
     * Main variables
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MNotificationDatabase";

    /**
     * Table structure for notifications
     */
    private static final String TABLE_REBOOT_NOTIFICATIONS = "reboot_notifications";
    private static final String TRN_ID = "id";
    private static final String TRN_TITLE = "title";
    private static final String TRN_BODY = "body";
    private static final String TRN_SUB_MESSAGE = "sub_message";
    private static final String TRN_DAYS = "days";
    private static final String TRN_TIMER = "timer";
    private static final String TRN_ACTION = "action";


    MDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_REBOOT_NOTIFICATIONS = "CREATE TABLE " + TABLE_REBOOT_NOTIFICATIONS + "("
                + TRN_ID + " INTEGER PRIMARY KEY, " + TRN_TITLE + " TEXT," + TRN_BODY + " TEXT,"
                + TRN_SUB_MESSAGE + " TEXT," + TRN_DAYS + " TEXT," + TRN_TIMER + " TEXT,"
                + TRN_ACTION + " TEXT)";
        db.execSQL(CREATE_TABLE_REBOOT_NOTIFICATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REBOOT_NOTIFICATIONS);
        onCreate(db);
    }

    /**
     * Add a notification to the database
     * @param rebootIntent MIntent to add
     */
    long addRebootNotification(MIntent rebootIntent) {
        SQLiteDatabase db = this.getReadableDatabase();
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put(TRN_TITLE, rebootIntent.getTitle());
        values.put(TRN_BODY, rebootIntent.getBody());
        values.put(TRN_SUB_MESSAGE, rebootIntent.getSubMessage());
        values.put(TRN_DAYS, gson.toJson(rebootIntent.getDays()));
        values.put(TRN_TIMER, gson.toJson(rebootIntent.getTimer()));
        long result = db.insert(TABLE_REBOOT_NOTIFICATIONS, null, values);
        db.close();
        if(result > 0) {
            return MNotificationManager.ADDED;
        }
        return MNotificationManager.ERROR;
    }

    /**
     * Remove a notification from the database
     * @param rebootIntent MIntent to remove
     */
    int removeRebootNotification(MIntent rebootIntent) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_REBOOT_NOTIFICATIONS, TRN_ID + " = ?",
                new String[] { String.valueOf(rebootIntent.getId()) });
        db.close();
        if(result > 0) {
            return MNotificationManager.DELETED;
        }
        return MNotificationManager.NOT_FOUND;
    }

    /**
     * Update record in case notification is already found
     * @param rebootIntent MIntent to update
     * @return If the notification was returned or not
     */
    int updateRebootNotification(MIntent rebootIntent) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson gson = new Gson();

        ContentValues values = new ContentValues();
        values.put(TRN_TITLE, rebootIntent.getTitle());
        values.put(TRN_BODY, rebootIntent.getBody());
        values.put(TRN_SUB_MESSAGE, rebootIntent.getSubMessage());
        values.put(TRN_DAYS, gson.toJson(rebootIntent.getDays()));
        values.put(TRN_TIMER, gson.toJson(rebootIntent.getTimer()));

        // updating row
        int result = db.update(TABLE_REBOOT_NOTIFICATIONS, values, TRN_ID + " = ?",
                new String[] { String.valueOf(rebootIntent.getId()) });
        if(result > 0) {
            return MNotificationManager.UPDATED;
        }
        return MNotificationManager.NOT_FOUND;
    }

    /**
     * Pull a notification from the database with an id
     * @param id Id to remove notification
     * @return An object if found, null if not found
     */
    MIntent getRebootNotification(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_REBOOT_NOTIFICATIONS + " WHERE "+
                        TRN_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                Gson gson = new Gson();
                ArrayList<Integer> days = gson.fromJson(cursor.getString(4), new TypeToken<ArrayList<Integer>>() {
                }.getType());
                MNotificationTimer timer = gson.fromJson(cursor.getString(5), MNotificationTimer.class);
                MNotificationAction action = gson.fromJson(cursor.getString(6), MNotificationAction.class);
                return new MIntent(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        days,
                        timer,
                        action
                );
            }
        }
        return null;
    }

    /**
     * Get an ArrayList with all the notifications
     * @return ArrayList of MIntent with the notifications
     */
    ArrayList<MIntent> getRebootNotifications() {
        ArrayList<MIntent> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_REBOOT_NOTIFICATIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Gson gson = new Gson();

        if(cursor.moveToFirst()) {
            do {
                ArrayList<Integer> days = gson.fromJson(cursor.getString(4), new TypeToken<ArrayList<Integer>>(){}.getType());
                MNotificationTimer timer = gson.fromJson(cursor.getString(5), MNotificationTimer.class);
                MNotificationAction action = gson.fromJson(cursor.getString(6), MNotificationAction.class);
                MIntent mIntent = new MIntent(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        days,
                        timer,
                        action
                );
                list.add(mIntent);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Count of all the saved notifications
     * @return Integer with the count of all the saved notifications on the database
     */
    int getRebootNotificationsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_REBOOT_NOTIFICATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

}
