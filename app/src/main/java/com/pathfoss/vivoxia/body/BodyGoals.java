package com.pathfoss.vivoxia.body;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pathfoss.vivoxia.general.ViewChangeListener;

import java.util.Objects;

public class BodyGoals extends Fragment {

    private View view;
    private Object[][] setList;

    private final ViewChangeListener viewChangeListener;
    private final SharedPreferences sharedPreferences = Controller.getSharedPreferences();
    private final SharedPreferences.Editor sharedPreferencesEditor = Controller.getSharedPreferencesEditor();

    private String smallLengthUnit;
    private String massUnit;

    // Create a constructor for setting UI change listener
    public BodyGoals (ViewChangeListener viewChangeListener) {
        this.viewChangeListener = viewChangeListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define units
        smallLengthUnit = Units.getLengthUnit();
        massUnit = Units.getMassUnit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate and define all views
        view = inflater.inflate(R.layout.fragment_body_goals, container,false);
        ConstraintLayout clCalf = view.findViewById(R.id.sl_calf);
        ConstraintLayout clThigh = view.findViewById(R.id.sl_thigh);
        ConstraintLayout clForearm = view.findViewById(R.id.sl_forearm);
        ConstraintLayout clBicep = view.findViewById(R.id.sl_bicep);
        ConstraintLayout clHips = view.findViewById(R.id.sl_hips);
        ConstraintLayout clWaist = view.findViewById(R.id.sl_waist);
        ConstraintLayout clChest = view.findViewById(R.id.sl_chest);
        ConstraintLayout clShoulders = view.findViewById(R.id.sl_shoulders);
        ConstraintLayout clNeck = view.findViewById(R.id.sl_neck);
        ConstraintLayout clWeight = view.findViewById(R.id.sl_weight);
        ConstraintLayout clClavicles = view.findViewById(R.id.sl_clavicles);
        ConstraintLayout clBodyFat = view.findViewById(R.id.sl_body_fat);

        // Create a list of grouped views
        setList = new Object[][]{
                {getResources().getString(R.string.calf_capitalized), smallLengthUnit, clCalf.findViewById(R.id.tv_title), clCalf.findViewById(R.id.tv_value), clCalf.findViewById(R.id.slider)},
                {getResources().getString(R.string.thigh_capitalized), smallLengthUnit, clThigh.findViewById(R.id.tv_title), clThigh.findViewById(R.id.tv_value), clThigh.findViewById(R.id.slider)},
                {getResources().getString(R.string.forearm_capitalized), smallLengthUnit, clForearm.findViewById(R.id.tv_title), clForearm.findViewById(R.id.tv_value), clForearm.findViewById(R.id.slider)},
                {getResources().getString(R.string.bicep_capitalized), smallLengthUnit, clBicep.findViewById(R.id.tv_title), clBicep.findViewById(R.id.tv_value), clBicep.findViewById(R.id.slider)},
                {getResources().getString(R.string.hips_capitalized), smallLengthUnit, clHips.findViewById(R.id.tv_title), clHips.findViewById(R.id.tv_value), clHips.findViewById(R.id.slider)},
                {getResources().getString(R.string.waist_capitalized), smallLengthUnit, clWaist.findViewById(R.id.tv_title), clWaist.findViewById(R.id.tv_value), clWaist.findViewById(R.id.slider)},
                {getResources().getString(R.string.chest_capitalized), smallLengthUnit, clChest.findViewById(R.id.tv_title), clChest.findViewById(R.id.tv_value), clChest.findViewById(R.id.slider)},
                {getResources().getString(R.string.shoulders_capitalized), smallLengthUnit, clShoulders.findViewById(R.id.tv_title), clShoulders.findViewById(R.id.tv_value), clShoulders.findViewById(R.id.slider)},
                {getResources().getString(R.string.neck_capitalized), smallLengthUnit, clNeck.findViewById(R.id.tv_title), clNeck.findViewById(R.id.tv_value), clNeck.findViewById(R.id.slider)},
                {getResources().getString(R.string.weight_capitalized), massUnit, clWeight.findViewById(R.id.tv_title), clWeight.findViewById(R.id.tv_value), clWeight.findViewById(R.id.slider)},
                {getResources().getString(R.string.clavicles_capitalized), smallLengthUnit, clClavicles.findViewById(R.id.tv_title), clClavicles.findViewById(R.id.tv_value), clClavicles.findViewById(R.id.slider)},
                {getResources().getString(R.string.body_fat_capitalized), "%", clBodyFat.findViewById(R.id.tv_title), clBodyFat.findViewById(R.id.tv_value), clBodyFat.findViewById(R.id.slider)},
        };

        updateSliders();
        return view;
    }

    // Create method to update sliders to current values
    private void updateSliders() {

        // Set all sliders to old values
        for (Object[] objects : setList) {

            // Cast reusable objects
            String unit = (String) objects[1];
            float currentValue = Units.fromMetric(sharedPreferences.getFloat((String) objects[0], 0f), unit);
            TextView textView = ((TextView) objects[3]);
            Slider slider = (Slider) objects[4];
            ((TextView) objects[2]).setText((CharSequence) objects[0]);

            // Define slider element parameters
            if (!Objects.equals(objects[1], "%")) {
                slider.setValueTo((int) currentValue + 20);
            } else {
                slider.setValueTo(100);
            }

            textView.setText(Units.toHeightIfApplicable(currentValue, unit));
            slider.setValue((float) ((int) currentValue));
            setSliderListener(slider, textView, unit);
        }

        // Notify user and save values on click
        view.findViewById(R.id.ib_save).setOnClickListener( v -> {
            for (Object[] objects : setList) {
                sharedPreferencesEditor.putFloat((String) objects[0], Units.toMetric(((Slider) objects[4]).getValue(), (String) objects[1])).apply();
            }
            Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show();
        });

        // Set scroll listener to change top bar color
        ReusableMethods.setScrollViewListener(view.findViewById(R.id.ncv), viewChangeListener);
    }

    // Create method to set listener to a slider
    private void setSliderListener (@NonNull Slider slider, TextView textView, String unit) {

        // Set slider formatter
        slider.setLabelFormatter(new SliderFormatter(unit));

        // Set slider listener
        slider.addOnChangeListener((slider1, value, fromUser) -> {
            textView.setText(Units.toHeightIfApplicable((int) value, unit));
            if (!Objects.equals(unit, "%") && value == slider1.getValueTo()) {
                slider1.setValueTo(value + 2);
            }
        });
    }

    // Create method to redraw sliders on user resume
    @Override
    public void onResume() {
        super.onResume();
        updateSliders();
    }
}