package com.project.PJA.workspace.enumeration;

public enum ProgressStep {
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6");

    private final String value;

    ProgressStep(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProgressStep fromValue(String value) {
        for (ProgressStep step : values()) {
            if (step.value.equals(value)) return step;
        }
        throw new IllegalArgumentException();
    }
}
