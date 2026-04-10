package com.mdt.androidjava;

import android.Manifest;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.mdt.androidjava.data.DashboardSnapshot;
import com.mdt.androidjava.data.DiagnosticsRepository;
import com.mdt.androidjava.databinding.ActivityMainBinding;
import com.mdt.androidjava.ui.DeviceFragment;
import com.mdt.androidjava.ui.OverviewFragment;
import com.mdt.androidjava.ui.ReportFragment;
import com.mdt.androidjava.ui.SensorsFragment;
import com.mdt.androidjava.ui.SnapshotHost;
import com.mdt.androidjava.ui.StorageFragment;

public class MainActivity extends AppCompatActivity implements SnapshotHost {
    private ActivityMainBinding binding;
    private DiagnosticsRepository repository;
    private DashboardSnapshot snapshot;
    private boolean hasCallLogPermission;
    private boolean hasPhoneStatePermission;
    private ActivityResultLauncher<String> callLogPermissionLauncher;
    private ActivityResultLauncher<String> phoneStatePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            binding.bottomNavigation.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        repository = new DiagnosticsRepository(this);
        hasCallLogPermission = PermissionUtils.hasPermission(this, Manifest.permission.READ_CALL_LOG);
        hasPhoneStatePermission = PermissionUtils.hasPermission(this, Manifest.permission.READ_PHONE_STATE);

        callLogPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    hasCallLogPermission = granted;
                    refreshSnapshot();
                }
        );
        phoneStatePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    hasPhoneStatePermission = granted;
                    refreshSnapshot();
                }
        );

        refreshSnapshot();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = createFragment(item.getItemId());
            if (fragment == null) {
                return false;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        });

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.menu_overview);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean latestCallLog = PermissionUtils.hasPermission(this, Manifest.permission.READ_CALL_LOG);
        boolean latestPhoneState = PermissionUtils.hasPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (latestCallLog != hasCallLogPermission || latestPhoneState != hasPhoneStatePermission) {
            hasCallLogPermission = latestCallLog;
            hasPhoneStatePermission = latestPhoneState;
            refreshSnapshot();
        }
    }

    private void refreshSnapshot() {
        snapshot = repository.loadDashboardData(hasCallLogPermission, hasPhoneStatePermission);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, createFragment(binding.bottomNavigation.getSelectedItemId()))
                    .commitAllowingStateLoss();
        }
    }

    private Fragment createFragment(int itemId) {
        if (itemId == R.id.menu_overview) {
            return new OverviewFragment();
        }
        if (itemId == R.id.menu_device) {
            return new DeviceFragment();
        }
        if (itemId == R.id.menu_sensors) {
            return new SensorsFragment();
        }
        if (itemId == R.id.menu_storage) {
            return new StorageFragment();
        }
        if (itemId == R.id.menu_report) {
            return new ReportFragment();
        }
        return null;
    }

    @Override
    public DashboardSnapshot getSnapshot() {
        return snapshot;
    }

    @Override
    public void requestCallLogPermission() {
        callLogPermissionLauncher.launch(Manifest.permission.READ_CALL_LOG);
    }

    @Override
    public void requestPhoneStatePermission() {
        phoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
    }

    @Override
    public boolean hasCallLogPermission() {
        return hasCallLogPermission;
    }

    @Override
    public boolean hasPhoneStatePermission() {
        return hasPhoneStatePermission;
    }
}
