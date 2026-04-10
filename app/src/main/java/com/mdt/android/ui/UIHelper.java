package com.mdt.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mdt.android.R;

public class UIHelper {
    public static void addSpecRow(Context context, LinearLayout container, String label, String value) {
        View row = LayoutInflater.from(context).inflate(R.layout.item_spec_row, container, false);
        TextView tvLabel = row.findViewById(R.id.tv_label);
        TextView tvValue = row.findViewById(R.id.tv_value);
        tvLabel.setText(label);
        tvValue.setText(value);
        container.addView(row);
    }
}
