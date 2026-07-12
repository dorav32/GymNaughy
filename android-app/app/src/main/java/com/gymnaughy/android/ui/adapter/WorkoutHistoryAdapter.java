package com.gymnaughy.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.gymnaughy.android.databinding.ItemWorkoutHistoryBinding;
import com.gymnaughy.android.model.WorkoutLog;
import com.gymnaughy.android.util.DateUtils;

public class WorkoutHistoryAdapter extends ListAdapter<WorkoutLog, WorkoutHistoryAdapter.ViewHolder> {

    public WorkoutHistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkoutHistoryBinding binding = ItemWorkoutHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorkoutHistoryBinding binding;

        ViewHolder(ItemWorkoutHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WorkoutLog log) {
            binding.tvDate.setText(DateUtils.formatFriendly(DateUtils.parseIso(log.getCompletedAt())));
            binding.tvDuration.setText(String.valueOf(log.getDurationSeconds() / 60));
            binding.tvSetsCount.setText(String.valueOf(
                    log.getCompletedSets() == null ? 0 : log.getCompletedSets().size()));
        }
    }

    private static final DiffUtil.ItemCallback<WorkoutLog> DIFF_CALLBACK = new DiffUtil.ItemCallback<WorkoutLog>() {
        @Override
        public boolean areItemsTheSame(@NonNull WorkoutLog oldItem, @NonNull WorkoutLog newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull WorkoutLog oldItem, @NonNull WorkoutLog newItem) {
            return oldItem.getCompletedAt().equals(newItem.getCompletedAt());
        }
    };
}
