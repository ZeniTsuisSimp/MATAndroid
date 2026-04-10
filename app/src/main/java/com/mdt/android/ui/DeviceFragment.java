package com.mdt.android.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.mdt.android.R;
import com.mdt.android.data.DiagnosticsRepository;
import com.mdt.android.data.Models;

public class DeviceFragment extends Fragment {
    
    private LinearLayout llDeviceIdentity;
    private Button btnRequestPhoneState;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> loadData());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        llDeviceIdentity = view.findViewById(R.id.ll_device_identity);
        btnRequestPhoneState = view.findViewById(R.id.btn_request_phone_state);

        btnRequestPhoneState.setOnClickListener(v -> requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE));

        loadData();
    }

    private void loadData() {
        if (getView() == null) return;
        
        boolean hasPhoneState = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        DiagnosticsRepository repository = new DiagnosticsRepository(requireContext());
        Models.DashboardSnapshot snapshot = repository.loadDashboardData(false, hasPhoneState);

        // Clear existing rows (keep the title and button)
        int childCount = llDeviceIdentity.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = llDeviceIdentity.getChildAt(i);
            if (child != btnRequestPhoneState && child.getId() == View.NO_ID) {
                llDeviceIdentity.removeViewAt(i);
            }
        }

        btnRequestPhoneState.setVisibility(hasPhoneState ? View.GONE : View.VISIBLE);
        
        UIHelper.addSpecRow(requireContext(), llDeviceIdentity, "Device name", snapshot.device.deviceName);
        UIHelper.addSpecRow(requireContext(), llDeviceIdentity, "Hardware", snapshot.device.hardware);
        UIHelper.addSpecRow(requireContext(), llDeviceIdentity, "IMEI / MEID", snapshot.device.phoneIdentifier);

        LinearLayout llSoftware = getView().findViewById(R.id.ll_software_hardware);
        llSoftware.removeAllViews();
        UIHelper.addSpecRow(requireContext(), llSoftware, "Android version", snapshot.device.androidVersion);
        UIHelper.addSpecRow(requireContext(), llSoftware, "Total RAM", snapshot.device.totalRam);

        LinearLayout llBattery = getView().findViewById(R.id.ll_battery_network);
        llBattery.removeAllViews();
        UIHelper.addSpecRow(requireContext(), llBattery, "Battery level", snapshot.battery.percentage);
        UIHelper.addSpecRow(requireContext(), llBattery, "Network type", snapshot.network.networkType);
    }
}
