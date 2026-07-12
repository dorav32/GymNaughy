package com.gymnaughy.android.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.gymnaughy.android.databinding.FragmentProgressBinding;
import com.gymnaughy.android.model.WorkoutLog;
import com.gymnaughy.android.ui.adapter.WorkoutHistoryAdapter;
import com.gymnaughy.android.viewmodel.ProgressViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    private FragmentProgressBinding binding;
    private ProgressViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProgressViewModel.class);

        WorkoutHistoryAdapter adapter = new WorkoutHistoryAdapter();
        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHistory.setAdapter(adapter);

        viewModel.getHistory().observe(getViewLifecycleOwner(), logs -> {
            adapter.submitList(logs);
            renderVolumeChart(logs);
        });

        viewModel.loadHistory();
    }

    /**
     * Plots total completed sets per session (oldest → newest) as a simple proxy for
     * training volume over time.
     */
    private void renderVolumeChart(List<WorkoutLog> logs) {
        if (logs == null || logs.isEmpty()) {
            binding.chartVolume.clear();
            return;
        }
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < logs.size(); i++) {
            WorkoutLog log = logs.get(logs.size() - 1 - i); // oldest first
            int totalSets = log.getCompletedSets() == null ? 0 : log.getCompletedSets().stream()
                    .mapToInt(com.gymnaughy.android.model.CompletedSet::getSetsCompleted)
                    .sum();
            entries.add(new Entry(i, totalSets));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Sets completed");
        binding.chartVolume.setData(new LineData(dataSet));
        binding.chartVolume.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
