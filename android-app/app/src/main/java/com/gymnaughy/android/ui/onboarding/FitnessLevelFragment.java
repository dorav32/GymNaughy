package com.gymnaughy.android.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.gymnaughy.android.databinding.FragmentFitnessLevelBinding;
import com.gymnaughy.android.model.FitnessLevel;
import com.gymnaughy.android.viewmodel.OnboardingViewModel;

public class FitnessLevelFragment extends Fragment {

    private FragmentFitnessLevelBinding binding;
    private OnboardingViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentFitnessLevelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        binding.btnContinue.setOnClickListener(v -> {
            FitnessLevel level = selectedLevel();
            if (level == null) {
                binding.tvError.setVisibility(View.VISIBLE);
                return;
            }
            viewModel.selectFitnessLevel(level);
            ((OnboardingActivity) requireActivity()).showEquipmentStep();
        });
    }

    @Nullable
    private FitnessLevel selectedLevel() {
        int checkedId = binding.radioGroupLevel.getCheckedRadioButtonId();
        if (checkedId == binding.rbBeginner.getId()) {
            return FitnessLevel.BEGINNER;
        } else if (checkedId == binding.rbIntermediate.getId()) {
            return FitnessLevel.INTERMEDIATE;
        } else if (checkedId == binding.rbAdvanced.getId()) {
            return FitnessLevel.ADVANCED;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
