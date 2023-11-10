package com.pathfoss.vivoxia.exercise;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.ViewChangeListener;

import java.util.HashMap;
import java.util.Objects;

public class ExerciseCreator extends Fragment {

    private final ViewChangeListener viewChangeListener;

    private int typeIndex = 0;
    private final HashMap<Integer, Float> percentHashMap = new HashMap<>();

    private final String[] types = {"Weights", "Cardio"};
    private final String [] muscles = {
            "None",

            "Adductor (all)",
            "Adductor (deep)",
            "Adductor (longus)",
            "Adductor (magnus)",

            "Biceps (all)",
            "Biceps (brachialis)",
            "Biceps (inner)",
            "Biceps (outer)",

            "Calves (all)",
            "Calves (extensor)",
            "Calves (inner)",
            "Calves (outer)",
            "Calves (plantaris)",
            "Calves (popliteus)",
            "Calves (soleus)",
            "Calves (tibialis)",

            "Chest (all)",
            "Chest (deep)",
            "Chest (lower)",
            "Chest (upper)",

            "Core (all)",
            "Core (abdominal)",
            "Core (obliques)",
            "Core (serratus)",

            "Deltoids (all)",
            "Deltoids (back)",
            "Deltoids (front)",
            "Deltoids (side)",

            "Forearms (all)",
            "Forearms (brachioradialis)",
            "Forearms (extensors)",
            "Forearms (flexors)",

            "Groin (all)",
            "Groin (gracilis)",
            "Groin (iliacus)",
            "Groin (pectineus)",
            "Groin (psoas)",

            "Glutes (all)",
            "Glutes (maximus)",
            "Glutes (medius)",
            "Glutes (minimus)",

            "Hams (all)",
            "Hams (femoris)",
            "Hams (semimembranosus)",
            "Hams (semitendinosus)",

            "Lower back (all)",
            "Lower back (erector)",
            "Lower back (lats)",

            "Neck (all)",
            "Neck (back)",
            "Neck (front)",
            "Neck (side)",

            "Quadriceps (all)",
            "Quadriceps (femoris)",
            "Quadriceps (intermedius)",
            "Quadriceps (lateralis)",
            "Quadriceps (medialis)",

            "Trapezoids (all)",
            "Trapezoids (lower)",
            "Trapezoids (middle)",
            "Trapezoids (upper)",

            "Triceps (all)",
            "Triceps (lateral)",
            "Triceps (long)",
            "Triceps (medial)",

            "Upper Back (all)",
            "Upper Back (infraspinatus)",
            "Upper Back (rhomboids)",
            "Upper Back (supraspinatus)",

            "Other (clavicles)",
            "Other (masseters)",
    };

    // Create a constructor for setting UI change listener
    public ExerciseCreator (ViewChangeListener viewChangeListener) {
        this.viewChangeListener = viewChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate, define, and modify views
        View view = inflater.inflate(R.layout.fragment_exercise_creator, container, false);

        ConstraintLayout clMuscle1 = view.findViewById(R.id.cl_muscle1);
        ConstraintLayout clMuscle2 = view.findViewById(R.id.cl_muscle2);
        ConstraintLayout clMuscle3 = view.findViewById(R.id.cl_muscle3);
        ConstraintLayout clMuscle4 = view.findViewById(R.id.cl_muscle4);
        ConstraintLayout clMuscle5 = view.findViewById(R.id.cl_muscle5);
        ConstraintLayout clMuscle6 = view.findViewById(R.id.cl_muscle6);
        ConstraintLayout clMuscle7 = view.findViewById(R.id.cl_muscle7);
        ConstraintLayout clMuscle8 = view.findViewById(R.id.cl_muscle8);
        ConstraintLayout clMuscle9 = view.findViewById(R.id.cl_muscle9);
        ConstraintLayout clMuscle10 = view.findViewById(R.id.cl_muscle10);

        TextInputLayout tilName = view.findViewById(R.id.til_name);
        TextInputLayout tilType = view.findViewById(R.id.til_type);

        tilName.setHint("Name");
        tilType.setHint("Type");

        TextInputEditText nameMet = tilName.findViewById(R.id.met);
        nameMet.setInputType(InputType.TYPE_CLASS_TEXT);

        Object[][] objects = {
                {clMuscle1, clMuscle1.findViewById(R.id.til_left), clMuscle1.findViewById(R.id.til_right)},
                {clMuscle2, clMuscle2.findViewById(R.id.til_left), clMuscle2.findViewById(R.id.til_right)},
                {clMuscle3, clMuscle3.findViewById(R.id.til_left), clMuscle3.findViewById(R.id.til_right)},
                {clMuscle4, clMuscle4.findViewById(R.id.til_left), clMuscle4.findViewById(R.id.til_right)},
                {clMuscle5, clMuscle5.findViewById(R.id.til_left), clMuscle5.findViewById(R.id.til_right)},
                {clMuscle6, clMuscle6.findViewById(R.id.til_left), clMuscle6.findViewById(R.id.til_right)},
                {clMuscle7, clMuscle7.findViewById(R.id.til_left), clMuscle7.findViewById(R.id.til_right)},
                {clMuscle8, clMuscle8.findViewById(R.id.til_left), clMuscle8.findViewById(R.id.til_right)},
                {clMuscle9, clMuscle9.findViewById(R.id.til_left), clMuscle9.findViewById(R.id.til_right)},
                {clMuscle10, clMuscle10.findViewById(R.id.til_left), clMuscle10.findViewById(R.id.til_right)}
        };

        int[] indexList = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        HashMap<Integer, TextInputEditText> metHashMap = new HashMap<>();

        // Initialize all editable views
        for (int i = 0; i < objects.length; i++) {
            TextInputLayout tilMuscle = (TextInputLayout) objects[i][1];
            TextInputLayout tilPercent = ((TextInputLayout) objects[i][2]);
            TextInputEditText metPercent = tilPercent.findViewById(R.id.met);
            tilMuscle.setHint("Muscle");
            tilPercent.setHint("%");
            metPercent.setInputType(InputType.TYPE_CLASS_NUMBER);
            metHashMap.put(i, metPercent);
            int finalI = i;
            ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilMuscle.findViewById(R.id.act), muscles, indexList[i]).setOnItemClickListener((parent, view12, position, id) -> indexList[finalI] = position);
        }

        // Set listeners
        ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilType.findViewById(R.id.act), types, typeIndex).setOnItemClickListener((parent, view1, position, id) -> typeIndex = position);
        ReusableMethods.setScrollChangeListener(view.findViewById(R.id.ncv), viewChangeListener);

        ExerciseDataBase exerciseDataBase = Controller.getExerciseDataBase();

        view.findViewById(R.id.ib_add).setOnClickListener(v -> {
            String exerciseName = Objects.requireNonNull(nameMet.getText()).toString();

            // Save exercise in database if name is not empty and percentages add up
            if (!Objects.equals(exerciseName, "")) {

                Exercise existingExercise = exerciseDataBase.getExercise(exerciseName);

                double sum = 0;
                for (int i = 0; i < 10; i++) {
                    float value = ReusableMethods.getTextInputEditTextValue(metHashMap.get(i)) / 100;
                    percentHashMap.put(i, value);
                    sum += value;
                }

                if (sum == 0 || sum == 1) {
                    exerciseDataBase.addEntry(new Exercise(exerciseName,
                            types[typeIndex],
                            muscles[indexList[0]],
                            muscles[indexList[1]],
                            muscles[indexList[2]],
                            muscles[indexList[3]],
                            muscles[indexList[4]],
                            muscles[indexList[5]],
                            muscles[indexList[6]],
                            muscles[indexList[7]],
                            muscles[indexList[8]],
                            muscles[indexList[9]],
                            getFromHashMap(0),
                            getFromHashMap(1),
                            getFromHashMap(2),
                            getFromHashMap(3),
                            getFromHashMap(4),
                            getFromHashMap(5),
                            getFromHashMap(6),
                            getFromHashMap(7),
                            getFromHashMap(8),
                            getFromHashMap(9)
                    ));

                    if (existingExercise != null) {
                        Toast.makeText(requireContext(), exerciseName + " updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), exerciseName + " added", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Values must add up to 100% or be empty", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Please specify exercise exerciseName", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Create null-safe method to get values from HashMaps
    @NonNull
    private Float getFromHashMap(int key) {
        Float output = percentHashMap.get(key);
        return output != null ? output : 0f;
    }
}