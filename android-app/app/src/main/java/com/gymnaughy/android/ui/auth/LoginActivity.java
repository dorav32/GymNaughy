package com.gymnaughy.android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gymnaughy.android.databinding.ActivityLoginBinding;
import com.gymnaughy.android.model.User;
import com.gymnaughy.android.ui.main.MainActivity;
import com.gymnaughy.android.ui.onboarding.OnboardingActivity;
import com.gymnaughy.android.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (viewModel.isSignedIn()) {
            goToMain();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        viewModel.getLoading().observe(this, loading ->
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));

        viewModel.getUser().observe(this, this::onAuthenticated);

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                binding.tvError.setText(error);
                binding.tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void attemptLogin() {
        String email = binding.emailInput.getText() != null ? binding.emailInput.getText().toString().trim() : "";
        String password = binding.passwordInput.getText() != null ? binding.passwordInput.getText().toString() : "";

        if (email.isEmpty() || password.isEmpty()) {
            binding.tvError.setText("Enter your email and password.");
            binding.tvError.setVisibility(View.VISIBLE);
            return;
        }

        binding.tvError.setVisibility(View.GONE);
        viewModel.signIn(email, password);
    }

    private void onAuthenticated(User user) {
        if (user == null) {
            return;
        }
        if (user.hasCompletedOnboarding()) {
            goToMain();
        } else {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
        }
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
