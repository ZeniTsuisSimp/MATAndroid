package com.mdt.androidjava.data;

public class DeviceProfile {
    private final String deviceName;
    private final String modelNumber;
    private final String brand;
    private final String product;
    private final String androidVersion;
    private final String kernelVersion;
    private final String cpuAbi;
    private final String totalRam;
    private final String totalStorage;
    private final String hardware;
    private final String board;
    private final String cameraCount;
    private final String phoneIdentifier;

    public DeviceProfile(
            String deviceName,
            String modelNumber,
            String brand,
            String product,
            String androidVersion,
            String kernelVersion,
            String cpuAbi,
            String totalRam,
            String totalStorage,
            String hardware,
            String board,
            String cameraCount,
            String phoneIdentifier
    ) {
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
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getProduct() {
        return product;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public String getCpuAbi() {
        return cpuAbi;
    }

    public String getTotalRam() {
        return totalRam;
    }

    public String getTotalStorage() {
        return totalStorage;
    }

    public String getHardware() {
        return hardware;
    }

    public String getBoard() {
        return board;
    }

    public String getCameraCount() {
        return cameraCount;
    }

    public String getPhoneIdentifier() {
        return phoneIdentifier;
    }
}
