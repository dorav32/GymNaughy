package com.gymnaughy.android.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gymnaughy.android.databinding.FragmentWorkoutPlanBinding;
import com.gymnaughy.android.model.WorkoutPlan;
import com.gymnaughy.android.ui.adapter.WorkoutDayAdapter;
import com.gymnaughy.android.viewmodel.DashboardViewModel;

public class WorkoutPlanFragment extends Fragment {

    private FragmentWorkoutPlanBinding binding;
    private DashboardViewModel viewModel;
    private WorkoutPlan currentPlan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Reuses DashboardViewModel purely as the already-subscribed source of the
        // current plan; this fragment never mutates it.
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        viewModel.start();

        WorkoutDayAdapter adapter = new WorkoutDayAdapter(day -> {
            if (currentPlan == null) {
                return;
            }
            Intent intent = new Intent(requireContext(), WorkoutDetailActivity.class);
            intent.putExtra(WorkoutDetailActivity.EXTRA_PLAN_ID, currentPlan.getId());
            intent.putExtra(WorkoutDetailActivity.EXTRA_DAY_ID, day.getId());
            startActivity(intent);
        });

        binding.recyclerWorkoutDays.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerWorkoutDays.setAdapter(adapter);

        viewModel.getCurrentPlan().observe(getViewLifecycleOwner(), plan -> {
            currentPlan = plan;
            if (plan != null && plan.getDays() != null) {
                adapter.submitList(plan.getDays());
                binding.tvEmptyState.setVisibility(View.GONE);
            } else {
                binding.tvEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
