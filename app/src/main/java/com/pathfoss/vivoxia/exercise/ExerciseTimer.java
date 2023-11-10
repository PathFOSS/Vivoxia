package com.pathfoss.vivoxia.exercise;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.general.Controller;
import com.pathfoss.vivoxia.general.ReusableMethods;
import com.pathfoss.vivoxia.general.TopBarListener;
import com.pathfoss.vivoxia.general.Units;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class ExerciseTimer extends Fragment {

    private TextView tvExercise;
    private TextView tvStatus;
    private TextView tvTime;
    private TextView tvRepsValue;
    private TextView tvEffortValue;
    private TextView tvWeightValue;
    private ImageButton prevBtn;
    private ImageButton pauseBtn;
    private ImageButton nextBtn;
    private TextInputLayout tilReps;
    private TextInputLayout tilWeight;
    private TextInputLayout tilEffort;
    private TextInputEditText metReps;
    private TextInputEditText metWeight;
    private AutoCompleteTextView actEffort;
    private MediaPlayer restMediaPlayer;
    private MediaPlayer workMediaPlayer;
    private MediaPlayer endMediaPlayer;

    private final ExerciseJournalDataBase exerciseJournalDataBase = Controller.getExerciseJournalDataBase();
    private final HashMap<Boolean, String> restWorkMap = new HashMap<>();
    private final HashMap<Boolean, Integer> visibilityMap = new HashMap<>();
    private final String [] effortList = {"0 / 10", "1 / 10", "2 / 10", "3 / 10", "4 / 10", "5 / 10", "6 / 10", "7 / 10", "8 / 10", "9 / 10", "10 / 10"};
    private final String date = ExerciseJournal.getMeasurementDate();
    private final Handler handler = new Handler();
    private final TopBarListener topBarListener;
    private final boolean soundEnabled = Controller.getSharedPreferences().getBoolean("Sound", true);

    private LinkedHashMap<Integer, ExerciseJournalEntry> exerciseJournalEntryMap;
    private TextInputLayout[] textInputLayouts;

    private int counter = 0;
    private int effortIndex = 0;
    private int currentIndex = 0;
    private float timeLeft = 0;

    private boolean resting = true;
    private boolean paused = false;
    private boolean repsVisible = false;
    private boolean effortVisible = false;
    private boolean weightVisible = false;

    public ExerciseTimer (TopBarListener topBarListener) {
        this.topBarListener = topBarListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String restText = "Rest";
        String workText = "Work";
        restWorkMap.put(true, restText);
        restWorkMap.put(false, workText);
        visibilityMap.put(true, View.VISIBLE);
        visibilityMap.put(false, View.GONE);
        restMediaPlayer = MediaPlayer.create(requireContext(), R.raw.alien);
        workMediaPlayer = MediaPlayer.create(requireContext(), R.raw.bang);
        endMediaPlayer = MediaPlayer.create(requireContext(), R.raw.chime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate, define, and modify views
        View view = inflater.inflate(R.layout.fragment_exercise_timer, container, false);

        tilReps = view.findViewById(R.id.til_reps);
        tilWeight = view.findViewById(R.id.til_weight);
        tilEffort = view.findViewById(R.id.til_effort);

        textInputLayouts = new TextInputLayout[]{tilReps, tilWeight, tilEffort};

        tvRepsValue = view.findViewById(R.id.tv_reps_value);
        tvEffortValue = view.findViewById(R.id.tv_effort_value);
        tvWeightValue = view.findViewById(R.id.tv_weight_value);

        tvExercise = view.findViewById(R.id.tv_exercise);
        tvStatus = view.findViewById(R.id.tv_status);
        tvTime = view.findViewById(R.id.tv_time);

        metReps = tilReps.findViewById(R.id.met);
        metWeight = tilWeight.findViewById(R.id.met);

        prevBtn = view.findViewById(R.id.ib_previous);
        pauseBtn = view.findViewById(R.id.ib_pause);
        nextBtn = view.findViewById(R.id.ib_next);

        tilEffort.setHint("Effort");
        tilReps.setHint("Reps");
        tilWeight.setHint("Weight (" + Units.getMassUnit() + ")");

        actEffort = ReusableMethods.setAutoCompleteTextViewParameters(requireContext(), tilEffort.findViewById(R.id.act), effortList, 0);
        actEffort.setOnItemClickListener((parent, view1, position, id) -> {
            effortIndex = position;
            tvEffortValue.setText(effortList[effortIndex]);
            checkDataBaseUpdate(currentIndex);
        });

        refreshJournalList();
        topBarListener.textChanged("Set 1 / " + counter);

        // Initialize the timer value
        timeLeft = Objects.requireNonNull(exerciseJournalEntryMap.get(currentIndex)).getRest();

        setButtonListeners();
        updateEntryDataUI();

        startTimer();

        // Create editable listeners
        metWeight.setOnFocusChangeListener((v, hasFocus) -> {
            String weightText = Units.decimalFormat.format(ReusableMethods.getTextInputEditTextValue(metWeight)) + " " + Units.getMassUnit();
            tvWeightValue.setText(weightText);
            checkDataBaseUpdate(currentIndex);
        });

        metReps.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                tvRepsValue.setText(String.valueOf((int) ReusableMethods.getTextInputEditTextValue(metReps)));
                checkDataBaseUpdate(currentIndex);
            }
        });

        return view;
    }

    // Create a method to update the exercise journal list when an item is updated
    private void refreshJournalList () {
        List<ExerciseJournalEntry> entries = exerciseJournalDataBase.getEntriesForDate(date);
        exerciseJournalEntryMap = new LinkedHashMap<>();

        counter = 0;
        for (ExerciseJournalEntry exerciseJournalEntry : entries) {
            exerciseJournalEntryMap.put(counter, exerciseJournalEntry);
            counter++;
        }
    }

    // Create method to set view texts based on status
    private void updateEntryDataUI() {
        String repsText = String.valueOf((int) Objects.requireNonNull(exerciseJournalEntryMap.get(currentIndex)).getReps());
        String effortText = (int) Objects.requireNonNull(exerciseJournalEntryMap.get(currentIndex)).getEffort() + " / 10";
        String weightText = Units.decimalFormat.format(Units.fromMetric(Objects.requireNonNull(exerciseJournalEntryMap.get(currentIndex)).getWeight(), Units.getMassUnit()));
        String weightText2 = weightText + " " + Units.getMassUnit();
        actEffort.setText(effortText, false);
        metReps.setText(repsText);
        metWeight.setText(weightText);

        tvRepsValue.setText(repsText);
        tvEffortValue.setText(effortText);
        tvWeightValue.setText(weightText2);

        tvExercise.setText(Objects.requireNonNull(exerciseJournalEntryMap.get(currentIndex)).getName());
    }

    // Create method to set TextViews based on time
    private void updateTimerView () {
        tvTime.setText(String.valueOf((int) timeLeft));
        tvStatus.setText(restWorkMap.get(resting));
    }

    private void updateTimeOnSkip () {
        resting = !resting;
        if (!resting) {
            timeLeft = Objects.requireNonNull(exerciseJournalEntryMap.get(currentIndex)).getWork();
        } else {
            timeLeft = Objects.requireNonNull(exerciseJournalEntryMap.get(currentIndex)).getRest();
        }
    }

    // Create method to set button listeners
    private void setButtonListeners() {

        prevBtn.setOnClickListener( v -> {
            checkDataBaseUpdate(currentIndex);

            if (currentIndex > 0 || !resting) {
                if (resting) {
                    currentIndex--;
                    topBarListener.textChanged("Set " + (currentIndex + 1) + " / " + counter);
                }
                updateTimeOnSkip();
                updateEntryDataUI();
                handler.removeCallbacksAndMessages(null);
                pauseBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_pause));
                paused = false;
                startTimer();
            }
        });

        nextBtn.setOnClickListener( v -> {
            checkDataBaseUpdate(currentIndex);

            if (exerciseJournalEntryMap.size() > currentIndex + 1 || resting) {
                if (!resting) {
                    currentIndex++;
                    topBarListener.textChanged("Set " + (currentIndex + 1) + " / " + counter);
                }
                updateTimeOnSkip();
                updateEntryDataUI();
                handler.removeCallbacksAndMessages(null);
                pauseBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_pause));
                paused = false;
                startTimer();
            }
        });

        pauseBtn.setOnClickListener( v -> {
            paused = !paused;
            if (paused) {
                pauseBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_play_arrow));
                handler.removeCallbacksAndMessages(null);
            } else {
                pauseBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.icon_pause));
                startTimer();
            }
        });

        tvRepsValue.setOnClickListener( v -> {
            toggleEditableViews(tilReps, repsVisible);
            repsVisible = !repsVisible;
        });

        tvEffortValue.setOnClickListener( v -> {
            toggleEditableViews(tilEffort, effortVisible);
            effortVisible = !effortVisible;
        });

        tvWeightValue.setOnClickListener( v -> {
            toggleEditableViews(tilWeight, weightVisible);
            weightVisible = !weightVisible;
        });
    }

    // Create method to prevent toggle edit input box overlap
    private void toggleEditableViews (@NonNull TextInputLayout currentTextInputLayout, boolean visible) {
        currentTextInputLayout.setVisibility(getFromHashMap(visible));
        for (TextInputLayout textInputLayout : textInputLayouts) {
            if (!textInputLayout.equals(currentTextInputLayout)) {
                textInputLayout.setVisibility(getFromHashMap(false));
            }
        }
    }

    // Create null-safe method to get values from HashMaps
    @NonNull
    private Integer getFromHashMap(boolean key) {
        Integer output = visibilityMap.get(key);
        return output != null ? output : 0;
    }

    // Create method to start timer with handler
    private void startTimer() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                timeLeft -= 1;

                if (timeLeft <= 0) {
                    resting = !resting;
                    if (resting) {
                        if (exerciseJournalEntryMap.size() > currentIndex + 1) {
                            toggleEditableViews(tilReps, false);
                            checkDataBaseUpdate(currentIndex);
                            currentIndex++;
                            topBarListener.textChanged("Set " + (currentIndex + 1) + " / " + counter);
                            timeLeft = Objects.requireNonNull(exerciseJournalEntryMap.get(currentIndex)).getRest();
                            updateEntryDataUI();
                            if (soundEnabled) {
                                restMediaPlayer.start();
                            }
                            updateTimerView();
                            handler.postDelayed(this, 1000);
                        } else {
                            toggleEditableViews(tilReps, false);
                            checkDataBaseUpdate(currentIndex);
                            handler.removeCallbacksAndMessages(null);
                            tvTime.setText("0");
                            tvStatus.setText(getString(R.string.at_end));
                            if (soundEnabled) {
                                endMediaPlayer.start();
                            }
                            Toast.makeText(requireContext(), "Workout finished", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (soundEnabled) {
                            workMediaPlayer.start();
                        }

                        timeLeft = Objects.requireNonNull(exerciseJournalEntryMap.get(currentIndex)).getWork();
                        updateTimerView();

                        handler.postDelayed(this, 1000);
                    }
                } else {
                    updateTimerView();
                    handler.postDelayed(this, 1000);
                }
            }
        };
        updateTimerView();
        handler.postDelayed(runnable, 1000);
    }

    // Create method to see if database needs updating
    private void checkDataBaseUpdate (int index) {
        float effort = effortIndex;
        float reps = ReusableMethods.getTextInputEditTextValue(metReps);
        float weight = Units.toMetric(ReusableMethods.getTextInputEditTextValue(metWeight), Units.getMassUnit());
        ExerciseJournalEntry exerciseJournalEntry = exerciseJournalEntryMap.get(index);
        assert exerciseJournalEntry != null;
        if (reps != exerciseJournalEntry.getReps() || weight != exerciseJournalEntry.getWeight() || effort != exerciseJournalEntry.getEffort()) {
            exerciseJournalDataBase.updateEntry(exerciseJournalEntry, reps, weight, effort);
            refreshJournalList();
        }
    }

    // Create method to prevent handler from running the timer in the background
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        Toast.makeText(requireContext(), "Workout ended", Toast.LENGTH_SHORT).show();
    }
}