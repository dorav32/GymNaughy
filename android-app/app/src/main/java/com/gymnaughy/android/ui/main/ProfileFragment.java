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

import com.gymnaughy.android.databinding.FragmentProfileBinding;
import com.gymnaughy.android.model.Equipment;
import com.gymnaughy.android.model.User;
import com.gymnaughy.android.ui.auth.LoginActivity;
import com.gymnaughy.android.viewmodel.AuthViewModel;
import com.gymnaughy.android.viewmodel.DashboardViewModel;

import java.util.stream.Collectors;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.start();
        dashboardViewModel.getUser().observe(getViewLifecycleOwner(), this::renderUser);

        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.btnSignOut.setOnClickListener(v -> {
            authViewModel.signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void renderUser(User user) {
        if (user == null) {
            return;
        }
        binding.tvName.setText(user.getDisplayName());
        binding.tvEmail.setText(user.getEmail());
        binding.tvFitnessLevel.setText(user.getFitnessLevel() != null
                ? user.getFitnessLevel().getApiValue() : "-");

        String equipmentSummary = user.getEquipment() == null ? "-" : user.getEquipment().stream()
                .map(Equipment::getDisplayName)
                .collect(Collectors.joining(", "));
        binding.tvEquipment.setText(equipmentSummary);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
