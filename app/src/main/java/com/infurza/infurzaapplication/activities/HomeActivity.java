package com.infurza.infurzaapplication.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.infurza.infurzaapplication.R;
import com.infurza.infurzaapplication.fragments.EventsFragment;
import com.infurza.infurzaapplication.fragments.ProfileFragment;
import com.infurza.infurzaapplication.fragments.RecycleFragment;
import com.infurza.infurzaapplication.fragments.ProjectsFragment;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = new RecycleFragment();
        transaction.replace(R.id.fragmentContainer, currentFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_recycle:
                        fragment = new RecycleFragment();
                        loadFragment(fragment);
                        Toast.makeText(HomeActivity.this, "Recycle Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_event:
                        fragment = new EventsFragment();
                        loadFragment(fragment);
                        Toast.makeText(HomeActivity.this, "Events Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_project:
                        fragment = new ProjectsFragment();
                        loadFragment(fragment);
                        Toast.makeText(HomeActivity.this, "Projects Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_profile:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        Toast.makeText(HomeActivity.this, "Profile Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });

        //CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        //layoutParams.setBehavior(new BottomSheetBehavior());
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
