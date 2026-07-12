package com.gymnaughy.android.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gymnaughy.android.databinding.ActivityOnboardingBinding;
import com.gymnaughy.android.ui.main.MainActivity;
import com.gymnaughy.android.util.PreferenceManager;
import com.gymnaughy.android.viewmodel.OnboardingViewModel;

/**
 * Hosts the two onboarding steps as fragments sharing one activity-scoped
 * {@link OnboardingViewModel}, so the fitness level picked on step 1 is still
 * available when step 2 submits both selections together.
 */
public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private OnboardingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentContainer.getId(), new FitnessLevelFragment())
                    .commit();
        }

        viewModel.getGeneratedPlan().observe(this, plan -> {
            if (plan != null) {
                new PreferenceManager(this).setOnboardingCompleted(true);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    public void showEquipmentStep() {
        getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentContainer.getId(), new EquipmentFragment())
                .addToBackStack(null)
                .commit();
    }
}
