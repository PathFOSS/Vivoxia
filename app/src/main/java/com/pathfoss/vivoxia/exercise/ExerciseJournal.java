package com.pathfoss.vivoxia.exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.DateChangeListener;
import com.pathfoss.vivoxia.general.EntryAddedListener;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.SelectorClickedListener;
import com.pathfoss.vivoxia.general.TopBarListener;
import com.pathfoss.vivoxia.general.Units;
import com.pathfoss.vivoxia.general.ViewChangeListener;

import java.util.HashMap;

public class ExerciseJournal extends Fragment implements DateChangeListener, EntryAddedListener {

    public static String measurementDate;

    private ViewGroup viewGroup;
    private LinearLayout linearLayout;
    private ImageButton ibDuplicate;
    private ImageButton ibDelete;
    private ImageButton ibPlay;

    private final ExerciseJournalDataBase exerciseJournalDataBase = Controller.getExerciseJournalDataBase();
    private final ViewChangeListener viewChangeListener;
    private final TopBarListener topBarListener;
    private final SelectorClickedListener selectorClickedListener;
    private final HashMap<Boolean, Integer> visibilityMap = new HashMap<>();

    private int exerciseSelectedID;
    private boolean exerciseSelected = false;

    // Create a constructor for setting UI change listener
    public ExerciseJournal (ViewChangeListener viewChangeListener, TopBarListener topBarListener, SelectorClickedListener selectorClickedListener) {
        this.viewChangeListener = viewChangeListener;
        this.topBarListener = topBarListener;
        this.selectorClickedListener = selectorClickedListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        measurementDate = Controller.getDataBaseDateFormat().format(System.currentTimeMillis());
        visibilityMap.put(true, View.VISIBLE);
        visibilityMap.put(false, View.GONE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate and define views
        View view = inflater.inflate(R.layout.fragment_exercise_journal, container, false);
        ibPlay = view.findViewById(R.id.ib_start);
        ibDuplicate = view.findViewById(R.id.ib_duplicate);
        ibDelete = view.findViewById(R.id.ib_delete);
        linearLayout = view.findViewById(R.id.ll);
        viewGroup = container;

        // Set listeners for views
        ReusableMethods.setScrollChangeListener(view.findViewById(R.id.ncv), viewChangeListener);
        view.findViewById(R.id.ib_add).setOnClickListener(v -> new ExerciseSelector(topBarListener, this, selectorClickedListener).show(getParentFragmentManager(), "Exercise Selector"));
        ibPlay.setOnClickListener( v -> {
            if (linearLayout.getChildCount() > 0) {
                Controller.changeGroupIDs(206);
                getParentFragmentManager().beginTransaction().replace(R.id.fcv_main, new ExerciseTimer(topBarListener)).commit();
            } else {
                Toast.makeText(requireContext(), "Add exercises before starting timer", Toast.LENGTH_SHORT).show();
            }
        });

        // Draw old entries
        drawEntries();
        return view;
    }

    // Create method to toggle visibility of buttons on user action
    private void toggleButtons (boolean selected) {

        ibDuplicate.setVisibility(getFromHashMap(selected));
        ibDelete.setVisibility(getFromHashMap(selected));
        ibPlay.setVisibility(getFromHashMap(!selected));

        ibDuplicate.setOnClickListener(null);
        ibDelete.setOnClickListener(null);
        exerciseSelected = selected;
    }

    // Create method to draw all journal entries from database from specific date
    private void drawEntries(){

        // Prepare container for entries
        linearLayout.removeAllViews();
        toggleButtons(false);

        // Iterate through journal entry list and draw each within a ConstraintLayout
        for (ExerciseJournalEntry exerciseJournalEntry : exerciseJournalDataBase.getEntriesForDate(measurementDate)) {

            ConstraintLayout child = (ConstraintLayout) getLayoutInflater().inflate(R.layout.journal_item, viewGroup, false);
            child.setId(exerciseJournalEntry.getRowID());

            String topLeftText = exerciseJournalEntry.getName();
            String topRightText = Units.decimalFormat.format(Units.fromMetric(exerciseJournalEntry.getWeight(), Units.getMassUnit())) + " " + Units.getMassUnit();
            String bottomLeftText = (int) exerciseJournalEntry.getReps() + " reps";
            String bottomRightText = "Rest " + (int) exerciseJournalEntry.getRest() + " s   |   Work " +  (int) exerciseJournalEntry.getWork() + " s";

            ((TextView) child.findViewById(R.id.tv_top_left)).setText(topLeftText);
            ((TextView) child.findViewById(R.id.tv_top_right)).setText(topRightText);
            ((TextView) child.findViewById(R.id.tv_bottom_left)).setText(bottomLeftText);
            ((TextView) child.findViewById(R.id.tv_bottom_right)).setText(bottomRightText);

            linearLayout.addView(child);
            createExerciseEntryViewListener(child);
        }
    }

    // Create method to enable duplication and deletion
    private void setSelectedListeners (ConstraintLayout constraintLayout) {
        toggleButtons(true);

        ibDuplicate.setOnClickListener( v -> {
            exerciseJournalDataBase.duplicateEntry(exerciseSelectedID);
            Toast.makeText(getContext(), "Duplicated", Toast.LENGTH_SHORT).show();
            toggleButtons(false);
            ReusableMethods.colorJournalTextViews(constraintLayout.findViewById(R.id.tv_top_left), constraintLayout.findViewById(R.id.tv_top_right), Controller.getColorNaturalWhite());
             drawEntries();
        });

        ibDelete.setOnClickListener( v -> {
            exerciseJournalDataBase.deleteEntry(exerciseSelectedID);
            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            linearLayout.removeView(constraintLayout);
            toggleButtons(false);
            ReusableMethods.colorJournalTextViews(constraintLayout.findViewById(R.id.tv_top_left), constraintLayout.findViewById(R.id.tv_top_right), Controller.getColorNaturalWhite());
        });

    }

    // Create method to set listeners for each drawn entry container
    private void createExerciseEntryViewListener(@NonNull ConstraintLayout exerciseItemLayout){
        exerciseItemLayout.setOnClickListener(v -> {
            if (!exerciseSelected) {
                toggleButtons(true);
                exerciseSelectedID = exerciseItemLayout.getId();
                ReusableMethods.colorJournalTextViews(exerciseItemLayout.findViewById(R.id.tv_top_left), exerciseItemLayout.findViewById(R.id.tv_top_right), Controller.getColorVivoxia());
                setSelectedListeners(exerciseItemLayout);
            } else if (exerciseSelectedID == exerciseItemLayout.getId()) {
                Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                toggleButtons(false);
                ReusableMethods.colorJournalTextViews(exerciseItemLayout.findViewById(R.id.tv_top_left), exerciseItemLayout.findViewById(R.id.tv_top_right), Controller.getColorNaturalWhite());
            } else if (exerciseSelected) {
                Toast.makeText(getContext(), "Swapped entries", Toast.LENGTH_SHORT).show();
                exerciseJournalDataBase.swapEntries(exerciseSelectedID, exerciseItemLayout.getId());
                toggleButtons(false);
                ReusableMethods.colorJournalTextViews(exerciseItemLayout.findViewById(R.id.tv_top_left), exerciseItemLayout.findViewById(R.id.tv_top_right), Controller.getColorNaturalWhite());
                drawEntries();
            }
        });
    }

    // Create null-safe method to get values from HashMaps
    @NonNull
    private Integer getFromHashMap(boolean key) {
        Integer output = visibilityMap.get(key);
        return output != null ? output : 0;
    }

    // Create getter method for date
    public static String getMeasurementDate() {
        return measurementDate;
    }

    // Implement change of date listener to refresh view
    @Override
    public void dateChanged(String date) {
        measurementDate = date;
        drawEntries();
    }

    // Implement entry added listener to refresh view
    @Override
    public void onEntryAdded() {
        drawEntries();
    }
}