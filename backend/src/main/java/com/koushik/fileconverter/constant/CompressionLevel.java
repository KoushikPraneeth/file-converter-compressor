package com.koushik.fileconverter.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompressionLevel {
    HIGH(25, 0.25f),
    MEDIUM(50, 0.50f),
    LOW(75, 0.75f);

    private final int value;
    private final float compressionRatio;

    public static CompressionLevel fromValue(int value) {
        if (value <= 25) return HIGH;
        if (value <= 50) return MEDIUM;
        return LOW;
    }
}
