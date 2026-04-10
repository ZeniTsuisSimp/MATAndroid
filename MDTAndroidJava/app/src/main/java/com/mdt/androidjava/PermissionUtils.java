package com.mdt.androidjava;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public final class PermissionUtils {
    private PermissionUtils() {
    }

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
