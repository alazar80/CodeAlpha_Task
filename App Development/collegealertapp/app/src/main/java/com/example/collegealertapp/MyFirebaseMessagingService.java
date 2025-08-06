package com.example.collegealertapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "fcm_events";

    @Override
    public void onNewToken(String token) {
        // send this token to your server if you have one
    }

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        String title = msg.getNotification() != null
                ? msg.getNotification().getTitle()
                : "New Event";
        String body  = msg.getNotification() != null
                ? msg.getNotification().getBody()
                : "Tap to view";

        NotificationManager nm =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Event Alerts",
                            NotificationManager.IMPORTANCE_HIGH
                    )
            );
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);

        nm.notify((int)System.currentTimeMillis(), b.build());
    }
}
