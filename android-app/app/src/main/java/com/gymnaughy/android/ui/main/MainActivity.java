package com.gymnaughy.android.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gymnaughy.android.R;
import com.gymnaughy.android.databinding.ActivityMainBinding;

/**
 * Hosts the four bottom-navigation destinations as fragments swapped in a single
 * container, rather than four separate activities, so the bottom nav bar itself
 * never has to be re-created.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setOnItemSelectedListener(this::onNavItemSelected);

        if (savedInstanceState == null) {
            showFragment(new DashboardFragment());
        }
    }

    private boolean onNavItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_dashboard) {
            showFragment(new DashboardFragment());
            return true;
        } else if (id == R.id.nav_plan) {
            showFragment(new WorkoutPlanFragment());
            return true;
        } else if (id == R.id.nav_progress) {
            showFragment(new ProgressFragment());
            return true;
        } else if (id == R.id.nav_profile) {
            showFragment(new ProfileFragment());
            return true;
        }
        return false;
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentContainer.getId(), fragment)
                .commit();
    }
}
