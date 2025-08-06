package com.example.collegealertapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collegealertapp.databinding.ActivityAddEditEventBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditEventActivity extends AppCompatActivity {
    public static final String EXTRA_ID    = "event_id";
    public static final String EXTRA_TITLE = "event_title";
    public static final String EXTRA_DESC  = "event_desc";
    public static final String EXTRA_TIME  = "event_time";
    ActivityAddEditEventBinding binding;
    private DBHelper db;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private boolean isEdit = false;
    private long eventId;
    private long selectedTimeMs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =
                ActivityAddEditEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setContentView(binding.getRoot());

        db = new DBHelper(this);

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

        binding.etDateTime.setOnClickListener(v -> showDateTimePicker());
        binding.btnSave   .setOnClickListener(v -> saveAndFinish());
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
        // your DatePicker → TimePicker flow here,
        // setting `selectedTimeMs` & `binding.etDateTime`
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
