package com.mdt.android.data

data class DashboardSnapshot(
    val generatedAt: String,
    val device: DeviceProfile,
    val battery: BatteryStatus,
    val network: NetworkStatus,
    val storage: StorageStatus,
    val sensors: List<SensorGroup>
)

data class DeviceProfile(
    val deviceName: String,
    val modelNumber: String,
    val brand: String,
    val product: String,
    val androidVersion: String,
    val kernelVersion: String,
    val cpuAbi: String,
    val totalRam: String,
    val totalStorage: String,
    val hardware: String,
    val board: String,
    val cameraCount: String,
    val phoneIdentifier: String,
    val displayResolution: String = "Unknown",
    val displayDensity: String = "Unknown",
    val refreshRate: String = "Unknown",
    val isRooted: String = "Unknown",
    val widevineLevel: String = "Unknown",
    val biometricSupport: String = "Unknown",
    val processor: String = "Unknown"
)

data class BatteryStatus(
    val percentage: String,
    val health: String,
    val chargingState: String,
    val temperature: String,
    val voltage: String = "Unknown",
    val technology: String = "Unknown"
)

data class NetworkStatus(
    val connection: String,
    val networkType: String,
    val roaming: String,
    val signalHint: String,
    val localIp: String = "Unknown",
    val wifiSsid: String = "Unknown"
)

data class StorageStatus(
    val internalUsed: String,
    val internalFree: String,
    val internalTotal: String,
    val externalFree: String,
    val externalTotal: String,
    val callLogSummary: CallLogSummary
)

data class CallLogSummary(
    val totalCalls: String,
    val lastCall: String
)

data class SensorGroup(
    val title: String,
    val sensors: List<SensorStatus>
)

data class SensorStatus(
    val label: String,
    val availability: String,
    val vendor: String,
    val version: String
)
