package com.mdt.androidjava.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;
import com.mdt.androidjava.R;
import com.mdt.androidjava.data.SensorGroup;
import com.mdt.androidjava.data.SensorStatus;
import com.mdt.androidjava.databinding.FragmentSensorsBinding;

public class SensorsFragment extends BaseSnapshotFragment {
    private FragmentSensorsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSensorsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SnapshotHost host = snapshotHost();
        if (host == null || host.getSnapshot() == null) {
            return;
        }

        for (SensorGroup group : host.getSnapshot().getSensors()) {
            int detected = 0;
            for (SensorStatus sensor : group.getSensors()) {
                if ("Available".equals(sensor.getAvailability())) {
                    detected++;
                }
            }

            MaterialCardView cardView = UiHelper.createSensorCard(
                    requireContext(),
                    binding.sensorGroupsContainer,
                    group.getTitle(),
                    detected + " of " + group.getSensors().size() + " detected"
            );
            LinearLayout details = cardView.findViewById(R.id.sensorDetailsContainer);
            TextView toggle = cardView.findViewById(R.id.sensorToggle);

            for (SensorStatus sensor : group.getSensors()) {
                UiHelper.addSpecRow(
                        requireContext(),
                        details,
                        sensor.getLabel(),
                        sensor.getAvailability() + " | " + sensor.getVendor()
                );
            }

            cardView.setOnClickListener(v -> {
                boolean shouldShow = details.getVisibility() != View.VISIBLE;
                details.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
                toggle.setText(shouldShow ? "Hide" : "Show");
            });
            binding.sensorGroupsContainer.addView(cardView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
