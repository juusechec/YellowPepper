package com.yellowpepper.challenge.service.enums;

public enum AvailabilityTransfer {
    OK("OK"),
    NO_FUNDS("NO_FUNDS"),
    LIMITS_EXCEED("LIMITS_EXCEED");

    public final String label;

    private AvailabilityTransfer(String label) {
        this.label = label;
    }

    public AvailabilityTransfer getValue(){
        return this;
    }
}
