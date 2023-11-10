package com.pathfoss.vivoxia.general;

import androidx.annotation.NonNull;

import com.google.android.material.slider.LabelFormatter;

import java.util.Objects;

public class SliderFormatter implements LabelFormatter {

    private final String dataType;

    // Create constructor
    public SliderFormatter (String dataType) {
        this.dataType = dataType;
    }

    @NonNull
    @Override
    public String getFormattedValue(float value) {

        // Format height if necessary
        if (Objects.equals(dataType, "ft")) {
            return (int) (value / 12) + "'" + (int) (value % 12) +"''";
        }
        return (int) value + " " + dataType;
    }
}