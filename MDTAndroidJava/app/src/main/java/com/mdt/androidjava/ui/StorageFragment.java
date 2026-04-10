package com.mdt.androidjava.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mdt.androidjava.databinding.FragmentStorageBinding;

public class StorageFragment extends BaseSnapshotFragment {
    private FragmentStorageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStorageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SnapshotHost host = snapshotHost();
        if (host == null || host.getSnapshot() == null) {
            return;
        }
        binding.callLogPermissionCard.setVisibility(host.hasCallLogPermission() ? View.GONE : View.VISIBLE);
        binding.callLogPermissionAction.setOnClickListener(v -> host.requestCallLogPermission());

        LinearLayout memorySummaryContainer = binding.getRoot().findViewById(com.mdt.androidjava.R.id.memorySummaryContainer);
        LinearLayout callLogsContainer = binding.getRoot().findViewById(com.mdt.androidjava.R.id.callLogsContainer);

        UiHelper.addSpecRow(requireContext(), memorySummaryContainer, "Internal used", host.getSnapshot().getStorage().getInternalUsed());
        UiHelper.addSpecRow(requireContext(), memorySummaryContainer, "Internal free", host.getSnapshot().getStorage().getInternalFree());
        UiHelper.addSpecRow(requireContext(), memorySummaryContainer, "Internal total", host.getSnapshot().getStorage().getInternalTotal());
        UiHelper.addSpecRow(requireContext(), memorySummaryContainer, "External free", host.getSnapshot().getStorage().getExternalFree());
        UiHelper.addSpecRow(requireContext(), memorySummaryContainer, "External total", host.getSnapshot().getStorage().getExternalTotal());

        UiHelper.addSpecRow(requireContext(), callLogsContainer, "Total calls", host.getSnapshot().getStorage().getCallLogSummary().getTotalCalls());
        UiHelper.addSpecRow(requireContext(), callLogsContainer, "Latest activity", host.getSnapshot().getStorage().getCallLogSummary().getLastCall());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
