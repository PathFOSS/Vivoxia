package com.pathfoss.vivoxia.nutrition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.SliderFormatter;
import com.pathfoss.vivoxia.general.Units;

import java.util.HashMap;

public class FoodEstimator extends Fragment {

    private View view;

    private final HashMap<String, Float> activityMultiplierMap = new HashMap<>();
    private final String[] activityList = {
            "No exercise",
            "Light exercise 1-2 days",
            "Moderate exercise 2-3 days",
            "Hard exercise 4-5 days",
            "Intense exercise 6-7 days",
            "Professional athlete"
    };

    private int activityIndex = 0;
    private Object[][] objects;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Put equivalent multipliers into the hashmap based on activity level
        activityMultiplierMap.put(activityList[0], 1.2f);
        activityMultiplierMap.put(activityList[1], 1.4f);
        activityMultiplierMap.put(activityList[2], 1.6f);
        activityMultiplierMap.put(activityList[3], 1.75f);
        activityMultiplierMap.put(activityList[4], 2.0f);
        activityMultiplierMap.put(activityList[5], 2.3f);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflate, define, and modify views
        view = inflater.inflate(R.layout.fragment_food_estimator, container, false);

        ConstraintLayout clAge = view.findViewById(R.id.cl_age);
        ConstraintLayout clHeight = view.findViewById(R.id.cl_height);
        ConstraintLayout clWeight = view.findViewById(R.id.cl_weight);

        Slider ageSlider = clAge.findViewById(R.id.slider);
        Slider heightSlider = clHeight.findViewById(R.id.slider);
        Slider weightSlider = clWeight.findViewById(R.id.slider);

        objects = new Object[][]{
                {ageSlider, clAge.findViewById(R.id.tv_title), clAge.findViewById(R.id.tv_value), "Age", "years"},
                {heightSlider, clHeight.findViewById(R.id.tv_title), clHeight.findViewById(R.id.tv_value), "Height", Units.getHeightUnit()},
                {weightSlider, clWeight.findViewById(R.id.tv_title), clWeight.findViewById(R.id.tv_value), "Weight", Units.getMassUnit()},
        };

        view.findViewById(R.id.ib_confirm).setOnClickListener( v -> {
                    FoodEstimates foodEstimates = new FoodEstimates();
                    foodEstimates.setValues((int) ageSlider.getValue(),
                            Units.toMetric(heightSlider.getValue(), Units.getLengthUnit()),
                            Units.toMetric(weightSlider.getValue(), Units.getMassUnit()),
                            getFromHashMap(activityList[activityIndex])
                    );
                    foodEstimates.show(getParentFragmentManager(), "Food Estimates");
                });

        ageSlider.setValueTo(150);
        ageSlider.setValue(Controller.getSharedPreferences().getInt("Age", 0));
        refreshLayout();
        return view;
    }

    // Create method to refresh the view on request
    private void refreshLayout() {
        ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), view.findViewById(R.id.act), activityList, 0).setOnItemClickListener((parent, view, position, id) -> activityIndex = position);
        for (Object[] object : objects) {

            Slider slider = (Slider) object[0];
            TextView tvValue = (TextView) object[2];
            String dataType = (String) object[4];
            slider.setValueTo(Math.max(slider.getValue(), 100f));

            ((TextView) object[1]).setText((String) object[3]);
            tvValue.setText(Units.toHeightIfApplicable((int) slider.getValue(), dataType));

            slider.setLabelFormatter(new SliderFormatter(dataType));

            setSliderListener(slider, tvValue, dataType);
        }
    }

    // Create method to set Slider change listener
    private void setSliderListener (@NonNull Slider slider, TextView textView, String unit) {
        slider.addOnChangeListener((slider1, value, fromUser) -> {
            textView.setText(Units.toHeightIfApplicable((int) value, unit));
            if (value == slider1.getValueTo()) {
                slider1.setValueTo(value + 1);
            }
        });
    }

    // Create null-safe method to get values from HashMaps
    @NonNull
    private Float getFromHashMap(String key) {
        Float output = activityMultiplierMap.get(key);
        return output != null ? output : 0f;
    }

    // Create method to refresh sliders and dropdown on user resume
    @Override
    public void onResume() {
        super.onResume();
        refreshLayout();
    }
}