package com.gymnaughy.android.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gymnaughy.android.databinding.ActivityWorkoutDetailBinding;
import com.gymnaughy.android.firebase.OnDataChangeListener;
import com.gymnaughy.android.model.PlannedExercise;
import com.gymnaughy.android.model.WorkoutDay;
import com.gymnaughy.android.model.WorkoutPlan;
import com.gymnaughy.android.repository.WorkoutRepository;
import com.gymnaughy.android.viewmodel.WorkoutViewModel;

/**
 * Guided, one-exercise-at-a-time session screen. The plan is re-fetched here (rather
 * than passed in as a Parcelable extra) so this screen always reflects the latest
 * Firestore state even if the plan changed since the list screen was rendered.
 */
public class WorkoutDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLAN_ID = "extra_plan_id";
    public static final String EXTRA_DAY_ID = "extra_day_id";

    private ActivityWorkoutDetailBinding binding;
    private WorkoutViewModel viewModel;
    private final WorkoutRepository workoutRepository = new WorkoutRepository();

    private String planId;
    private String dayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkoutDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        planId = getIntent().getStringExtra(EXTRA_PLAN_ID);
        dayId = getIntent().getStringExtra(EXTRA_DAY_ID);

        viewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);

        workoutRepository.observeCurrentPlan(new OnDataChangeListener<WorkoutPlan>() {
            @Override
            public void onChanged(WorkoutPlan plan) {
                WorkoutDay day = plan.findDay(dayId);
                if (day != null) {
                    viewModel.start(planId, day);
                    renderCurrentExercise();
                }
            }

            @Override
            public void onError(Exception e) {
                binding.tvExerciseName.setText(e.getMessage());
            }
        });

        binding.btnLogSet.setOnClickListener(v -> {
            int reps = parseReps();
            viewModel.recordSet(reps);
            binding.etRepsCompleted.setText("");
        });

        binding.btnNextExercise.setOnClickListener(v -> {
            if (viewModel.advanceToNextExercise()) {
                renderCurrentExercise();
            } else {
                viewModel.finishWorkout();
            }
        });

        viewModel.getLogResult().observe(this, result -> {
            if (result != null) {
                finish();
            }
        });
    }

    private int parseReps() {
        try {
            return Integer.parseInt(binding.etRepsCompleted.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void renderCurrentExercise() {
        PlannedExercise exercise = viewModel.getCurrentExercise();
        if (exercise == null) {
            return;
        }
        binding.tvExerciseName.setText(exercise.getExerciseId());
        binding.tvSetsReps.setText(getString(com.gymnaughy.android.R.string.workout_sets_reps,
                exercise.getSets(), exercise.getReps()));
        binding.tvProgress.setText(getString(com.gymnaughy.android.R.string.workout_progress,
                (viewModel.getCurrentIndex().getValue() == null ? 0 : viewModel.getCurrentIndex().getValue()) + 1,
                viewModel.getTotalExercises()));

        boolean isLastExercise = viewModel.getCurrentIndex().getValue() != null
                && viewModel.getCurrentIndex().getValue() + 1 >= viewModel.getTotalExercises();
        binding.btnNextExercise.setText(isLastExercise
                ? com.gymnaughy.android.R.string.workout_finish
                : com.gymnaughy.android.R.string.workout_next_exercise);
    }
}
