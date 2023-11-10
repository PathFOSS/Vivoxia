package com.pathfoss.vivoxia.nutrition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.Units;

import java.text.DecimalFormat;
import java.util.Objects;

public class FoodEstimates extends DialogFragment {

    private int age;
    private float height;
    private float weight;
    private float multiplier;

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate, define, and modify views
        View view = inflater.inflate(R.layout.fragment_food_estimates, container, false);

        ConstraintLayout cl1 = view.findViewById(R.id.cl_1);
        ConstraintLayout cl2 = view.findViewById(R.id.cl_2);
        ConstraintLayout cl3 = view.findViewById(R.id.cl_3);
        ConstraintLayout cl4 = view.findViewById(R.id.cl_4);
        ConstraintLayout cl5 = view.findViewById(R.id.cl_5);
        ConstraintLayout cl6 = view.findViewById(R.id.cl_6);
        ConstraintLayout cl7 = view.findViewById(R.id.cl_7);

        setTextViews(cl1.findViewById(R.id.tv_title), cl1.findViewById(R.id.tv_value), R.drawable.icon_trend_down, -1f);
        setTextViews(cl2.findViewById(R.id.tv_title), cl2.findViewById(R.id.tv_value), R.drawable.icon_trend_down, -0.5f);
        setTextViews(cl3.findViewById(R.id.tv_title), cl3.findViewById(R.id.tv_value), R.drawable.icon_trend_down, -0.25f);
        setTextViews(cl4.findViewById(R.id.tv_title), cl4.findViewById(R.id.tv_value), R.drawable.icon_trend_flat, 0f);
        setTextViews(cl5.findViewById(R.id.tv_title), cl5.findViewById(R.id.tv_value), R.drawable.icon_trend_up, 0.25f);
        setTextViews(cl6.findViewById(R.id.tv_title), cl6.findViewById(R.id.tv_value), R.drawable.icon_trend_up, 0.5f);
        setTextViews(cl7.findViewById(R.id.tv_title), cl7.findViewById(R.id.tv_value), R.drawable.icon_trend_up, 1f);

        return view;
    }

    // Create method to modify all TextViews based on weekly weight change goal and unit preference
    private void setTextViews (@NonNull TextView tvTitle, @NonNull TextView tvValue, int drawable, float difference) {
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        tvTitle.setCompoundDrawablePadding(Units.convertDPtoPX(20));

        String titleText = decimalFormat.format(Math.abs(difference)) + " kg";
        if (!Controller.getSharedPreferences().getBoolean("Metric", true)) {
            titleText = decimalFormat.format(Math.abs(difference * 2)) + " lbs";
            difference = Units.toMetric(2 * difference, "lbs");
        }
        tvTitle.setText(titleText);

        String calorieText = Math.max(0, (int) (getMaintenanceCalories() + (difference * 7716.179176470715 / 7))) + " kcal";
        tvValue.setText(calorieText);
    }

    // Create method to get maintenance calories based on gender
    private int getMaintenanceCalories () {
        int base = (int) (10 * weight + 6.25 * height - 5 * age);
        if (Controller.getSharedPreferences().getBoolean("Male", true)) {
            return (int) ((base + 5) * multiplier);
        } else {
            return (int) ((base - 161) * multiplier);
        }
    }

    // Create setter method to set inputs from FoodEstimator fragment
    public void setValues (int newAge, float newHeight, float newWeight, float newMultiplier) {
        age = newAge;
        height = newHeight;
        weight = newWeight;
        multiplier = newMultiplier;
    }

    // Create method to set the window parameters
    @Override
    public void onResume() {
        super.onResume();
        ReusableMethods.setDialogWindowParameters(Objects.requireNonNull(getDialog()));
    }
}