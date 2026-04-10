package com.mdt.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mdt.android.R;
import com.mdt.android.data.DiagnosticsRepository;
import com.mdt.android.data.Models;

public class ReportFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DiagnosticsRepository repository = new DiagnosticsRepository(requireContext());
        Models.DashboardSnapshot snapshot = repository.loadDashboardData(false, false);
        
        String report = repository.buildReport(snapshot);
        
        TextView tvReportContent = view.findViewById(R.id.tv_report_content);
        tvReportContent.setText(report);
        
        Button btnShare = view.findViewById(R.id.btn_share_report);
        btnShare.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "MDT Android Report");
            intent.putExtra(Intent.EXTRA_TEXT, report);
            startActivity(Intent.createChooser(intent, "Share diagnostic report"));
        });
    }
}
