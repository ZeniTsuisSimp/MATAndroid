package com.mdt.androidjava.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.mdt.androidjava.R;

public final class UiHelper {
    private UiHelper() {
    }

    public static void addSpecRow(Context context, LinearLayout container, String label, String value) {
        View row = LayoutInflater.from(context).inflate(R.layout.item_spec_row, container, false);
        ((TextView) row.findViewById(R.id.specLabel)).setText(label);
        ((TextView) row.findViewById(R.id.specValue)).setText(value);
        container.addView(row);
    }

    public static View createMetricCard(Context context, LinearLayout parent, String label, String value) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_metric_card, parent, false);
        ((TextView) view.findViewById(R.id.metricLabel)).setText(label);
        ((TextView) view.findViewById(R.id.metricValue)).setText(value);
        return view;
    }

    public static MaterialCardView createSensorCard(Context context, LinearLayout parent, String title, String subtitle) {
        MaterialCardView view = (MaterialCardView) LayoutInflater.from(context)
                .inflate(R.layout.item_sensor_group, parent, false);
        ((TextView) view.findViewById(R.id.sensorGroupTitle)).setText(title);
        ((TextView) view.findViewById(R.id.sensorGroupSummary)).setText(subtitle);
        return view;
    }
}
