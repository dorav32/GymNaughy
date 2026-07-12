package com.gymnaughy.android.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gymnaughy.android.databinding.FragmentEquipmentBinding;
import com.gymnaughy.android.model.Equipment;
import com.gymnaughy.android.viewmodel.OnboardingViewModel;

public class EquipmentFragment extends Fragment {

    private FragmentEquipmentBinding binding;
    private OnboardingViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentEquipmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        binding.cbBodyweight.setOnCheckedChangeListener((btn, checked) ->
                viewModel.toggleEquipment(Equipment.BODYWEIGHT, checked));
        binding.cbDumbbells.setOnCheckedChangeListener((btn, checked) ->
                viewModel.toggleEquipment(Equipment.DUMBBELLS, checked));
        binding.cbBands.setOnCheckedChangeListener((btn, checked) ->
                viewModel.toggleEquipment(Equipment.RESISTANCE_BANDS, checked));
        binding.cbFullGym.setOnCheckedChangeListener((btn, checked) ->
                viewModel.toggleEquipment(Equipment.FULL_GYM, checked));

        binding.btnGeneratePlan.setOnClickListener(v -> {
            if (!viewModel.canSubmit()) {
                binding.tvError.setVisibility(View.VISIBLE);
                return;
            }
            binding.tvError.setVisibility(View.GONE);
            viewModel.submit();
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.tvError.setText(error);
                binding.tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
