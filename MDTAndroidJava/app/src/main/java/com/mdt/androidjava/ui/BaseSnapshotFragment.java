package com.mdt.androidjava.ui;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseSnapshotFragment extends Fragment {
    @Nullable
    protected SnapshotHost snapshotHost() {
        Context context = getContext();
        if (context instanceof SnapshotHost) {
            return (SnapshotHost) context;
        }
        return null;
    }
}
