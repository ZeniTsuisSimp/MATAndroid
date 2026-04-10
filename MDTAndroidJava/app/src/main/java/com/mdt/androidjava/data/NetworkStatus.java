package com.mdt.androidjava.data;

public class NetworkStatus {
    private final String connection;
    private final String networkType;
    private final String roaming;
    private final String signalHint;

    public NetworkStatus(String connection, String networkType, String roaming, String signalHint) {
        this.connection = connection;
        this.networkType = networkType;
        this.roaming = roaming;
        this.signalHint = signalHint;
    }

    public String getConnection() {
        return connection;
    }

    public String getNetworkType() {
        return networkType;
    }

    public String getRoaming() {
        return roaming;
    }

    public String getSignalHint() {
        return signalHint;
    }
}
