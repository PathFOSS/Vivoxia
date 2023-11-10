package com.pathfoss.vivoxia.general;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pathfoss.vivoxia.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Objects;

public class EditDate extends DialogFragment {

    private final DateChangeListener dateChangeListener;
    private final TopBarListener topBarListener;
    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    private long time;

    // Create constructor method
    public EditDate(Context context, DateChangeListener dateChangeListener, long time) {
        this.dateChangeListener = dateChangeListener;
        this.topBarListener = (TopBarListener) context;
        this.time = time;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_dialog_edit_date, container, false);

        // Set listener for the CalendarView
        CalendarView calendarView = view.findViewById(R.id.cv);
        calendarView.setDate(time);
        calendarView.setOnDateChangeListener((viewLayout, year, month, dayOfMonth) -> {
            String rawDate = year + "-" + decimalFormat.format(month + 1) + "-" + decimalFormat.format(dayOfMonth);
            dateChangeListener.dateChanged(rawDate);
            try {
                time = Objects.requireNonNull(Controller.getDataBaseDateFormat().parse(rawDate)).getTime();
                topBarListener.textChanged(Controller.getTopBarDateFormat().format(Objects.requireNonNull(Controller.getDataBaseDateFormat().parse(rawDate))));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            dismiss();
        });
        return view;
    }

    // Create method to set dialog window parameters
    @Override
    public void onResume() {
        super.onResume();
        ReusableMethods.setDialogWindowParameters(Objects.requireNonNull(getDialog()));
    }
}