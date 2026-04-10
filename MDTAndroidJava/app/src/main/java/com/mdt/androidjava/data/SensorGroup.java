package com.mdt.androidjava.data;

import java.util.List;

public class SensorGroup {
    private final String title;
    private final List<SensorStatus> sensors;

    public SensorGroup(String title, List<SensorStatus> sensors) {
        this.title = title;
        this.sensors = sensors;
    }

    public String getTitle() {
        return title;
    }

    public List<SensorStatus> getSensors() {
        return sensors;
    }
}
