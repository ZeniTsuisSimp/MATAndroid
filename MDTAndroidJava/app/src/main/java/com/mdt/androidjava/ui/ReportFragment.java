package com.mdt.androidjava.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mdt.androidjava.data.DiagnosticsRepository;
import com.mdt.androidjava.databinding.FragmentReportBinding;

public class ReportFragment extends BaseSnapshotFragment {
    private FragmentReportBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SnapshotHost host = snapshotHost();
        if (host == null || host.getSnapshot() == null) {
            return;
        }
        String report = new DiagnosticsRepository(requireContext()).buildReport(host.getSnapshot());
        binding.reportText.setText(report);
        binding.shareButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "MDT Android Report");
            intent.putExtra(Intent.EXTRA_TEXT, report);
            startActivity(Intent.createChooser(intent, "Share diagnostic report"));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
