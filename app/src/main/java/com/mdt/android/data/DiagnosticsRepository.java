package com.mdt.android.data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaDrm;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.biometric.BiometricManager;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

public class DiagnosticsRepository {

    private final Context context;
    private static final String NOTIFICATION_CHANNEL_ID = "battery_alerts";
    private static final float TEMP_THRESHOLD = 40.0f; // Celsius

    public DiagnosticsRepository(Context context) {
        this.context = context.getApplicationContext();
        createNotificationChannel();
    }

    public Models.DashboardSnapshot loadDashboardData(boolean hasCallLogPermission, boolean hasPhoneStatePermission) {
        Models.DeviceProfile device = loadDeviceProfile(hasPhoneStatePermission);
        Models.BatteryStatus battery = loadBatteryStatus();
        Models.NetworkStatus network = loadNetworkStatus(hasPhoneStatePermission);
        Models.StorageStatus storage = loadStorageStatus(hasCallLogPermission);
        List<Models.SensorGroup> sensors = loadSensorGroups();

        return new Models.DashboardSnapshot(
                DateFormat.getDateTimeInstance().format(new Date()),
                device,
                battery,
                network,
                storage,
                sensors
        );
    }

    public List<Models.SensorGroup> loadSensorGroups() {
        SensorManager sensorManager = ContextCompat.getSystemService(context, SensorManager.class);

        List<Models.SensorStatus> motionSensors = new ArrayList<>();
        motionSensors.add(sensorStatus(sensorManager, Sensor.TYPE_ACCELEROMETER, "Accelerometer"));
        motionSensors.add(sensorStatus(sensorManager, Sensor.TYPE_GYROSCOPE, "Gyroscope"));
        motionSensors.add(sensorStatus(sensorManager, Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector"));
        motionSensors.add(sensorStatus(sensorManager, Sensor.TYPE_GRAVITY, "Gravity"));

        List<Models.SensorStatus> positionSensors = new ArrayList<>();
        positionSensors.add(sensorStatus(sensorManager, Sensor.TYPE_PROXIMITY, "Proximity"));
        positionSensors.add(sensorStatus(sensorManager, Sensor.TYPE_ORIENTATION, "Orientation"));
        positionSensors.add(sensorStatus(sensorManager, Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer"));

        List<Models.SensorStatus> envSensors = new ArrayList<>();
        envSensors.add(sensorStatus(sensorManager, Sensor.TYPE_LIGHT, "Light"));
        envSensors.add(sensorStatus(sensorManager, Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature"));
        envSensors.add(sensorStatus(sensorManager, Sensor.TYPE_PRESSURE, "Pressure"));
        envSensors.add(sensorStatus(sensorManager, Sensor.TYPE_RELATIVE_HUMIDITY, "Humidity"));

        List<Models.SensorGroup> groups = new ArrayList<>();
        groups.add(new Models.SensorGroup("Motion Sensors", motionSensors));
        groups.add(new Models.SensorGroup("Position Sensors", positionSensors));
        groups.add(new Models.SensorGroup("Environment Sensors", envSensors));

        return groups;
    }

    public String buildReport(Models.DashboardSnapshot snapshot) {
        StringBuilder sensorText = new StringBuilder();
        for (Models.SensorGroup group : snapshot.sensors) {
            sensorText.append(group.title).append("\n");
            for (Models.SensorStatus sensor : group.sensors) {
                sensorText.append("- ").append(sensor.label).append(": ")
                          .append(sensor.availability).append(" (")
                          .append(sensor.vendor).append(")\n");
            }
            sensorText.append("\n");
        }

        return "MDT - Android Diagnostic Report\n" +
                "Generated: " + snapshot.generatedAt + "\n\n" +
                "Device\n" +
                "- Device: " + snapshot.device.deviceName + "\n" +
                "- Model: " + snapshot.device.modelNumber + "\n" +
                "- Android: " + snapshot.device.androidVersion + "\n" +
                "- Processor: " + snapshot.device.processor + "\n" +
                "- Refresh Rate: " + snapshot.device.refreshRate + "\n" +
                "- Resolution: " + snapshot.device.displayResolution + "\n" +
                "- Widevine: " + snapshot.device.widevineLevel + "\n" +
                "- Rooted: " + snapshot.device.isRooted + "\n" +
                "- Biometrics: " + snapshot.device.biometricSupport + "\n" +
                "- RAM: " + snapshot.device.totalRam + "\n" +
                "- Internal Storage: " + snapshot.device.totalStorage + "\n" +
                "- Phone ID: " + snapshot.device.phoneIdentifier + "\n\n" +
                "Battery\n" +
                "- Level: " + snapshot.battery.percentage + "\n" +
                "- Health: " + snapshot.battery.health + "\n" +
                "- State: " + snapshot.battery.chargingState + "\n" +
                "- Temperature: " + snapshot.battery.temperature + "\n" +
                "- Voltage: " + snapshot.battery.voltage + "\n" +
                "- Technology: " + snapshot.battery.technology + "\n\n" +
                "Network\n" +
                "- Connection: " + snapshot.network.connection + "\n" +
                "- Network Type: " + snapshot.network.networkType + "\n" +
                "- IP: " + snapshot.network.localIp + "\n" +
                "- Wi-Fi: " + snapshot.network.wifiSsid + "\n" +
                "- Roaming: " + snapshot.network.roaming + "\n\n" +
                "Storage & Memory\n" +
                "- Internal Used: " + snapshot.storage.internalUsed + "\n" +
                "- Internal Free: " + snapshot.storage.internalFree + "\n" +
                "- Internal Total: " + snapshot.storage.internalTotal + "\n" +
                "- External Free: " + snapshot.storage.externalFree + "\n" +
                "- External Total: " + snapshot.storage.externalTotal + "\n" +
                "- Call Count: " + snapshot.storage.callLogSummary.totalCalls + "\n\n" +
                "Sensors\n" +
                sensorText.toString();
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private Models.DeviceProfile loadDeviceProfile(boolean hasPhoneStatePermission) {
        ActivityManager activityManager = ContextCompat.getSystemService(context, ActivityManager.class);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
        }
        float totalRamGb = memoryInfo.totalMem / 1024f / 1024f / 1024f;

        StatFs internalStorage = new StatFs(Environment.getDataDirectory().getPath());
        float totalStorageGb = (float) internalStorage.getTotalBytes() / 1024f / 1024f / 1024f;

        int cameraCount = 0;
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            cameraCount = 1; // Simplify check for UI
        }

        TelephonyManager telephonyManager = ContextCompat.getSystemService(context, TelephonyManager.class);
        String phoneId = null;

        if (hasPhoneStatePermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {
                if (telephonyManager != null) {
                    phoneId = telephonyManager.getImei();
                    if (phoneId == null) phoneId = telephonyManager.getMeid();
                }
            } catch (SecurityException ignored) {
            }
        }

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int refreshRate = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                Display display = wm.getDefaultDisplay();
                if (display != null) {
                    refreshRate = (int) display.getRefreshRate();
                }
            }
        }

        return new Models.DeviceProfile(
                Build.MANUFACTURER + " " + Build.MODEL,
                Build.MODEL,
                Build.BRAND,
                Build.PRODUCT,
                Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")",
                System.getProperty("os.version"),
                Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "",
                String.format("%.1f GB", totalRamGb),
                String.format("%.1f GB", totalStorageGb),
                Build.HARDWARE,
                Build.BOARD,
                String.valueOf(cameraCount),
                phoneId != null ? phoneId : "Permission required / unavailable",
                metrics.widthPixels + " x " + metrics.heightPixels,
                getDensityString(metrics.densityDpi),
                refreshRate > 0 ? refreshRate + " Hz" : "Unknown",
                checkRootMethod() ? "Yes" : "No",
                getWidevineLevel(),
                getBiometricSupport(),
                Build.HARDWARE
        );
    }

    private Models.BatteryStatus loadBatteryStatus() {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        int health = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) : -1;
        int status = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
        float temperature = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f : 0f;
        int voltage = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) : 0;
        String technology = batteryIntent != null ? batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) : "Unknown";

        int percent = (level >= 0 && scale > 0) ? Math.round((level * 100f) / scale) : 0;

        if (temperature > TEMP_THRESHOLD) {
            sendBatteryNotification(temperature);
        }

        return new Models.BatteryStatus(
                percent + "%",
                batteryHealthText(health),
                batteryStatusText(status),
                temperature + " C",
                voltage + " mV",
                technology != null ? technology : "Unknown"
        );
    }

    @SuppressLint("MissingPermission")
    private Models.NetworkStatus loadNetworkStatus(boolean hasPhoneStatePermission) {
        ConnectivityManager connectivity = ContextCompat.getSystemService(context, ConnectivityManager.class);
        Network network = connectivity != null ? connectivity.getActiveNetwork() : null;
        NetworkCapabilities capabilities = connectivity != null ? connectivity.getNetworkCapabilities(network) : null;
        TelephonyManager telephonyManager = ContextCompat.getSystemService(context, TelephonyManager.class);

        String transport = "Disconnected";
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) transport = "Wi-Fi";
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) transport = "Cellular";
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) transport = "Ethernet";
            else transport = "Other";
        }

        String networkType = "Permission required";
        if (hasPhoneStatePermission && telephonyManager != null) {
            networkType = networkTypeText(telephonyManager.getDataNetworkType());
        }

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ssid = "N/A";
        if (transport.equals("Wi-Fi") && wifiManager != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                WifiInfo info = wifiManager.getConnectionInfo();
                if (info != null && info.getSSID() != null) {
                    ssid = info.getSSID().replace("\"", "");
                }
            } else {
                ssid = "Location Permission Required";
            }
        }

        String roaming = "Permission required";
        if (hasPhoneStatePermission && telephonyManager != null) {
            roaming = String.valueOf(telephonyManager.isNetworkRoaming());
        }

        return new Models.NetworkStatus(
                transport,
                networkType,
                roaming,
                transport.equals("Disconnected") ? "No active network" : "Connection detected",
                getLocalIpAddress(),
                ssid
        );
    }

    private Models.StorageStatus loadStorageStatus(boolean hasCallLogPermission) {
        StatFs internal = new StatFs(Environment.getDataDirectory().getPath());
        long internalUsedBytes = internal.getTotalBytes() - internal.getAvailableBytes();
        
        File externalDir = context.getExternalFilesDir(null);
        StatFs externalStats = externalDir != null ? new StatFs(externalDir.getPath()) : null;

        Models.CallLogSummary callSummary;
        if (hasCallLogPermission && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            callSummary = loadCallLogSummary();
        } else {
            callSummary = new Models.CallLogSummary("Permission required", "Grant call log access to inspect usage history");
        }

        return new Models.StorageStatus(
                formatGb(internalUsedBytes),
                formatGb(internal.getAvailableBytes()),
                formatGb(internal.getTotalBytes()),
                externalStats != null ? formatGb(externalStats.getAvailableBytes()) : "Unavailable",
                externalStats != null ? formatGb(externalStats.getTotalBytes()) : "Unavailable",
                callSummary
        );
    }

    private Models.CallLogSummary loadCallLogSummary() {
        String[] projection = { CallLog.Calls.DATE };
        try (Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                CallLog.Calls.DATE + " DESC")) {
            
            if (cursor != null) {
                int count = cursor.getCount();
                String lastCall = "No call history";
                if (cursor.moveToFirst()) {
                    long date = cursor.getLong(0);
                    lastCall = DateFormat.getDateTimeInstance().format(new Date(date));
                }
                return new Models.CallLogSummary(String.valueOf(count), lastCall);
            }
        } catch (Exception ignored) {}
        return new Models.CallLogSummary("Unavailable", "Call log could not be read");
    }

    private Models.SensorStatus sensorStatus(SensorManager manager, int type, String label) {
        Sensor sensor = manager != null ? manager.getDefaultSensor(type) : null;
        return new Models.SensorStatus(
                label,
                sensor != null ? "Available" : "Not detected",
                sensor != null ? sensor.getVendor() : "Device not reporting",
                sensor != null ? String.valueOf(sensor.getVersion()) : "-"
        );
    }

    private String formatGb(long bytes) {
        return String.format("%.1f GB", bytes / 1024f / 1024f / 1024f);
    }

    private String getDensityString(int density) {
        switch (density) {
            case DisplayMetrics.DENSITY_LOW: return "LDPI";
            case DisplayMetrics.DENSITY_MEDIUM: return "MDPI";
            case DisplayMetrics.DENSITY_HIGH: return "HDPI";
            case DisplayMetrics.DENSITY_XHIGH: return "XHDPI";
            case DisplayMetrics.DENSITY_XXHIGH: return "XXHDPI";
            case DisplayMetrics.DENSITY_XXXHIGH: return "XXXHDPI";
            default: return density + " DPI";
        }
    }

    private boolean checkRootMethod() {
        String[] paths = {
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su"
        };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private String getWidevineLevel() {
        UUID widevineUuid = new UUID(-0x121074568629b532L, -0x35b3dce11963283fL);
        try {
            MediaDrm mediaDrm = new MediaDrm(widevineUuid);
            String level = mediaDrm.getPropertyString("securityLevel");
            mediaDrm.close();
            return level;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getBiometricSupport() {
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS: return "Available";
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE: return "No Hardware";
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE: return "Unavailable";
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED: return "Not Enrolled";
            default: return "Unknown";
        }
    }

    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress()) {
                        String ip = address.getHostAddress();
                        if (ip != null && !ip.contains(":")) return ip; 
                    }
                }
            }
        } catch (Exception ignored) {}
        return "Unknown";
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Battery Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for high battery temperature");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendBatteryNotification(float temp) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Battery Overheat Warning")
                .setContentText("Your battery temperature is " + temp + "°C. Please consider cooling it down.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) manager.notify(1001, builder.build());
    }

    private String batteryHealthText(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD: return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return "Overheating";
            case BatteryManager.BATTERY_HEALTH_DEAD: return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return "Over-voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "Failure";
            case BatteryManager.BATTERY_HEALTH_COLD: return "Cold";
            default: return "Unknown";
        }
    }

    private String batteryStatusText(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING: return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING: return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL: return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING: return "Not charging";
            default: return "Unknown";
        }
    }

    private String networkTypeText(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP: return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_LTE: return "LTE";
            case TelephonyManager.NETWORK_TYPE_NR: return "5G NR";
            default: return "Type " + type;
        }
    }
}
