package com.gymnaughy.android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gymnaughy.android.databinding.ActivityRegisterBinding;
import com.gymnaughy.android.ui.onboarding.OnboardingActivity;
import com.gymnaughy.android.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> attemptRegister());
        binding.tvGoToLogin.setOnClickListener(v -> finish());

        viewModel.getLoading().observe(this, loading ->
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));

        // A freshly-registered user has never completed onboarding by definition.
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                startActivity(new Intent(this, OnboardingActivity.class));
                finish();
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                binding.tvError.setText(error);
                binding.tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void attemptRegister() {
        String email = binding.emailInput.getText() != null ? binding.emailInput.getText().toString().trim() : "";
        String password = binding.passwordInput.getText() != null ? binding.passwordInput.getText().toString() : "";
        String confirm = binding.confirmPasswordInput.getText() != null
                ? binding.confirmPasswordInput.getText().toString() : "";

        if (email.isEmpty() || password.isEmpty()) {
            showError("Enter your email and password.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords don't match.");
            return;
        }

        binding.tvError.setVisibility(View.GONE);
        viewModel.register(email, password);
    }

    private void showError(String message) {
        binding.tvError.setText(message);
        binding.tvError.setVisibility(View.VISIBLE);
    }
}
