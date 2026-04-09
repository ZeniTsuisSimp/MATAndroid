package com.mdt.android.data

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaDrm
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.CallLog
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import androidx.biometric.BiometricManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mdt.android.hasPermission
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.DateFormat
import java.util.Date
import java.util.UUID
import kotlin.math.roundToInt

class DiagnosticsRepository(private val context: Context) {

    private val NOTIFICATION_CHANNEL_ID = "battery_alerts"
    private val TEMP_THRESHOLD = 40.0f // Threshold in Celsius

    init {
        createNotificationChannel()
    }

    fun loadDashboardData(
        hasCallLogPermission: Boolean,
        hasPhoneStatePermission: Boolean
    ): DashboardSnapshot {
        val device = loadDeviceProfile(hasPhoneStatePermission)
        val battery = loadBatteryStatus()
        val network = loadNetworkStatus(hasPhoneStatePermission)
        val storage = loadStorageStatus(hasCallLogPermission)
        val sensors = loadSensorGroups()

        return DashboardSnapshot(
            generatedAt = DateFormat.getDateTimeInstance().format(Date()),
            device = device,
            battery = battery,
            network = network,
            storage = storage,
            sensors = sensors
        )
    }

    fun loadSensorGroups(): List<SensorGroup> {
        val sensorManager = ContextCompat.getSystemService(context, SensorManager::class.java)

        return listOf(
            SensorGroup(
                title = "Motion Sensors",
                sensors = listOf(
                    sensorStatus(sensorManager, Sensor.TYPE_ACCELEROMETER, "Accelerometer"),
                    sensorStatus(sensorManager, Sensor.TYPE_GYROSCOPE, "Gyroscope"),
                    sensorStatus(sensorManager, Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector"),
                    sensorStatus(sensorManager, Sensor.TYPE_GRAVITY, "Gravity")
                )
            ),
            SensorGroup(
                title = "Position Sensors",
                sensors = listOf(
                    sensorStatus(sensorManager, Sensor.TYPE_PROXIMITY, "Proximity"),
                    sensorStatus(sensorManager, Sensor.TYPE_ORIENTATION, "Orientation"),
                    sensorStatus(sensorManager, Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer")
                )
            ),
            SensorGroup(
                title = "Environment Sensors",
                sensors = listOf(
                    sensorStatus(sensorManager, Sensor.TYPE_LIGHT, "Light"),
                    sensorStatus(sensorManager, Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature"),
                    sensorStatus(sensorManager, Sensor.TYPE_PRESSURE, "Pressure"),
                    sensorStatus(sensorManager, Sensor.TYPE_RELATIVE_HUMIDITY, "Humidity")
                )
            )
        )
    }

    fun buildReport(snapshot: DashboardSnapshot): String {
        val sensorText = snapshot.sensors.joinToString("\n\n") { group ->
            buildString {
                append(group.title)
                append('\n')
                group.sensors.forEach { sensor ->
                    append("- ${sensor.label}: ${sensor.availability} (${sensor.vendor})")
                    append('\n')
                }
            }
        }

        return """
            MDT - Android Diagnostic Report
            Generated: ${snapshot.generatedAt}

            Device
            - Device: ${snapshot.device.deviceName}
            - Model: ${snapshot.device.modelNumber}
            - Android: ${snapshot.device.androidVersion}
            - Processor: ${snapshot.device.processor}
            - Refresh Rate: ${snapshot.device.refreshRate}
            - Resolution: ${snapshot.device.displayResolution}
            - Widevine: ${snapshot.device.widevineLevel}
            - Rooted: ${snapshot.device.isRooted}
            - Biometrics: ${snapshot.device.biometricSupport}
            - RAM: ${snapshot.device.totalRam}
            - Internal Storage: ${snapshot.device.totalStorage}
            - Phone ID: ${snapshot.device.phoneIdentifier}

            Battery
            - Level: ${snapshot.battery.percentage}
            - Health: ${snapshot.battery.health}
            - State: ${snapshot.battery.chargingState}
            - Temperature: ${snapshot.battery.temperature}
            - Voltage: ${snapshot.battery.voltage}
            - Technology: ${snapshot.battery.technology}

            Network
            - Connection: ${snapshot.network.connection}
            - Network Type: ${snapshot.network.networkType}
            - IP: ${snapshot.network.localIp}
            - Wi-Fi: ${snapshot.network.wifiSsid}
            - Roaming: ${snapshot.network.roaming}

            Storage & Memory
            - Internal Used: ${snapshot.storage.internalUsed}
            - Internal Free: ${snapshot.storage.internalFree}
            - Internal Total: ${snapshot.storage.internalTotal}
            - External Free: ${snapshot.storage.externalFree}
            - External Total: ${snapshot.storage.externalTotal}
            - Call Count: ${snapshot.storage.callLogSummary.totalCalls}

            Sensors
            $sensorText
        """.trimIndent()
    }

    private fun loadDeviceProfile(hasPhoneStatePermission: Boolean): DeviceProfile {
        val activityManager = ContextCompat.getSystemService(context, ActivityManager::class.java)
        val memoryInfo = ActivityManager.MemoryInfo().also { info ->
            activityManager?.getMemoryInfo(info)
        }
        val totalRamGb = memoryInfo.totalMem / 1024f / 1024f / 1024f
        val internalStorage = StatFs(Environment.getDataDirectory().path)
        val totalStorageGb = internalStorage.totalBytes / 1024f / 1024f / 1024f
        val cameraCount = context.packageManager.systemAvailableFeatures.count {
            it.name?.contains("camera") == true
        }

        val telephonyManager = ContextCompat.getSystemService(context, TelephonyManager::class.java)
        val phoneId = when {
            hasPhoneStatePermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                telephonyManager?.imei ?: telephonyManager?.meid
            else -> null
        }

        val metrics = context.resources.displayMetrics
        val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.refreshRate?.toInt() ?: 0
        } else {
            0
        }

        return DeviceProfile(
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
            modelNumber = Build.MODEL,
            brand = Build.BRAND,
            product = Build.PRODUCT,
            androidVersion = "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            kernelVersion = System.getProperty("os.version").orEmpty(),
            cpuAbi = Build.SUPPORTED_ABIS.firstOrNull().orEmpty(),
            totalRam = "${"%.1f".format(totalRamGb)} GB",
            totalStorage = "${"%.1f".format(totalStorageGb)} GB",
            hardware = Build.HARDWARE,
            board = Build.BOARD,
            cameraCount = cameraCount.toString(),
            phoneIdentifier = phoneId ?: "Permission required / unavailable",
            displayResolution = "${metrics.widthPixels} x ${metrics.heightPixels}",
            displayDensity = getDensityString(metrics.densityDpi),
            refreshRate = if (refreshRate > 0) "$refreshRate Hz" else "Unknown",
            isRooted = if (checkRootMethod()) "Yes" else "No",
            widevineLevel = getWidevineLevel(),
            biometricSupport = getBiometricSupport(),
            processor = Build.HARDWARE
        )
    }

    private fun loadBatteryStatus(): BatteryStatus {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val health = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val temperature = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)?.div(10f) ?: 0f
        val voltage = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
        val technology = batteryIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"
        val percent = if (level >= 0 && scale > 0) (level * 100f / scale).roundToInt() else 0

        if (temperature > TEMP_THRESHOLD) {
            sendBatteryNotification(temperature)
        }

        return BatteryStatus(
            percentage = "$percent%",
            health = batteryHealthText(health),
            chargingState = batteryStatusText(status),
            temperature = "${temperature} C",
            voltage = "$voltage mV",
            technology = technology
        )
    }

    private fun loadNetworkStatus(hasPhoneStatePermission: Boolean): NetworkStatus {
        val connectivity = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        val network = connectivity?.activeNetwork
        val capabilities = connectivity?.getNetworkCapabilities(network)
        val telephonyManager = ContextCompat.getSystemService(context, TelephonyManager::class.java)

        val transport = when {
            capabilities == null -> "Disconnected"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Other"
        }

        val networkType = if (hasPhoneStatePermission) {
            telephonyManager?.dataNetworkType?.let(::networkTypeText) ?: "Unavailable"
        } else {
            "Permission required"
        }

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ssid = if (context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            wifiManager.connectionInfo.ssid.removeSurrounding("\"")
        } else "Location Permission Required"

        return NetworkStatus(
            connection = transport,
            networkType = networkType,
            roaming = if (hasPhoneStatePermission) {
                telephonyManager?.isNetworkRoaming?.toString() ?: "Unknown"
            } else {
                "Permission required"
            },
            signalHint = if (transport == "Disconnected") "No active network" else "Connection detected",
            localIp = getLocalIpAddress() ?: "Unknown",
            wifiSsid = if (transport == "Wi-Fi") ssid else "N/A"
        )
    }

    private fun loadStorageStatus(hasCallLogPermission: Boolean): StorageStatus {
        val internal = StatFs(Environment.getDataDirectory().path)
        val internalUsed = internal.totalBytes - internal.availableBytes
        val externalStats = context.getExternalFilesDir(null)?.let { StatFs(it.path) }

        val callSummary = if (hasCallLogPermission && context.hasPermission(Manifest.permission.READ_CALL_LOG)) {
            loadCallLogSummary()
        } else {
            CallLogSummary(
                totalCalls = "Permission required",
                lastCall = "Grant call log access to inspect usage history"
            )
        }

        return StorageStatus(
            internalUsed = formatGb(internalUsed),
            internalFree = formatGb(internal.availableBytes),
            internalTotal = formatGb(internal.totalBytes),
            externalFree = externalStats?.availableBytes?.let(::formatGb) ?: "Unavailable",
            externalTotal = externalStats?.totalBytes?.let(::formatGb) ?: "Unavailable",
            callLogSummary = callSummary
        )
    }

    private fun loadCallLogSummary(): CallLogSummary {
        val projection = arrayOf(CallLog.Calls.DATE)
        context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )?.use { cursor ->
            val count = cursor.count
            val lastCall = if (cursor.moveToFirst()) {
                val date = cursor.getLong(0)
                DateFormat.getDateTimeInstance().format(Date(date))
            } else {
                "No call history"
            }

            return CallLogSummary(
                totalCalls = count.toString(),
                lastCall = lastCall
            )
        }

        return CallLogSummary(
            totalCalls = "Unavailable",
            lastCall = "Call log could not be read"
        )
    }

    private fun sensorStatus(
        manager: SensorManager?,
        type: Int,
        label: String
    ): SensorStatus {
        val sensor = manager?.getDefaultSensor(type)
        return SensorStatus(
            label = label,
            availability = if (sensor != null) "Available" else "Not detected",
            vendor = sensor?.vendor ?: "Device not reporting",
            version = sensor?.version?.toString() ?: "-"
        )
    }

    private fun formatGb(bytes: Long): String {
        return "${"%.1f".format(bytes / 1024f / 1024f / 1024f)} GB"
    }

    private fun getDensityString(density: Int): String = when (density) {
        DisplayMetrics.DENSITY_LOW -> "LDPI"
        DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
        DisplayMetrics.DENSITY_HIGH -> "HDPI"
        DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
        DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
        DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
        else -> "$density DPI"
    }

    private fun checkRootMethod(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun getWidevineLevel(): String {
        val widevineUuid = UUID(-0x121074568629b532L, -0x35b3dce11963283fL)
        return try {
            val mediaDrm = MediaDrm(widevineUuid)
            val level = mediaDrm.getPropertyString("securityLevel")
            mediaDrm.close()
            level
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getBiometricSupport(): String {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> "Available"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No Hardware"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Unavailable"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "Not Enrolled"
            else -> "Unknown"
        }
    }

    private fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is InetAddress) {
                        val ip = address.hostAddress
                        if (!ip.contains(":")) return ip // IPv4 only for simplicity
                    }
                }
            }
        } catch (e: Exception) {}
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Battery Alerts"
            val descriptionText = "Notifications for high battery temperature"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendBatteryNotification(temp: Float) {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Battery Overheat Warning")
            .setContentText("Your battery temperature is ${temp}°C. Please consider cooling it down.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1001, builder.build())
    }

    private fun batteryHealthText(health: Int): String = when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over-voltage"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        else -> "Unknown"
    }

    private fun batteryStatusText(status: Int): String = when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
        else -> "Unknown"
    }

    private fun networkTypeText(type: Int): String = when (type) {
        TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
        TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
        TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
        TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
        TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+"
        TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
        TelephonyManager.NETWORK_TYPE_NR -> "5G NR"
        else -> "Type $type"
    }
}
