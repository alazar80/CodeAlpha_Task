package com.example.collegealertapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;



public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        long eventId    = intent.getLongExtra("eventId", -1);
        String title    = intent.getStringExtra("title");
        String desc     = intent.getStringExtra("desc");

        String channelId = "events_channel";
        NotificationManager nm = 
            (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          nm.createNotificationChannel(
            new NotificationChannel(channelId, "Event Alerts", NotificationManager.IMPORTANCE_HIGH)
          );
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, channelId)
          .setSmallIcon(R.drawable.ic_event)
          .setContentTitle(title)
          .setContentText(desc)
          .setAutoCancel(true);

        nm.notify((int)eventId, b.build());

        // mark in DB
        DBHelper db = new DBHelper(ctx);
        db.markNotified(eventId);
    }
}
