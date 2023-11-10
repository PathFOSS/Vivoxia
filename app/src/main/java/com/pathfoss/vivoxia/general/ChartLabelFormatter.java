package com.pathfoss.vivoxia.general;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class ChartLabelFormatter extends ValueFormatter {

    private final String dataType;

    public ChartLabelFormatter(String dataType) {
        this.dataType = dataType;
    }

    // Create method to set string label in the chart based on units
    @Override
    public String getFormattedValue(float value) {
        return Units.decimalFormat.format(Units.fromMetric(value, dataType));
    }
}