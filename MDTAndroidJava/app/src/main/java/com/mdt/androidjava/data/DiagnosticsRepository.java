package com.mdt.androidjava.data;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.FeatureInfo;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

import com.mdt.androidjava.PermissionUtils;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiagnosticsRepository {
    private final Context context;

    public DiagnosticsRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public DashboardSnapshot loadDashboardData(boolean hasCallLogPermission, boolean hasPhoneStatePermission) {
        return new DashboardSnapshot(
                DateFormat.getDateTimeInstance().format(new Date()),
                loadDeviceProfile(hasPhoneStatePermission),
                loadBatteryStatus(),
                loadNetworkStatus(hasPhoneStatePermission),
                loadStorageStatus(hasCallLogPermission),
                loadSensorGroups()
        );
    }

    public List<SensorGroup> loadSensorGroups() {
        SensorManager sensorManager = ContextCompat.getSystemService(context, SensorManager.class);
        return Arrays.asList(
                new SensorGroup("Motion Sensors", Arrays.asList(
                        sensorStatus(sensorManager, Sensor.TYPE_ACCELEROMETER, "Accelerometer"),
                        sensorStatus(sensorManager, Sensor.TYPE_GYROSCOPE, "Gyroscope"),
                        sensorStatus(sensorManager, Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector"),
                        sensorStatus(sensorManager, Sensor.TYPE_GRAVITY, "Gravity")
                )),
                new SensorGroup("Position Sensors", Arrays.asList(
                        sensorStatus(sensorManager, Sensor.TYPE_PROXIMITY, "Proximity"),
                        sensorStatus(sensorManager, Sensor.TYPE_ORIENTATION, "Orientation"),
                        sensorStatus(sensorManager, Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer")
                )),
                new SensorGroup("Environment Sensors", Arrays.asList(
                        sensorStatus(sensorManager, Sensor.TYPE_LIGHT, "Light"),
                        sensorStatus(sensorManager, Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature"),
                        sensorStatus(sensorManager, Sensor.TYPE_PRESSURE, "Pressure"),
                        sensorStatus(sensorManager, Sensor.TYPE_RELATIVE_HUMIDITY, "Humidity")
                ))
        );
    }

    public String buildReport(DashboardSnapshot snapshot) {
        StringBuilder sensorText = new StringBuilder();
        for (int i = 0; i < snapshot.getSensors().size(); i++) {
            SensorGroup group = snapshot.getSensors().get(i);
            sensorText.append(group.getTitle()).append('\n');
            for (SensorStatus sensor : group.getSensors()) {
                sensorText.append("- ")
                        .append(sensor.getLabel())
                        .append(": ")
                        .append(sensor.getAvailability())
                        .append(" (")
                        .append(sensor.getVendor())
                        .append(")\n");
            }
            if (i < snapshot.getSensors().size() - 1) {
                sensorText.append('\n');
            }
        }

        return "MDT - Android Diagnostic Report\n" +
                "Generated: " + snapshot.getGeneratedAt() + "\n\n" +
                "Device\n" +
                "- Device: " + snapshot.getDevice().getDeviceName() + "\n" +
                "- Model: " + snapshot.getDevice().getModelNumber() + "\n" +
                "- Android: " + snapshot.getDevice().getAndroidVersion() + "\n" +
                "- CPU ABI: " + snapshot.getDevice().getCpuAbi() + "\n" +
                "- RAM: " + snapshot.getDevice().getTotalRam() + "\n" +
                "- Internal Storage: " + snapshot.getDevice().getTotalStorage() + "\n" +
                "- Kernel: " + snapshot.getDevice().getKernelVersion() + "\n" +
                "- Hardware: " + snapshot.getDevice().getHardware() + "\n" +
                "- Board: " + snapshot.getDevice().getBoard() + "\n" +
                "- Phone ID: " + snapshot.getDevice().getPhoneIdentifier() + "\n\n" +
                "Battery\n" +
                "- Level: " + snapshot.getBattery().getPercentage() + "\n" +
                "- Health: " + snapshot.getBattery().getHealth() + "\n" +
                "- State: " + snapshot.getBattery().getChargingState() + "\n" +
                "- Temperature: " + snapshot.getBattery().getTemperature() + "\n\n" +
                "Network\n" +
                "- Connection: " + snapshot.getNetwork().getConnection() + "\n" +
                "- Network Type: " + snapshot.getNetwork().getNetworkType() + "\n" +
                "- Roaming: " + snapshot.getNetwork().getRoaming() + "\n" +
                "- Status: " + snapshot.getNetwork().getSignalHint() + "\n\n" +
                "Storage & Memory\n" +
                "- Internal Used: " + snapshot.getStorage().getInternalUsed() + "\n" +
                "- Internal Free: " + snapshot.getStorage().getInternalFree() + "\n" +
                "- Internal Total: " + snapshot.getStorage().getInternalTotal() + "\n" +
                "- External Free: " + snapshot.getStorage().getExternalFree() + "\n" +
                "- External Total: " + snapshot.getStorage().getExternalTotal() + "\n" +
                "- Call Count: " + snapshot.getStorage().getCallLogSummary().getTotalCalls() + "\n" +
                "- Last Call: " + snapshot.getStorage().getCallLogSummary().getLastCall() + "\n\n" +
                "Sensors\n" + sensorText;
    }

    private DeviceProfile loadDeviceProfile(boolean hasPhoneStatePermission) {
        ActivityManager activityManager = ContextCompat.getSystemService(context, ActivityManager.class);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
        }

        float totalRamGb = memoryInfo.totalMem / 1024f / 1024f / 1024f;
        StatFs internalStorage = new StatFs(Environment.getDataDirectory().getPath());
        float totalStorageGb = internalStorage.getTotalBytes() / 1024f / 1024f / 1024f;

        int cameraCount = 0;
        for (FeatureInfo feature : context.getPackageManager().getSystemAvailableFeatures()) {
            if (feature.name != null && feature.name.contains("camera")) {
                cameraCount++;
            }
        }

        TelephonyManager telephonyManager = ContextCompat.getSystemService(context, TelephonyManager.class);
        String phoneId = null;
        if (hasPhoneStatePermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && telephonyManager != null) {
            try {
                phoneId = telephonyManager.getImei();
                if (phoneId == null || phoneId.isEmpty()) {
                    phoneId = telephonyManager.getMeid();
                }
            } catch (SecurityException ignored) {
                phoneId = null;
            }
        }

        return new DeviceProfile(
                Build.MANUFACTURER + " " + Build.MODEL,
                Build.MODEL,
                Build.BRAND,
                Build.PRODUCT,
                Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")",
                System.getProperty("os.version", ""),
                Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "",
                String.format(Locale.US, "%.1f GB", totalRamGb),
                String.format(Locale.US, "%.1f GB", totalStorageGb),
                Build.HARDWARE,
                Build.BOARD,
                String.valueOf(cameraCount),
                phoneId != null ? phoneId : "Permission required / unavailable"
        );
    }

    private BatteryStatus loadBatteryStatus() {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        int health = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) : -1;
        int status = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
        float temperature = batteryIntent != null
                ? batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
                : 0f;
        int percent = level >= 0 && scale > 0 ? Math.round(level * 100f / scale) : 0;

        return new BatteryStatus(
                percent + "%",
                batteryHealthText(health),
                batteryStatusText(status),
                String.format(Locale.US, "%.1f C", temperature)
        );
    }

    private NetworkStatus loadNetworkStatus(boolean hasPhoneStatePermission) {
        ConnectivityManager connectivityManager = ContextCompat.getSystemService(context, ConnectivityManager.class);
        Network network = connectivityManager != null ? connectivityManager.getActiveNetwork() : null;
        NetworkCapabilities capabilities = connectivityManager != null
                ? connectivityManager.getNetworkCapabilities(network)
                : null;
        TelephonyManager telephonyManager = ContextCompat.getSystemService(context, TelephonyManager.class);

        String transport;
        if (capabilities == null) {
            transport = "Disconnected";
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            transport = "Wi-Fi";
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            transport = "Cellular";
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            transport = "Ethernet";
        } else {
            transport = "Other";
        }

        String networkType = "Permission required";
        String roaming = "Permission required";
        if (hasPhoneStatePermission && telephonyManager != null) {
            try {
                networkType = networkTypeText(telephonyManager.getDataNetworkType());
                roaming = String.valueOf(telephonyManager.isNetworkRoaming());
            } catch (SecurityException ignored) {
                networkType = "Unavailable";
                roaming = "Unknown";
            }
        }

        return new NetworkStatus(
                transport,
                networkType,
                roaming,
                "Disconnected".equals(transport) ? "No active network" : "Connection detected"
        );
    }

    private StorageStatus loadStorageStatus(boolean hasCallLogPermission) {
        StatFs internal = new StatFs(Environment.getDataDirectory().getPath());
        long internalUsed = internal.getTotalBytes() - internal.getAvailableBytes();
        StatFs externalStats = context.getExternalFilesDir(null) != null
                ? new StatFs(context.getExternalFilesDir(null).getPath())
                : null;

        CallLogSummary summary;
        if (hasCallLogPermission && PermissionUtils.hasPermission(context, Manifest.permission.READ_CALL_LOG)) {
            summary = loadCallLogSummary();
        } else {
            summary = new CallLogSummary(
                    "Permission required",
                    "Grant call log access to inspect usage history"
            );
        }

        return new StorageStatus(
                formatGb(internalUsed),
                formatGb(internal.getAvailableBytes()),
                formatGb(internal.getTotalBytes()),
                externalStats != null ? formatGb(externalStats.getAvailableBytes()) : "Unavailable",
                externalStats != null ? formatGb(externalStats.getTotalBytes()) : "Unavailable",
                summary
        );
    }

    private CallLogSummary loadCallLogSummary() {
        String[] projection = new String[]{CallLog.Calls.DATE};
        try (Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
        )) {
            if (cursor != null) {
                int count = cursor.getCount();
                String lastCall = cursor.moveToFirst()
                        ? DateFormat.getDateTimeInstance().format(new Date(cursor.getLong(0)))
                        : "No call history";
                return new CallLogSummary(String.valueOf(count), lastCall);
            }
        } catch (SecurityException ignored) {
        }

        return new CallLogSummary("Unavailable", "Call log could not be read");
    }

    private SensorStatus sensorStatus(SensorManager manager, int type, String label) {
        Sensor sensor = manager != null ? manager.getDefaultSensor(type) : null;
        return new SensorStatus(
                label,
                sensor != null ? "Available" : "Not detected",
                sensor != null ? sensor.getVendor() : "Device not reporting",
                sensor != null ? String.valueOf(sensor.getVersion()) : "-"
        );
    }

    private String formatGb(long bytes) {
        return String.format(Locale.US, "%.1f GB", bytes / 1024f / 1024f / 1024f);
    }

    private String batteryHealthText(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheating";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over-voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Failure";
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "Cold";
            default:
                return "Unknown";
        }
    }

    private String batteryStatusText(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not charging";
            default:
                return "Unknown";
        }
    }

    private String networkTypeText(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G NR";
            default:
                return "Type " + type;
        }
    }
}
