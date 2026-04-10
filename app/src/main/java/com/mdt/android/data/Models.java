package com.mdt.android.data;

import java.util.List;

public class Models {

    public static class DashboardSnapshot {
        public String generatedAt;
        public DeviceProfile device;
        public BatteryStatus battery;
        public NetworkStatus network;
        public StorageStatus storage;
        public List<SensorGroup> sensors;

        public DashboardSnapshot(String generatedAt, DeviceProfile device, BatteryStatus battery,
                                 NetworkStatus network, StorageStatus storage, List<SensorGroup> sensors) {
            this.generatedAt = generatedAt;
            this.device = device;
            this.battery = battery;
            this.network = network;
            this.storage = storage;
            this.sensors = sensors;
        }
    }

    public static class DeviceProfile {
        public String deviceName;
        public String modelNumber;
        public String brand;
        public String product;
        public String androidVersion;
        public String kernelVersion;
        public String cpuAbi;
        public String totalRam;
        public String totalStorage;
        public String hardware;
        public String board;
        public String cameraCount;
        public String phoneIdentifier;
        public String displayResolution;
        public String displayDensity;
        public String refreshRate;
        public String isRooted;
        public String widevineLevel;
        public String biometricSupport;
        public String processor;

        public DeviceProfile(String deviceName, String modelNumber, String brand, String product,
                             String androidVersion, String kernelVersion, String cpuAbi, String totalRam,
                             String totalStorage, String hardware, String board, String cameraCount,
                             String phoneIdentifier, String displayResolution, String displayDensity,
                             String refreshRate, String isRooted, String widevineLevel, String biometricSupport,
                             String processor) {
            this.deviceName = deviceName;
            this.modelNumber = modelNumber;
            this.brand = brand;
            this.product = product;
            this.androidVersion = androidVersion;
            this.kernelVersion = kernelVersion;
            this.cpuAbi = cpuAbi;
            this.totalRam = totalRam;
            this.totalStorage = totalStorage;
            this.hardware = hardware;
            this.board = board;
            this.cameraCount = cameraCount;
            this.phoneIdentifier = phoneIdentifier;
            this.displayResolution = displayResolution;
            this.displayDensity = displayDensity;
            this.refreshRate = refreshRate;
            this.isRooted = isRooted;
            this.widevineLevel = widevineLevel;
            this.biometricSupport = biometricSupport;
            this.processor = processor;
        }
    }

    public static class BatteryStatus {
        public String percentage;
        public String health;
        public String chargingState;
        public String temperature;
        public String voltage;
        public String technology;

        public BatteryStatus(String percentage, String health, String chargingState, String temperature,
                             String voltage, String technology) {
            this.percentage = percentage;
            this.health = health;
            this.chargingState = chargingState;
            this.temperature = temperature;
            this.voltage = voltage;
            this.technology = technology;
        }
    }

    public static class NetworkStatus {
        public String connection;
        public String networkType;
        public String roaming;
        public String signalHint;
        public String localIp;
        public String wifiSsid;

        public NetworkStatus(String connection, String networkType, String roaming, String signalHint,
                             String localIp, String wifiSsid) {
            this.connection = connection;
            this.networkType = networkType;
            this.roaming = roaming;
            this.signalHint = signalHint;
            this.localIp = localIp;
            this.wifiSsid = wifiSsid;
        }
    }

    public static class StorageStatus {
        public String internalUsed;
        public String internalFree;
        public String internalTotal;
        public String externalFree;
        public String externalTotal;
        public CallLogSummary callLogSummary;

        public StorageStatus(String internalUsed, String internalFree, String internalTotal,
                             String externalFree, String externalTotal, CallLogSummary callLogSummary) {
            this.internalUsed = internalUsed;
            this.internalFree = internalFree;
            this.internalTotal = internalTotal;
            this.externalFree = externalFree;
            this.externalTotal = externalTotal;
            this.callLogSummary = callLogSummary;
        }
    }

    public static class CallLogSummary {
        public String totalCalls;
        public String lastCall;

        public CallLogSummary(String totalCalls, String lastCall) {
            this.totalCalls = totalCalls;
            this.lastCall = lastCall;
        }
    }

    public static class SensorGroup {
        public String title;
        public List<SensorStatus> sensors;

        public SensorGroup(String title, List<SensorStatus> sensors) {
            this.title = title;
            this.sensors = sensors;
        }
    }

    public static class SensorStatus {
        public String label;
        public String availability;
        public String vendor;
        public String version;

        public SensorStatus(String label, String availability, String vendor, String version) {
            this.label = label;
            this.availability = availability;
            this.vendor = vendor;
            this.version = version;
        }
    }
}
