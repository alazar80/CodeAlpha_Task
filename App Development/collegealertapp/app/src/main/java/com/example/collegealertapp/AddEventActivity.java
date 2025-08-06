package com.example.collegealertapp;

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

public class AddEventActivity extends AppCompatActivity {
    private ActivityAddEventBinding binding;
    private DBHelper db;
    private LocalDateTime selectedDateTime;

    // static final formatter to avoid reallocating
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DBHelper(this);

        // 1) Make date field non-editable and show pickers
        binding.etDate.setFocusable(false);
        binding.etDate.setOnClickListener(v -> showDateTimePicker());

        binding.btnAdd.setOnClickListener(v -> onAddClicked());
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (dp, year, month, day) -> new TimePickerDialog(
                        this,
                        (tp, hour, minute) -> {
                            selectedDateTime = LocalDateTime.of(year, month + 1, day, hour, minute);
                            binding.etDate.setText(selectedDateTime.format(DATE_FORMATTER));
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                ).show(),
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void onAddClicked() {
        String title = binding.etTitle.getText().toString().trim();
        String desc  = binding.etDesc .getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || selectedDateTime == null) {
            Toast.makeText(this, R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        long timeMs = selectedDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        long rowId = db.addEvent(title, desc, timeMs);
        scheduleAlarm(this, rowId, title, desc, timeMs);

        Toast.makeText(this, R.string.event_added, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private static void scheduleAlarm(
            Context ctx,
            long rowId,
            String title,
            String desc,
            long triggerAtMs
    ) {
        // 2) spread 64-bit ID into int safely
        int requestCode = (int)(rowId ^ (rowId >>> 32));
        Intent intent = new Intent(ctx, AlarmReceiver.class)
                .putExtra("eventId", rowId)
                .putExtra("title",   title)
                .putExtra("desc",    desc);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pi = PendingIntent.getBroadcast(ctx, requestCode, intent, flags);
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        if (am != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMs, pi);
            }
        }
    }
    public static void cancelAlarm(Context ctx, long eventId) {
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, (int)eventId, intent, PendingIntent.FLAG_NO_CREATE);
        if (pi != null) {
            AlarmManager am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
