package com.mdt.android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mdt.android.ui.OverviewFragment;
import com.mdt.android.ui.DeviceFragment;
import com.mdt.android.ui.SensorsFragment;
import com.mdt.android.ui.StorageFragment;
import com.mdt.android.ui.ReportFragment;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_overview) selectedFragment = new OverviewFragment();
            else if (id == R.id.nav_device) selectedFragment = new DeviceFragment();
            else if (id == R.id.nav_sensors) selectedFragment = new SensorsFragment();
            else if (id == R.id.nav_storage) selectedFragment = new StorageFragment();
            else if (id == R.id.nav_report) selectedFragment = new ReportFragment();
            
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OverviewFragment()).commit();
        }
    }
}
