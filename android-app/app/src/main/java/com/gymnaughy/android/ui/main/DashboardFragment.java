package com.gymnaughy.android.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gymnaughy.android.databinding.FragmentDashboardBinding;
import com.gymnaughy.android.model.WorkoutDay;
import com.gymnaughy.android.model.WorkoutPlan;
import com.gymnaughy.android.viewmodel.DashboardViewModel;

import java.util.Calendar;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private WorkoutPlan currentPlan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        viewModel.start();

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                return;
            }
            binding.tvGreeting.setText(getString(com.gymnaughy.android.R.string.dashboard_greeting, user.getDisplayName()));
            binding.tvStreakValue.setText(String.valueOf(user.getStreak()));
        });

        viewModel.getCurrentPlan().observe(getViewLifecycleOwner(), plan -> {
            currentPlan = plan;
            renderTodayWorkout(plan);
        });

        // Surfaces Firestore permission-denied / offline errors instead of leaving
        // the Start Workout button silently disabled with no explanation.
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        binding.btnStartWorkout.setOnClickListener(v -> {
            WorkoutDay today = todaysWorkoutDay();
            if (currentPlan == null || today == null) {
                return;
            }
            Intent intent = new Intent(requireContext(), WorkoutDetailActivity.class);
            intent.putExtra(WorkoutDetailActivity.EXTRA_PLAN_ID, currentPlan.getId());
            intent.putExtra(WorkoutDetailActivity.EXTRA_DAY_ID, today.getId());
            startActivity(intent);
        });
    }

    private void renderTodayWorkout(WorkoutPlan plan) {
        WorkoutDay today = todaysWorkoutDay();
        if (plan == null || today == null) {
            binding.tvTodayWorkoutLabel.setText(com.gymnaughy.android.R.string.dashboard_no_plan);
            binding.btnStartWorkout.setEnabled(false);
            return;
        }
        binding.tvTodayWorkoutLabel.setText(today.getLabel());
        binding.btnStartWorkout.setEnabled(true);
    }

    /**
     * Maps the plan's ordered day list onto the current day of the week using a simple
     * modulo, so a 4-day/week plan repeats predictably without needing its own calendar.
     */
    @Nullable
    private WorkoutDay todaysWorkoutDay() {
        if (currentPlan == null || currentPlan.getDays() == null || currentPlan.getDays().isEmpty()) {
            return null;
        }
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int index = dayOfWeek % currentPlan.getDays().size();
        return currentPlan.getDays().get(index);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
