package com.example.collegealertapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collegealertapp.databinding.ActivityAddEditEventBinding;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditEventActivity extends AppCompatActivity {
    public static final String EXTRA_ID    = "event_id";
    public static final String EXTRA_TITLE = "event_title";
    public static final String EXTRA_DESC  = "event_desc";
    public static final String EXTRA_TIME  = "event_time";

    private ActivityAddEditEventBinding binding;
    private DBHelper db;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private boolean isEdit = false;
    private long eventId;
    private long selectedTimeMs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DBHelper(this);

        // 1) Detect edit vs. add
        Intent in = getIntent();
        if (in.hasExtra(EXTRA_ID)) {
            isEdit = true;
            eventId = in.getLongExtra(EXTRA_ID, -1);
            binding.btnSave.setText("Update Event");
            exec.execute(() -> {
                Event e = db.getEventById(eventId);
                runOnUiThread(() -> {
                    binding.etTitle.setText(e.title);
                    binding.etDesc .setText(e.description);
                    selectedTimeMs = e.eventTime;
                    binding.etDateTime.setText(
                            DateUtils.formatDateTime(
                                    this,
                                    selectedTimeMs,
                                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
                            )
                    );
                });
            });
        }

        // 2) Wire up date/time picker
        binding.etDateTime.setFocusable(false);
        binding.etDateTime.setClickable(true);
        binding.etDateTime.setOnClickListener(v -> showDateTimePicker());

        // 3) Save
        binding.btnSave.setOnClickListener(v -> saveAndFinish());
    }

    private void saveAndFinish() {
        String title = binding.etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            binding.etTitle.setError("Required");
            return;
        }
        String desc = binding.etDesc.getText().toString().trim();
        long   time = selectedTimeMs;

        Intent result = new Intent();
        if (isEdit) {
            db.updateEvent(eventId, title, desc, time);
            AlarmUtil.cancelAlarm(this, eventId);
            AlarmUtil.scheduleExactAlarm(this, eventId, time);
            result.putExtra(EXTRA_ID, eventId);
        } else {
            long newId = db.addEvent(title, desc, time);
            AlarmUtil.scheduleExactAlarm(this, newId, time);
            result.putExtra(EXTRA_ID, newId);
        }

        result.putExtra(EXTRA_TITLE, title);
        result.putExtra(EXTRA_DESC,  desc);
        result.putExtra(EXTRA_TIME,  time);
        setResult(RESULT_OK, result);
        finish();
    }

    private void showDateTimePicker() {
        // Use a Calendar so we can read/write both date & time in ms
        Calendar cal = Calendar.getInstance();
        if (selectedTimeMs > 0) {
            cal.setTimeInMillis(selectedTimeMs);
        }

        // 1) DatePicker
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    cal.set(Calendar.YEAR,  year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // 2) TimePicker
                    new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                cal.set(Calendar.MINUTE,      minute);
                                cal.set(Calendar.SECOND,      0);

                                // Save & display
                                selectedTimeMs = cal.getTimeInMillis();
                                binding.etDateTime.setText(
                                        DateUtils.formatDateTime(
                                                this,
                                                selectedTimeMs,
                                                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
                                        )
                                );
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                    ).show();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
