package com.mdt.androidjava.data;

public class CallLogSummary {
    private final String totalCalls;
    private final String lastCall;

    public CallLogSummary(String totalCalls, String lastCall) {
        this.totalCalls = totalCalls;
        this.lastCall = lastCall;
    }

    public String getTotalCalls() {
        return totalCalls;
    }

    public String getLastCall() {
        return lastCall;
    }
}
