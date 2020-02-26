package com.example.customprogressbarproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;

public class TestRange {

    @NonNull private final BigDecimal mHighRate;
    @NonNull private final BigDecimal mMinValue;
    @Nullable private final BigDecimal mMaxValue;

    TestRange(@NonNull final BigDecimal highRate,
              @NonNull final BigDecimal minValue,
              @Nullable final BigDecimal maxValue){
        mHighRate = highRate;
        mMinValue = minValue;
        mMaxValue = maxValue;
    }

    @NonNull
    public BigDecimal highRate() {
        return mHighRate;
    }

    @NonNull
    public BigDecimal minValue() {
        return mMinValue;
    }

    @Nullable
    public BigDecimal maxValue() {
        return mMaxValue;
    }
}
