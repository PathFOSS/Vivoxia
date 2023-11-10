package com.pathfoss.vivoxia.general;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;

public class Units {

    public static int phoneDPI;
    public final static DecimalFormat decimalFormat = new DecimalFormat("0.0");
    private final static SharedPreferences sharedPreferences = Controller.getSharedPreferences();

    private final static float poundMultiplier =  0.45359237f;
    private final static float ounceMultiplier = 28.349523125f;
    private final static int kilogramMultiplier = 1000;
    private final static float inchMultiplier = 2.54f;

    // Create method to convert pixels to DP for defining UI elements
    public static int convertDPtoPX (int dp) {
        return dp * phoneDPI /160;
    }

    // Create method to convert input to grams
    public static float toGrams (float input, @NonNull String unit) {
        switch (unit) {
            case "kg":
                return input * kilogramMultiplier;
            case "oz":
                return input * ounceMultiplier;
            case "lbs":
                return input * poundMultiplier * kilogramMultiplier;
            default:
                return input;
        }
    }

    // Create method to convert input to metric units from imperial units
    public static float toMetric (float input, @NonNull String unit) {
        switch (unit) {
            case "lbs":
                return input * poundMultiplier;
            case "ft":
            case "in":
                return input * inchMultiplier;
            default:
                return input;
        }
    }

    // Create method to convert input from metric units to imperial units
    public static float fromMetric (float input, @NonNull String unit) {
        switch (unit) {
            case "lbs":
                return input / poundMultiplier;
            case "ft":
            case "in":
                return input / inchMultiplier;
            default:
                return input;
        }
    }

    // Create method to convert input to a height string or a normal string
    @NonNull
    public static String toHeightIfApplicable (float input, @NonNull String unit) {
        if (unit.equals("ft")) {
            return (int) (input / 12) + "'" + (int) (input % 12) + "''";
        }
        return (int) input + " " + unit;
    }

    // Create method to get current length unit
    @NonNull
    public static String getLengthUnit () {
        if (sharedPreferences.getBoolean("Metric", true)) {
            return "cm";
        }
        return "in";
    }

    // Create method to get current height unit
    @NonNull
    public static String getHeightUnit () {
        if (sharedPreferences.getBoolean("Metric", true)) {
            return "cm";
        }
        return "ft";
    }

    // Create method to get current mass unit
    @NonNull
    public static String getMassUnit () {
        if (sharedPreferences.getBoolean("Metric", true)) {
            return "kg";
        }
        return  "lbs";
    }

    // Create method to get data unit based on measurable item
    @NonNull
    public static String getFoodDataType () {
        if (sharedPreferences.getString("FoodChartItem", "Calories").equals("Calories")) {
            return "kcal";
        }
        return "g";
    }

    // Create method to get data unit based on measurable item
    public static String getBodyDataType () {
        switch (sharedPreferences.getString("BodyChartItem", "Weight")) {
            case "Height":
                return getHeightUnit();
            case "Weight":
                return getMassUnit();
            case "Body Fat":
                return "%";
            default:
                return getLengthUnit();
        }
    }

    // Create method to get data unit based on measurable item
    @NonNull
    public static String getExerciseDataType () {
        String key = sharedPreferences.getString("ExerciseChartItem", "Volume / Workout");
        switch (key) {
            case "Volume / Workout":
                return getMassUnit();
            case "Workout Time":
                return "min";
            case "Rest / Set":
            case "Work / Set":
                return "sec";
            default:
                return "";
        }
    }

    // Create method to get a database-suitable column name based on requested chart item
    @NonNull
    public static String getFoodChartItem () {
        return sharedPreferences.getString("FoodChartItem", "Calories").replace(" ", "_").toUpperCase();
    }

    // Create method to get a goal value based on requested chart item
    public static float getFoodChartItemGoal () {
        return sharedPreferences.getFloat(sharedPreferences.getString("FoodChartItem", "Calories"), 0f);
    }

    // Create method to get a database-suitable column name based on requested chart item
    @NonNull
    public static String getBodyChartItem () {
        return sharedPreferences.getString("BodyChartItem", "Weight").replace(" ", "_").toUpperCase();
    }

    // Create method to get a goal value based on requested chart item
    public static float getBodyChartItemGoal () {
        String bodyPart = sharedPreferences.getString("BodyChartItem", "Weight");
        String firstWord = bodyPart.split(" ")[0];

        if (firstWord.equals("Left") || firstWord.equals("Right")) {
            return sharedPreferences.getFloat(bodyPart.split(" ")[1], 0f);
        }
        return sharedPreferences.getFloat(bodyPart, 0f);
    }

    // Create method to get a database-suitable column name based on requested chart item
    @NonNull
    public static String getExerciseChartItem () {
        return sharedPreferences.getString("ExerciseChartItem", "Volume");
    }

    // Create method to get a goal value based on requested chart item
    public static float getExerciseChartItemGoal () {
        return sharedPreferences.getFloat(sharedPreferences.getString("ExerciseChartItem", "Volume"), 0f);
    }
}