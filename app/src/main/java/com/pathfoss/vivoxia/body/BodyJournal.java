package com.pathfoss.vivoxia.body;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.DateChangeListener;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.TopBarListener;
import com.pathfoss.vivoxia.general.Units;
import com.pathfoss.vivoxia.general.ViewChangeListener;

import java.text.DecimalFormat;

public class BodyJournal extends Fragment implements DateChangeListener {

    private final DecimalFormat decimalFormat = Units.decimalFormat;
    public static String measurementDate;

    private TextInputEditText metCalfLeft;
    private TextInputEditText metCalfRight;
    private TextInputEditText metThighLeft;
    private TextInputEditText metThighRight;
    private TextInputEditText metForearmLeft;
    private TextInputEditText metForearmRight;
    private TextInputEditText metBicepLeft;
    private TextInputEditText metBicepRight;
    private TextInputEditText metHips;
    private TextInputEditText metWaist;
    private TextInputEditText metChest;
    private TextInputEditText metShoulders;
    private TextInputEditText metNeck;
    private TextInputEditText metClavicles;
    private TextInputEditText metWeight;
    private TextInputEditText metBodyFat;

    private String smallLengthUnit;
    private String massUnit;

    private final SharedPreferences sharedPreferences = Controller.getSharedPreferences();
    private final ViewChangeListener viewChangeListener;
    private Object[][] objectLists;

    public BodyJournal (ViewChangeListener viewChangeListener) {
        this.viewChangeListener = viewChangeListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define units
        measurementDate = Controller.getDataBaseDateFormat().format(System.currentTimeMillis());
        smallLengthUnit = Units.getLengthUnit();
        massUnit = Units.getMassUnit();
        ((TopBarListener) requireContext()).textChanged(Controller.getTopBarDateFormat().format(System.currentTimeMillis()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Define and assign views
        View view = inflater.inflate(R.layout.fragment_body_journal, container, false);

        ConstraintLayout clCalf = view.findViewById(R.id.det_calf);
        ConstraintLayout clThigh = view.findViewById(R.id.det_thigh);
        ConstraintLayout clForearm = view.findViewById(R.id.det_forearm);
        ConstraintLayout clBicep = view.findViewById(R.id.det_bicep);
        ConstraintLayout clCore = view.findViewById(R.id.det_core);
        ConstraintLayout clTorsoLower = view.findViewById(R.id.det_lower_torso);
        ConstraintLayout clTorsoUpper = view.findViewById(R.id.det_upper_torso);

        TextInputLayout tilCalfLeft = clCalf.findViewById(R.id.til_left);
        TextInputLayout tilCalfRight = clCalf.findViewById(R.id.til_right);
        TextInputLayout tilThighLeft = clThigh.findViewById(R.id.til_left);
        TextInputLayout tilThighRight = clThigh.findViewById(R.id.til_right);
        TextInputLayout tilForearmLeft = clForearm.findViewById(R.id.til_left);
        TextInputLayout tilForearmRight = clForearm.findViewById(R.id.til_right);
        TextInputLayout tilBicepLeft = clBicep.findViewById(R.id.til_left);
        TextInputLayout tilBicepRight = clBicep.findViewById(R.id.til_right);
        TextInputLayout tilHips = clCore.findViewById(R.id.til_left);
        TextInputLayout tilWaist = clCore.findViewById(R.id.til_right);
        TextInputLayout tilChest = clTorsoLower.findViewById(R.id.til_left);
        TextInputLayout tilShoulders = clTorsoLower.findViewById(R.id.til_right);
        TextInputLayout tilNeck = clTorsoUpper.findViewById(R.id.til_left);
        TextInputLayout tilClavicles = clTorsoUpper.findViewById(R.id.til_right);
        TextInputLayout tilWeight = view.findViewById(R.id.til_weight);
        TextInputLayout tilBodyFat = view.findViewById(R.id.til_body_fat);

        metCalfLeft = tilCalfLeft.findViewById(R.id.met);
        metCalfRight = tilCalfRight.findViewById(R.id.met);
        metThighLeft = tilThighLeft.findViewById(R.id.met);
        metThighRight = tilThighRight.findViewById(R.id.met);
        metForearmLeft = tilForearmLeft.findViewById(R.id.met);
        metForearmRight = tilForearmRight.findViewById(R.id.met);
        metBicepLeft = tilBicepLeft.findViewById(R.id.met);
        metBicepRight = tilBicepRight.findViewById(R.id.met);
        metHips = tilHips.findViewById(R.id.met);
        metWaist = tilWaist.findViewById(R.id.met);
        metChest = tilChest.findViewById(R.id.met);
        metShoulders = tilShoulders.findViewById(R.id.met);
        metNeck = tilNeck.findViewById(R.id.met);
        metClavicles = tilClavicles.findViewById(R.id.met);
        metWeight = tilWeight.findViewById(R.id.met);
        metBodyFat = tilBodyFat.findViewById(R.id.met);

        // Create list of object lists for mass changes
        objectLists = new Object[][]{
                {"Left Calf", smallLengthUnit, tilCalfLeft, metCalfLeft},
                {"Right Calf", smallLengthUnit, tilCalfRight, metCalfRight},
                {"Left Thigh", smallLengthUnit, tilThighLeft, metThighLeft},
                {"Right Thigh", smallLengthUnit, tilThighRight, metThighRight},
                {"Left Forearm", smallLengthUnit, tilForearmLeft, metForearmLeft},
                {"Right Forearm", smallLengthUnit, tilForearmRight, metForearmRight},
                {"Left Bicep", smallLengthUnit, tilBicepLeft, metBicepLeft},
                {"Right Bicep", smallLengthUnit, tilBicepRight, metBicepRight},
                {"Hips", smallLengthUnit, tilHips, metHips},
                {"Waist", smallLengthUnit, tilWaist, metWaist},
                {"Chest", smallLengthUnit, tilChest, metChest},
                {"Shoulders", smallLengthUnit, tilShoulders, metShoulders},
                {"Neck", smallLengthUnit, tilNeck, metNeck},
                {"Clavicles", smallLengthUnit, tilClavicles, metClavicles},
                {"Weight", massUnit, tilWeight, metWeight},
                {"Body Fat", "%", tilBodyFat, metBodyFat},
        };

        // Set hints for all input boxes
        for (Object[] objects : objectLists) {
            ((TextInputLayout) objects[2]).setHint((String) objects[0]);
        }
        
        // Create a Navy body fat calculator from end icon
        tilBodyFat.setEndIconDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_refresh));
        tilBodyFat.setEndIconVisible(true);
        tilBodyFat.setEndIconMode(tilBodyFat.getEndIconMode());

        tilBodyFat.setEndIconOnClickListener(v -> {
            String smallLengthUnit = Units.getLengthUnit();

            double waist = Units.toMetric(ReusableMethods.getTextInputEditTextValue(metWaist), smallLengthUnit);
            double neck = Units.toMetric(ReusableMethods.getTextInputEditTextValue(metNeck), smallLengthUnit);
            double hips = Units.toMetric(ReusableMethods.getTextInputEditTextValue(metHips), smallLengthUnit);

            double height = sharedPreferences.getFloat("Height", 0f);
            boolean male = sharedPreferences.getBoolean("Male", true);

            double baseValue = 0;

            if (height != 0 ) {
                if (waist != 0 && neck != 0) {
                    if (male) {
                        baseValue = 495 / (1.0324 - 0.19077 * Math.log10(waist - neck) + 0.15456 * Math.log10(height));
                    } else {
                        if (hips != 0) {
                            baseValue = 495 / (1.29579 - 0.35004 * Math.log10(waist + hips - neck) + 0.221 * Math.log10(height));
                        } else {
                            Toast.makeText(requireContext(), "Hips cannot be zero", Toast.LENGTH_SHORT).show();
                        }
                    }
                    metBodyFat.setText(decimalFormat.format(Math.max(0, baseValue - 450)));
                } else {
                    Toast.makeText(requireContext(), "Waist and neck cannot be zero", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Please first set your height in settings", Toast.LENGTH_SHORT).show();
            }
        });

        // Set listener to check if user wants to see old inputs
        view.findViewById(R.id.ib_auto_fill).setOnClickListener( v -> fillTextInputEditTexts(true));

        // Set listener to save entries to database
        view.findViewById(R.id.ib_save).setOnClickListener(v -> {

            // Create an entry and pass onto the database
            BodyJournalEntry bodyJournalEntry = new BodyJournalEntry(measurementDate,
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metCalfLeft), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metCalfRight), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metThighLeft), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metThighRight), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metForearmLeft), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metForearmRight), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metBicepLeft), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metBicepRight), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metHips), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metWaist), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metChest), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metShoulders), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metNeck), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metClavicles), smallLengthUnit),
                    Units.toMetric(ReusableMethods.getTextInputEditTextValue(metWeight), massUnit),
                    ReusableMethods.getTextInputEditTextValue(metBodyFat),
                    sharedPreferences.getFloat("Height", 0f)
            );

            Controller.getBodyJournalDataBase().addEntry(bodyJournalEntry);
            Toast.makeText(getContext(), "Measurements added for " + measurementDate, Toast.LENGTH_SHORT).show();
        });

        // Create motion listeners
        ReusableMethods.setScrollViewListener(view.findViewById(R.id.ncv), viewChangeListener);

        return view;
    }

    // Create a method to fetch data from database
    private void fillTextInputEditTexts (boolean latest) {

        // Fetch entry from database
        BodyJournalEntry bodyJournalEntry = Controller.getBodyJournalDataBase().getBodyJournalEntriesByDay(measurementDate, latest);

        // Fetch entry from database and leave empty if does not exist
        if (bodyJournalEntry != null) {
            metCalfLeft.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getLeftCalf(), smallLengthUnit)));
            metCalfRight.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getRightCalf(), smallLengthUnit)));
            metThighLeft.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getLeftThigh(), smallLengthUnit)));
            metThighRight.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getRightThigh(), smallLengthUnit)));
            metForearmLeft.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getLeftForearm(), smallLengthUnit)));
            metForearmRight.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getRightForearm(), smallLengthUnit)));
            metBicepLeft.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getLeftBicep(), smallLengthUnit)));
            metBicepRight.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getRightBicep(), smallLengthUnit)));
            metHips.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getHips(), smallLengthUnit)));
            metWaist.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getWaist(), smallLengthUnit)));
            metChest.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getChest(), smallLengthUnit)));
            metShoulders.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getShoulders(), smallLengthUnit)));
            metNeck.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getNeck(), smallLengthUnit)));
            metWeight.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getWeight(), massUnit)));
            metClavicles.setText(decimalFormat.format(Units.fromMetric(bodyJournalEntry.getClavicles(), smallLengthUnit)));
            metBodyFat.setText(decimalFormat.format(bodyJournalEntry.getBodyFat()));
        } else {
            for (Object[] objects : objectLists) {
                ((TextInputEditText) objects[3]).setText("");
            }
        }
    }

    // Create method to set date when it is changed
    @Override
    public void dateChanged(String date) {
        measurementDate = date;
        fillTextInputEditTexts(false);
    }

    // Create method to refill values when user resumes
    @Override
    public void onResume() {
        super.onResume();
        fillTextInputEditTexts(false);
    }
}