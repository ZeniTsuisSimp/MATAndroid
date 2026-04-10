package com.mdt.androidjava.data;

import java.util.List;

public class DashboardSnapshot {
    private final String generatedAt;
    private final DeviceProfile device;
    private final BatteryStatus battery;
    private final NetworkStatus network;
    private final StorageStatus storage;
    private final List<SensorGroup> sensors;

    public DashboardSnapshot(
            String generatedAt,
            DeviceProfile device,
            BatteryStatus battery,
            NetworkStatus network,
            StorageStatus storage,
            List<SensorGroup> sensors
    ) {
        this.generatedAt = generatedAt;
        this.device = device;
        this.battery = battery;
        this.network = network;
        this.storage = storage;
        this.sensors = sensors;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public DeviceProfile getDevice() {
        return device;
    }

    public BatteryStatus getBattery() {
        return battery;
    }

    public NetworkStatus getNetwork() {
        return network;
    }

    public StorageStatus getStorage() {
        return storage;
    }

    public List<SensorGroup> getSensors() {
        return sensors;
    }
}
