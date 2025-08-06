package com.example.collegealertapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegealertapp.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DBHelper db;
    private EventAdapter adapter;

    // new Activity-Result API
    private ActivityResultLauncher<Intent> editLauncher;

    // for undo
    private Event recentlyDeletedEvent;
    private int   recentlyDeletedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db      = new DBHelper(this);
        adapter = new EventAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
// 1) Tap → add
        binding.fabAdd.setOnClickListener(v -> {
            Intent addIt = new Intent(this, AddEditEventActivity.class);
            // no EXTRA_ID → Add mode
            editLauncher.launch(addIt);
        });

        // 2) Tap → edit (you already have this)
        adapter.setOnItemClickListener(e -> {
            Intent editIt = new Intent(this, AddEditEventActivity.class);
            editIt.putExtra(AddEditEventActivity.EXTRA_ID, e.id);
            editLauncher.launch(editIt);
        });

        // 1) Register for edit-event result
        editLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                res -> {
                    if (res.getResultCode() == RESULT_OK) {
                        loadAllEvents();
                    }
                }
        );

        // 2) Swipe → delete with undo
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(
                    @NonNull RecyclerView rv,
                    @NonNull RecyclerView.ViewHolder vh,
                    @NonNull RecyclerView.ViewHolder tgt
            ) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                recentlyDeletedPosition = vh.getAdapterPosition();
                recentlyDeletedEvent    = adapter.getCurrentList().get(recentlyDeletedPosition);
                db.deleteEvent(recentlyDeletedEvent.id);
                loadAllEvents();
                showUndoSnackbar();
            }
        }).attachToRecyclerView(binding.recyclerView);

        // 3) Tap → edit
        adapter.setOnItemClickListener(e -> {
            Intent it = new Intent(this, AddEditEventActivity.class);
            it.putExtra(AddEditEventActivity.EXTRA_ID, e.id);
            editLauncher.launch(it);
        });

        loadAllEvents();

        // optional: safe FCM topic subscribe
        FirebaseMessaging.getInstance()
                .subscribeToTopic("events")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MainActivity","Subscribed to events");
                    } else {
                        Log.w("MainActivity","FCM subscribe failed",task.getException());
                    }
                });
    }

    private void loadAllEvents() {
        List<Event> list = db.getAllEventsList();
        adapter.submitList(list);
    }

    private void showUndoSnackbar() {
        Snackbar.make(binding.getRoot(), "Event deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                    if (recentlyDeletedEvent != null) {
                        long id = db.addEvent(
                                recentlyDeletedEvent.title,
                                recentlyDeletedEvent.description,
                                recentlyDeletedEvent.eventTime
                        );
                        AlarmUtil.scheduleExactAlarm(this, id, recentlyDeletedEvent.eventTime);
                        loadAllEvents();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
