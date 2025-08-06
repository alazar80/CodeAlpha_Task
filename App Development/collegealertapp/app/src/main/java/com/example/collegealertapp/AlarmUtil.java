package com.example.collegealertapp;

import android.app.PendingIntent;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.collegealertapp.databinding.ActivityAddEventBinding;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
public class AlarmUtil {
    private static String EXTRA_ID;
    private static final int FLAG = PendingIntent.FLAG_UPDATE_CURRENT
            | PendingIntent.FLAG_IMMUTABLE;

    public static void scheduleExactAlarm(Context ctx, long eventId, long triggerAtMs) {
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        Intent i = new Intent(ctx, AlarmReceiver.class);
        i.putExtra(EXTRA_ID, eventId);
        PendingIntent pi = PendingIntent.getBroadcast(ctx, (int)eventId, i, FLAG);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pi);
    }

    public static void cancelAlarm(Context ctx, long eventId) {
        Intent i = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(ctx, (int)eventId, i,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pi != null) {
            ctx.getSystemService(AlarmManager.class).cancel(pi);
        }
    }
}

