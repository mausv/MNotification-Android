package com.mausv.mnotification;

/**
 * Created by mausv on 11/24/2016.
 */

class MNotificationNotFoundException extends Exception {
    @Override
    public String getMessage() {
        return "Notification was not found";
    }
}
