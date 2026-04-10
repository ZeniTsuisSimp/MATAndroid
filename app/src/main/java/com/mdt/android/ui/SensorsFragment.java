package com.mdt.android.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import com.mdt.android.R;
import com.mdt.android.data.DiagnosticsRepository;
import com.mdt.android.data.Models;
import java.util.List;

public class SensorsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensors, container, false);
        
        DiagnosticsRepository repository = new DiagnosticsRepository(requireContext());
        List<Models.SensorGroup> groups = repository.loadSensorGroups();
        
        LinearLayout llSensorGroups = view.findViewById(R.id.ll_sensor_groups);
        
        for (Models.SensorGroup group : groups) {
            MaterialCardView card = new MaterialCardView(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 48); // 16dp
            card.setLayoutParams(params);
            card.setCardBackgroundColor(Color.parseColor("#FDF8F1"));
            card.setRadius(72f); // 24dp
            
            LinearLayout cardContent = new LinearLayout(requireContext());
            cardContent.setOrientation(LinearLayout.VERTICAL);
            cardContent.setPadding(60, 60, 60, 60); // 20dp
            
            TextView tvTitle = new TextView(requireContext());
            tvTitle.setText(group.title);
            tvTitle.setTextSize(20f);
            tvTitle.setTextColor(Color.parseColor("#20352B"));
            cardContent.addView(tvTitle);
            
            for (Models.SensorStatus status : group.sensors) {
                UIHelper.addSpecRow(requireContext(), cardContent, status.label, status.availability + " | " + status.vendor);
            }
            
            card.addView(cardContent);
            llSensorGroups.addView(card);
        }
        
        return view;
    }
}
