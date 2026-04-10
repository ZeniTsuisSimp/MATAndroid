package com.mdt.androidjava.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mdt.androidjava.databinding.FragmentDeviceBinding;

public class DeviceFragment extends BaseSnapshotFragment {
    private FragmentDeviceBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDeviceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SnapshotHost host = snapshotHost();
        if (host == null || host.getSnapshot() == null) {
            return;
        }
        binding.phonePermissionCard.setVisibility(host.hasPhoneStatePermission() ? View.GONE : View.VISIBLE);
        binding.phonePermissionAction.setOnClickListener(v -> host.requestPhoneStatePermission());

        LinearLayout identityContainer = binding.getRoot().findViewById(com.mdt.androidjava.R.id.identityContainer);
        LinearLayout softwareContainer = binding.getRoot().findViewById(com.mdt.androidjava.R.id.softwareContainer);
        LinearLayout batteryNetworkContainer = binding.getRoot().findViewById(com.mdt.androidjava.R.id.batteryNetworkContainer);
        LinearLayout mediaContainer = binding.getRoot().findViewById(com.mdt.androidjava.R.id.mediaContainer);

        UiHelper.addSpecRow(requireContext(), identityContainer, "Device name", host.getSnapshot().getDevice().getDeviceName());
        UiHelper.addSpecRow(requireContext(), identityContainer, "Model number", host.getSnapshot().getDevice().getModelNumber());
        UiHelper.addSpecRow(requireContext(), identityContainer, "Brand", host.getSnapshot().getDevice().getBrand());
        UiHelper.addSpecRow(requireContext(), identityContainer, "Product", host.getSnapshot().getDevice().getProduct());
        UiHelper.addSpecRow(requireContext(), identityContainer, "Hardware", host.getSnapshot().getDevice().getHardware());
        UiHelper.addSpecRow(requireContext(), identityContainer, "Board", host.getSnapshot().getDevice().getBoard());
        UiHelper.addSpecRow(requireContext(), identityContainer, "IMEI / MEID", host.getSnapshot().getDevice().getPhoneIdentifier());

        UiHelper.addSpecRow(requireContext(), softwareContainer, "Android version", host.getSnapshot().getDevice().getAndroidVersion());
        UiHelper.addSpecRow(requireContext(), softwareContainer, "Kernel version", host.getSnapshot().getDevice().getKernelVersion());
        UiHelper.addSpecRow(requireContext(), softwareContainer, "CPU ABI", host.getSnapshot().getDevice().getCpuAbi());
        UiHelper.addSpecRow(requireContext(), softwareContainer, "Total RAM", host.getSnapshot().getDevice().getTotalRam());
        UiHelper.addSpecRow(requireContext(), softwareContainer, "Internal storage", host.getSnapshot().getDevice().getTotalStorage());

        UiHelper.addSpecRow(requireContext(), batteryNetworkContainer, "Battery level", host.getSnapshot().getBattery().getPercentage());
        UiHelper.addSpecRow(requireContext(), batteryNetworkContainer, "Battery health", host.getSnapshot().getBattery().getHealth());
        UiHelper.addSpecRow(requireContext(), batteryNetworkContainer, "Battery temp", host.getSnapshot().getBattery().getTemperature());
        UiHelper.addSpecRow(requireContext(), batteryNetworkContainer, "Connection", host.getSnapshot().getNetwork().getConnection());
        UiHelper.addSpecRow(requireContext(), batteryNetworkContainer, "Network type", host.getSnapshot().getNetwork().getNetworkType());
        UiHelper.addSpecRow(requireContext(), batteryNetworkContainer, "Roaming", host.getSnapshot().getNetwork().getRoaming());

        String cameraText = "0".equals(host.getSnapshot().getDevice().getCameraCount())
                ? "Not detected"
                : host.getSnapshot().getDevice().getCameraCount() + " camera feature(s)";
        UiHelper.addSpecRow(requireContext(), mediaContainer, "Camera support", cameraText);
        UiHelper.addSpecRow(requireContext(), mediaContainer, "Display test", "Ready for manual visual inspection");
        UiHelper.addSpecRow(requireContext(), mediaContainer, "Sound test", "Ready for manual speaker / mic workflow");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
