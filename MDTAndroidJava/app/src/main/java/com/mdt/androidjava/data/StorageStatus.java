package com.mdt.androidjava.data;

public class StorageStatus {
    private final String internalUsed;
    private final String internalFree;
    private final String internalTotal;
    private final String externalFree;
    private final String externalTotal;
    private final CallLogSummary callLogSummary;

    public StorageStatus(
            String internalUsed,
            String internalFree,
            String internalTotal,
            String externalFree,
            String externalTotal,
            CallLogSummary callLogSummary
    ) {
        this.internalUsed = internalUsed;
        this.internalFree = internalFree;
        this.internalTotal = internalTotal;
        this.externalFree = externalFree;
        this.externalTotal = externalTotal;
        this.callLogSummary = callLogSummary;
    }

    public String getInternalUsed() {
        return internalUsed;
    }

    public String getInternalFree() {
        return internalFree;
    }

    public String getInternalTotal() {
        return internalTotal;
    }

    public String getExternalFree() {
        return externalFree;
    }

    public String getExternalTotal() {
        return externalTotal;
    }

    public CallLogSummary getCallLogSummary() {
        return callLogSummary;
    }
}
