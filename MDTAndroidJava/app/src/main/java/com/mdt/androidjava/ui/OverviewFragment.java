package com.mdt.androidjava.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mdt.androidjava.databinding.FragmentOverviewBinding;

public class OverviewFragment extends BaseSnapshotFragment {
    private FragmentOverviewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOverviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SnapshotHost host = snapshotHost();
        if (host == null || host.getSnapshot() == null) {
            return;
        }
        binding.heroEyebrow.setText("Generated " + host.getSnapshot().getGeneratedAt());
        binding.metricsContainer.addView(UiHelper.createMetricCard(requireContext(), binding.metricsContainer, "Battery", host.getSnapshot().getBattery().getPercentage()));
        binding.metricsContainer.addView(UiHelper.createMetricCard(requireContext(), binding.metricsContainer, "Connection", host.getSnapshot().getNetwork().getConnection()));
        binding.metricsContainer.addView(UiHelper.createMetricCard(requireContext(), binding.metricsContainer, "RAM", host.getSnapshot().getDevice().getTotalRam()));

        UiHelper.addSpecRow(requireContext(), binding.priorityChecksContainer, "Battery health", host.getSnapshot().getBattery().getHealth());
        UiHelper.addSpecRow(requireContext(), binding.priorityChecksContainer, "Charging state", host.getSnapshot().getBattery().getChargingState());
        UiHelper.addSpecRow(requireContext(), binding.priorityChecksContainer, "Internal free", host.getSnapshot().getStorage().getInternalFree());
        UiHelper.addSpecRow(requireContext(), binding.priorityChecksContainer, "Primary network", host.getSnapshot().getNetwork().getNetworkType());

        UiHelper.addSpecRow(requireContext(), binding.hardwareSnapshotContainer, "Device", host.getSnapshot().getDevice().getDeviceName());
        UiHelper.addSpecRow(requireContext(), binding.hardwareSnapshotContainer, "Android", host.getSnapshot().getDevice().getAndroidVersion());
        UiHelper.addSpecRow(requireContext(), binding.hardwareSnapshotContainer, "CPU", host.getSnapshot().getDevice().getCpuAbi());
        UiHelper.addSpecRow(requireContext(), binding.hardwareSnapshotContainer, "Cameras", host.getSnapshot().getDevice().getCameraCount());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
