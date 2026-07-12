package com.gymnaughy.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.gymnaughy.android.databinding.ItemWorkoutDayBinding;
import com.gymnaughy.android.model.PlannedExercise;
import com.gymnaughy.android.model.WorkoutDay;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WorkoutDayAdapter extends ListAdapter<WorkoutDay, WorkoutDayAdapter.ViewHolder> {

    private final Consumer<WorkoutDay> onDayClicked;

    public WorkoutDayAdapter(Consumer<WorkoutDay> onDayClicked) {
        super(DIFF_CALLBACK);
        this.onDayClicked = onDayClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkoutDayBinding binding = ItemWorkoutDayBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), onDayClicked);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorkoutDayBinding binding;

        ViewHolder(ItemWorkoutDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WorkoutDay day, Consumer<WorkoutDay> onDayClicked) {
            binding.tvDayLabel.setText(day.getLabel());

            String exerciseSummary = day.getExercises() == null ? "" : day.getExercises().stream()
                    .map(PlannedExercise::getExerciseId)
                    .collect(Collectors.joining(", "));
            binding.tvExerciseSummary.setText(exerciseSummary);
            binding.tvExerciseCount.setText(String.valueOf(
                    day.getExercises() == null ? 0 : day.getExercises().size()));

            binding.getRoot().setOnClickListener(v -> onDayClicked.accept(day));
        }
    }

    private static final DiffUtil.ItemCallback<WorkoutDay> DIFF_CALLBACK = new DiffUtil.ItemCallback<WorkoutDay>() {
        @Override
        public boolean areItemsTheSame(@NonNull WorkoutDay oldItem, @NonNull WorkoutDay newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull WorkoutDay oldItem, @NonNull WorkoutDay newItem) {
            return oldItem.getLabel().equals(newItem.getLabel());
        }
    };
}
