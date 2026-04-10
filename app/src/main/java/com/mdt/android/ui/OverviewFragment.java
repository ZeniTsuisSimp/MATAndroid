package com.mdt.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mdt.android.R;
import com.mdt.android.data.DiagnosticsRepository;
import com.mdt.android.data.Models;

public class OverviewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        
        DiagnosticsRepository repository = new DiagnosticsRepository(requireContext());
        Models.DashboardSnapshot snapshot = repository.loadDashboardData(false, false);
        
        TextView tvEyebrow = view.findViewById(R.id.tv_overview_eyebrow);
        tvEyebrow.setText("Generated " + snapshot.generatedAt);
        
        LinearLayout llPriority = view.findViewById(R.id.ll_priority_checks);
        UIHelper.addSpecRow(requireContext(), llPriority, "Battery health", snapshot.battery.health);
        UIHelper.addSpecRow(requireContext(), llPriority, "Charging state", snapshot.battery.chargingState);
        UIHelper.addSpecRow(requireContext(), llPriority, "Internal free", snapshot.storage.internalFree);
        UIHelper.addSpecRow(requireContext(), llPriority, "Primary network", snapshot.network.networkType);
        
        LinearLayout llHardware = view.findViewById(R.id.ll_hardware_snapshot);
        UIHelper.addSpecRow(requireContext(), llHardware, "Device", snapshot.device.deviceName);
        UIHelper.addSpecRow(requireContext(), llHardware, "Android", snapshot.device.androidVersion);
        UIHelper.addSpecRow(requireContext(), llHardware, "CPU", snapshot.device.cpuAbi);
        UIHelper.addSpecRow(requireContext(), llHardware, "Cameras", snapshot.device.cameraCount);
        
        return view;
    }
}
