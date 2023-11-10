package com.pathfoss.vivoxia.general;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.pathfoss.vivoxia.R;

import java.util.ArrayList;
import java.util.List;

public class Settings extends Fragment {

    private TextInputLayout tilGender;
    private TextInputLayout tilUnit;
    private TextInputLayout tilAge;
    private TextInputLayout tilHeight;
    private TextInputLayout tilSound;

    private final SharedPreferences sharedPreferences = Controller.getSharedPreferences();
    private final SharedPreferences.Editor sharedPreferencesEditor = Controller.getSharedPreferencesEditor();
    private final String[] genders = {"Male", "Female"};
    private final String[] units = {"Metric (kg / cm)", "Imperial (lbs / ft / in)"};
    private final String[] sounds = {"On", "Off"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_settings, container, false);

        tilGender = view1.findViewById(R.id.til_gender);
        tilUnit = view1.findViewById(R.id.til_units);
        tilAge = view1.findViewById(R.id.til_age);
        tilHeight = view1.findViewById(R.id.til_height);
        tilSound = view1.findViewById(R.id.til_sound);

        tilGender.setHint("Gender");
        tilUnit.setHint("Unit");
        tilAge.setHint("Age");
        tilHeight.setHint("Height");
        tilSound.setHint("Sound");

        return view1;
    }

    // Create method to refresh all dropdown items
    private void refreshLayout() {
        List<String> age = new ArrayList<>();
        List<String> height = new ArrayList<>();

        for (int i=0; i <= 150; i++) {
            age.add(i + " years");
        }

        for (int i=0; i <= 300; i++) {
            height.add(Units.toHeightIfApplicable(i, Units.getHeightUnit()));
        }

        AutoCompleteTextView actGender = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilGender.findViewById(R.id.act), genders, 0);
        AutoCompleteTextView actUnit = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilUnit.findViewById(R.id.act), units, 0);
        AutoCompleteTextView actAge = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilAge.findViewById(R.id.act), age.toArray(new String[0]), 0);
        AutoCompleteTextView actHeight = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilHeight.findViewById(R.id.act), height.toArray(new String[0]), 0);
        AutoCompleteTextView actSound = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilSound.findViewById(R.id.act), sounds, 0);

        actGender.setText(genders[convertBooleanToInt(sharedPreferences.getBoolean("Male", true))], false);
        actUnit.setText(units[convertBooleanToInt(sharedPreferences.getBoolean("Metric", true))], false);
        actAge.setText(age.get(sharedPreferences.getInt("Age", 0)), false);
        actHeight.setText(height.get((int) Units.fromMetric(sharedPreferences.getFloat("Height", 0f), Units.getHeightUnit())), false);
        actSound.setText(sounds[convertBooleanToInt(sharedPreferences.getBoolean("Sound", true))], false);

        actGender.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putBoolean("Male", convertIntToBoolean(position)).apply());
        actUnit.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putBoolean("Metric", convertIntToBoolean(position)).apply());
        actAge.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putInt("Age", position).apply());
        actHeight.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putFloat("Height", Units.toMetric(position, Units.getLengthUnit())).apply());
        actSound.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putBoolean("Sound", convertIntToBoolean(position)).apply());
    }

    // Create method to convert integer to true or false for setting preferences
    private boolean convertIntToBoolean (int input) {
        return input != 1;
    }

    // Create method to convert boolean to 0 or 1 for indexing
    private int convertBooleanToInt (boolean input) {
        if (input) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout();
    }
}