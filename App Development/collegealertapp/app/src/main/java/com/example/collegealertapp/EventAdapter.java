package com.example.collegealertapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.collegealertapp.databinding.ItemEventBinding;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class EventAdapter
        extends ListAdapter<Event, EventAdapter.VH> {

    private static final String KEY_TITLE       = "KEY_TITLE";
    private static final String KEY_DESC        = "KEY_DESC";
    private static final String KEY_EVENT_TIME = "KEY_EVENT_TIME";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, HH:mm",
                    Locale.getDefault()
            );

    /** click callback */
    public interface OnItemClickListener {
        void onItemClick(@NonNull Event e);
    }

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        clickListener = l;
    }

    public EventAdapter() {
        super(new DiffUtil.ItemCallback<Event>() {
            @Override
            public boolean areItemsTheSame(
                    @NonNull Event oldE,
                    @NonNull Event newE
            ) {
                return oldE.id == newE.id;
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(
                    @NonNull Event oldE,
                    @NonNull Event newE
            ) {
                return oldE.equals(newE); // implement equals() in Event
            }

            @Nullable
            @Override
            public Object getChangePayload(
                    @NonNull Event oldE,
                    @NonNull Event newE
            ) {
                Bundle diff = new Bundle();
                if (!oldE.title.equals(newE.title))
                    diff.putString(KEY_TITLE, newE.title);
                if (!oldE.description.equals(newE.description))
                    diff.putString(KEY_DESC, newE.description);
                if (oldE.eventTime != newE.eventTime)
                    diff.putLong(KEY_EVENT_TIME, newE.eventTime);
                return diff.isEmpty() ? null : diff;
            }
        });
    }

    @NonNull @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemEventBinding b = ItemEventBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH holder,
            int position
    ) {
        holder.bind(getItem(position), clickListener);
    }


    @Override
    public void onBindViewHolder(
            @NonNull VH holder,
            int position,
            @NonNull List<Object> payloads
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            holder.bindPartial(getItem(position), (Bundle) payloads.get(0));
        }
    }

    static final class VH extends RecyclerView.ViewHolder {
        private final ItemEventBinding b;

        VH(ItemEventBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(
                @NonNull Event e,
                @Nullable OnItemClickListener clickListener
        ) {
            b.tvTitle.setText(e.title);
            b.tvDesc.setText(e.description);
            b.tvDate.setText(formatTs(e.eventTime));
            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onItemClick(e);
            });
        }

        void bindPartial(
                @NonNull Event e,
                @NonNull Bundle diff
        ) {
            if (diff.containsKey(KEY_TITLE))
                b.tvTitle.setText(diff.getString(KEY_TITLE));
            if (diff.containsKey(KEY_DESC))
                b.tvDesc.setText(diff.getString(KEY_DESC));
            if (diff.containsKey(KEY_EVENT_TIME))
                b.tvDate.setText(formatTs(diff.getLong(KEY_EVENT_TIME)));
        }

        private static String formatTs(long ms) {
            return Instant.ofEpochMilli(ms)
                    .atZone(ZoneId.systemDefault())
                    .format(DATE_FMT);
        }
    }
}
