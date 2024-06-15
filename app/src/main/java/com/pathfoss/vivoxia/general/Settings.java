package com.pathfoss.vivoxia.general;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

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
    private TextInputLayout tilSets;
    private TextInputLayout tilReps;
    private TextInputLayout tilRest;
    private TextInputLayout tilWork;
    private TextInputLayout tilEffort;

    private final SharedPreferences sharedPreferences = Controller.getSharedPreferences();
    private final SharedPreferences.Editor sharedPreferencesEditor = Controller.getSharedPreferencesEditor();
    private final String[] genders = {"Male", "Female"};
    private final String[] units = {"Metric (kg / cm)", "Imperial (lbs / ft / in)"};
    private final String[] sounds = {"On", "Off"};
    private final String [] effort = {"0 / 10", "1 / 10", "2 / 10", "3 / 10", "4 / 10", "5 / 10", "6 / 10", "7 / 10", "8 / 10", "9 / 10", "10 / 10"};

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

        tilSets = view1.findViewById(R.id.til_sets);
        tilReps = view1.findViewById(R.id.til_reps);
        tilRest = view1.findViewById(R.id.til_rest);
        tilWork = view1.findViewById(R.id.til_work);
        tilEffort = view1.findViewById(R.id.til_effort);

        tilGender.setHint("Gender");
        tilUnit.setHint("Unit");
        tilAge.setHint("Age");
        tilHeight.setHint("Height");
        tilSound.setHint("Sound");

        tilSets.setHint("Sets Autofill");
        tilReps.setHint("Reps Autofill");
        tilRest.setHint("Rest Autofill");
        tilWork.setHint("Work Autofill");
        tilEffort.setHint("Effort Autofill");

        return view1;
    }

    // Create method to refresh all dropdown items
    private void refreshLayout() {

        List<String> age = new ArrayList<>();
        List<String> height = new ArrayList<>();
        List<String> sets = new ArrayList<>();
        List<String> reps = new ArrayList<>();
        List<String> rest = new ArrayList<>();
        List<String> work = new ArrayList<>();

        for (int i=0; i <= 150; i++) {
            age.add(i + " years");
        }

        for (int i=0; i <= 300; i++) {
            height.add(Units.toHeightIfApplicable(i, Units.getHeightUnit()));
            sets.add(i + " sets");
            reps.add(i + " reps");
            rest.add(i + " seconds");
            work.add(i + " seconds");
        }

        AutoCompleteTextView actGender = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilGender.findViewById(R.id.act), genders, 0);
        AutoCompleteTextView actUnit = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilUnit.findViewById(R.id.act), units, 0);
        AutoCompleteTextView actAge = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilAge.findViewById(R.id.act), age.toArray(new String[0]), 0);
        AutoCompleteTextView actHeight = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilHeight.findViewById(R.id.act), height.toArray(new String[0]), 0);
        AutoCompleteTextView actSound = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilSound.findViewById(R.id.act), sounds, 0);

        AutoCompleteTextView actSets = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilSets.findViewById(R.id.act), sets.toArray(new String[0]), 0);
        AutoCompleteTextView actReps = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilReps.findViewById(R.id.act), reps.toArray(new String[0]), 0);
        AutoCompleteTextView actRest = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilRest.findViewById(R.id.act), rest.toArray(new String[0]), 0);
        AutoCompleteTextView actWork = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilWork.findViewById(R.id.act), work.toArray(new String[0]), 0);
        AutoCompleteTextView actEffort = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilEffort.findViewById(R.id.act), effort, 0);

        actGender.setText(genders[convertBooleanToInt(sharedPreferences.getBoolean("Male", true))], false);
        actUnit.setText(units[convertBooleanToInt(sharedPreferences.getBoolean("Metric", true))], false);
        actAge.setText(age.get(sharedPreferences.getInt("Age", 0)), false);
        actHeight.setText(height.get((int) Units.fromMetric(sharedPreferences.getFloat("Height", 0f), Units.getHeightUnit())), false);
        actSound.setText(sounds[convertBooleanToInt(sharedPreferences.getBoolean("Sound", true))], false);

        actSets.setText(sets.get(sharedPreferences.getInt("Sets_Autofill", 1)), false);
        actReps.setText(reps.get(sharedPreferences.getInt("Reps_Autofill", 0)), false);
        actRest.setText(rest.get(sharedPreferences.getInt("Rest_Autofill", 40)), false);
        actWork.setText(work.get(sharedPreferences.getInt("Work_Autofill", 40)), false);
        actEffort.setText(effort[sharedPreferences.getInt("Effort_Autofill", 0)], false);

        actGender.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putBoolean("Male", convertIntToBoolean(position)).apply());
        actUnit.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putBoolean("Metric", convertIntToBoolean(position)).apply());
        actAge.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putInt("Age", position).apply());
        actHeight.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putFloat("Height", Units.toMetric(position, Units.getLengthUnit())).apply());
        actSound.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putBoolean("Sound", convertIntToBoolean(position)).apply());

        actSets.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putInt("Sets_Autofill", position).apply());
        actReps.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putInt("Reps_Autofill", position).apply());
        actRest.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putInt("Rest_Autofill", position).apply());
        actWork.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putInt("Work_Autofill", position).apply());
        actEffort.setOnItemClickListener((parent, view, position, id) -> sharedPreferencesEditor.putInt("Effort_Autofill", position).apply());
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

    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(requireContext(), "All preferences saved", Toast.LENGTH_SHORT).show();
    }
}