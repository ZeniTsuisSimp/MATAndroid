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

public class StorageFragment extends Fragment {

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> loadData()
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_storage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnRequestCallLog = view.findViewById(R.id.btn_request_call_logs);
        btnRequestCallLog.setOnClickListener(v -> requestPermissionLauncher.launch(Manifest.permission.READ_CALL_LOG));

        loadData();
    }

    private void loadData() {
        if (getView() == null) return;

        boolean hasCallLog = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
        DiagnosticsRepository repository = new DiagnosticsRepository(requireContext());
        Models.DashboardSnapshot snapshot = repository.loadDashboardData(hasCallLog, false);

        LinearLayout llMemory = getView().findViewById(R.id.ll_memory_summary);
        llMemory.removeAllViews();
        UIHelper.addSpecRow(requireContext(), llMemory, "Internal used", snapshot.storage.internalUsed);
        UIHelper.addSpecRow(requireContext(), llMemory, "Internal free", snapshot.storage.internalFree);

        LinearLayout llCallLogs = getView().findViewById(R.id.ll_call_logs);
        Button btnRequestCallLog = getView().findViewById(R.id.btn_request_call_logs);
        btnRequestCallLog.setVisibility(hasCallLog ? View.GONE : View.VISIBLE);
        
        int childCount = llCallLogs.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = llCallLogs.getChildAt(i);
            if (child != btnRequestCallLog && child.getId() == View.NO_ID) {
                llCallLogs.removeViewAt(i);
            }
        }
        
        UIHelper.addSpecRow(requireContext(), llCallLogs, "Total calls", snapshot.storage.callLogSummary.totalCalls);
        UIHelper.addSpecRow(requireContext(), llCallLogs, "Latest activity", snapshot.storage.callLogSummary.lastCall);
    }
}
