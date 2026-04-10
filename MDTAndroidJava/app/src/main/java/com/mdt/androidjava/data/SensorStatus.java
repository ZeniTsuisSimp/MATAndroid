package com.mdt.androidjava.data;

public class SensorStatus {
    private final String label;
    private final String availability;
    private final String vendor;
    private final String version;

    public SensorStatus(String label, String availability, String vendor, String version) {
        this.label = label;
        this.availability = availability;
        this.vendor = vendor;
        this.version = version;
    }

    public String getLabel() {
        return label;
    }

    public String getAvailability() {
        return availability;
    }

    public String getVendor() {
        return vendor;
    }

    public String getVersion() {
        return version;
    }
}
