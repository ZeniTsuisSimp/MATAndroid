package com.mdt.androidjava.ui;

import com.mdt.androidjava.data.DashboardSnapshot;

public interface SnapshotHost {
    DashboardSnapshot getSnapshot();

    void requestCallLogPermission();

    void requestPhoneStatePermission();

    boolean hasCallLogPermission();

    boolean hasPhoneStatePermission();
}
