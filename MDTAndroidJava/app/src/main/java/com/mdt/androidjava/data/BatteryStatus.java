package com.mdt.androidjava.data;

public class BatteryStatus {
    private final String percentage;
    private final String health;
    private final String chargingState;
    private final String temperature;

    public BatteryStatus(String percentage, String health, String chargingState, String temperature) {
        this.percentage = percentage;
        this.health = health;
        this.chargingState = chargingState;
        this.temperature = temperature;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getHealth() {
        return health;
    }

    public String getChargingState() {
        return chargingState;
    }

    public String getTemperature() {
        return temperature;
    }
}
