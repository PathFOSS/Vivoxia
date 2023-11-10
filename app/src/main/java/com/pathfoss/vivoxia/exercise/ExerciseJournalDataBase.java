package com.pathfoss.vivoxia.exercise;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ExerciseJournalDataBase extends SQLiteOpenHelper {

    private final String EXERCISE_JOURNAL_TABLE = "EXERCISE_JOURNAL_TABLE";
    private final String COL_UPDATED_ID = "UPDATED_ID";
    private final String COL_DATE = "DATE";
    private final String COL_NAME = "NAME";
    private final String COL_REPS = "REPS";
    private final String COL_WEIGHT = "WEIGHT";
    private final String COL_REST = "REST";
    private final String COL_WORK = "WORK";
    private final String COL_EFFORT = "EFFORT";

    public ExerciseJournalDataBase (@Nullable Context context) {super(context, "exercise_journal_table.db", null, 1);}

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    @Override
    public void onCreate (@NonNull SQLiteDatabase sqLiteDatabase) {

        // Create table if does not exist
        sqLiteDatabase.execSQL("CREATE TABLE " + EXERCISE_JOURNAL_TABLE
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_UPDATED_ID + " INTEGER, "
                + COL_DATE + " TEXT, "
                + COL_NAME + " TEXT, "
                + COL_REPS + " NUMERIC, "
                + COL_WEIGHT + " NUMERIC, "
                + COL_REST + " NUMERIC, "
                + COL_WORK + " NUMERIC, "
                + COL_EFFORT + " NUMERIC)"
        );
    }
    
    // Create method to add entries based on added set number
    public void addEntry (String date, String name, float sets, float reps, float weight, float rest, float work, float effort) {
        for (int i = 0; i < sets; i++) {
            getWritableDatabase().execSQL("INSERT INTO " + EXERCISE_JOURNAL_TABLE + " ("
                    + COL_UPDATED_ID + ", "
                    + COL_DATE + ", "
                    + COL_NAME + ", "
                    + COL_REPS + ", "
                    + COL_WEIGHT + ", "
                    + COL_REST + ", "
                    + COL_WORK + ", "
                    + COL_EFFORT + ") "
                    + "VALUES ("
                    + "COALESCE((SELECT MAX(ID) + 1 FROM " + EXERCISE_JOURNAL_TABLE + "), 1), "
                    + "'" + date + "', "
                    + "'" + name + "', "
                    + reps + ", "
                    + weight + ", "
                    + rest + ", "
                    + work + ", "
                    + effort + ")"
            );
        }
    }
    
    // Create method to delete entries
    public void deleteEntry (Integer rowID) {
        getWritableDatabase().execSQL("DELETE FROM " + EXERCISE_JOURNAL_TABLE + " WHERE " + COL_UPDATED_ID + " = " +  rowID);
    }
    
    // Create method to duplicate entries
    public void duplicateEntry (Integer rowID) {
        getWritableDatabase().execSQL("INSERT INTO " + EXERCISE_JOURNAL_TABLE + " ("
                + COL_UPDATED_ID + ", "
                + COL_DATE + ", "
                + COL_NAME + ", "
                + COL_REPS + ", "
                + COL_WEIGHT + ", "
                + COL_REST + ", "
                + COL_WORK + ", "
                + COL_EFFORT + ") "
                + " SELECT "
                + "(SELECT MAX(ID) + 1 FROM " + EXERCISE_JOURNAL_TABLE + "), "
                + COL_DATE + ", "
                + COL_NAME + ", "
                + COL_REPS + ", "
                + COL_WEIGHT + ", "
                + COL_REST + ", "
                + COL_WORK + ", "
                + COL_EFFORT
                + " FROM " + EXERCISE_JOURNAL_TABLE
                + " WHERE " + COL_UPDATED_ID + " = " + rowID
                + " LIMIT 1"
        );
    }

    // Create method to update entries
    public void updateEntry (@NonNull ExerciseJournalEntry exerciseJournalEntry, float reps, float weight, float effort) {
        getWritableDatabase().execSQL("UPDATE " + EXERCISE_JOURNAL_TABLE
                + " SET " + COL_REPS + " = " + reps + ", "
                + COL_WEIGHT + " = " + weight + ", "
                + COL_EFFORT + " = " + effort
                + " WHERE " + COL_UPDATED_ID + " = " + exerciseJournalEntry.getRowID()
        );
    }

    // Create method to swap entries
    public void swapEntries (int rowID1, int rowID2) {
        getWritableDatabase().execSQL("UPDATE " + EXERCISE_JOURNAL_TABLE
                + " SET " + COL_UPDATED_ID + " = CASE WHEN " + COL_UPDATED_ID + " = " + rowID1 + " THEN " + rowID2 + " ELSE " + rowID1
                + " END WHERE " + COL_UPDATED_ID + " IN (" + rowID1 + ", " + rowID2 + ")"
        );
    }

    // Create method to get entries for a specific date
    public List<ExerciseJournalEntry> getEntriesForDate (String date) {

        // Fetch entries from database
        List<ExerciseJournalEntry> exerciseJournalEntryList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + EXERCISE_JOURNAL_TABLE + " WHERE " + COL_DATE + " = '" + date + "' ORDER BY " + COL_UPDATED_ID + ", ID", null);

        // Create ExerciseJournalEntry objects from database and add to the return list
        if (cursor.moveToFirst()) {
            do {
                exerciseJournalEntryList.add(new ExerciseJournalEntry(cursor.getInt(1),
                        cursor.getString(3),
                        cursor.getFloat(4),
                        cursor.getFloat(5),
                        cursor.getFloat(6),
                        cursor.getFloat(7),
                        cursor.getFloat(8)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return  exerciseJournalEntryList;
    }

    // Create a method to get customized data based on user input
    public LinkedHashMap<String, Float> getExerciseJournalEntriesByQuery (@NonNull String COL_QUERIED) {
        switch (COL_QUERIED) {
            case "Workouts / Week":
                return getWorkoutsPerWeekList();
            case "Volume / Workout":
                return getWorkoutVolumeList();
            case "Reps / Set":
                return getAverageEntryList(COL_REPS);
            case "Sets / Workout":
                return getCountEntryList("*");
            case "Workout Time":
                return getWorkoutTimeList();
            case "Rest / Set":
                return getAverageEntryList(COL_REST);
            case "Work / Set":
                return getAverageEntryList(COL_WORK);
            case "Effort / Set":
                return getAverageEntryList(COL_EFFORT);
            default:
                return null;
        }
    }

    // Create a method to get average measures per day
    public LinkedHashMap<String, Float> getAverageEntryList (String COL_QUERIED) {

        LinkedHashMap<String, Float> map = new LinkedHashMap<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT " + COL_DATE + ", AVG(" + COL_QUERIED + ")" + " FROM " + EXERCISE_JOURNAL_TABLE + " GROUP BY " + COL_DATE + " ORDER BY " + COL_DATE + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getString(0), cursor.getFloat(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return  map;
    }

    // Create a method to get count measures per day
    public LinkedHashMap<String, Float> getCountEntryList (String COL_QUERIED) {

        LinkedHashMap<String, Float> map = new LinkedHashMap<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT " + COL_DATE + ", COUNT(" + COL_QUERIED + ")" + " FROM " + EXERCISE_JOURNAL_TABLE + " GROUP BY " + COL_DATE + " ORDER BY " + COL_DATE + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getString(0), cursor.getFloat(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return  map;
    }

    // Create a method to get workout time per day
    public LinkedHashMap<String, Float> getWorkoutTimeList () {

        LinkedHashMap<String, Float> map = new LinkedHashMap<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT " + COL_DATE + ", SUM(" + COL_REST + "), " + "SUM(" + COL_WORK + ") FROM " + EXERCISE_JOURNAL_TABLE + " GROUP BY " + COL_DATE + " ORDER BY " + COL_DATE + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getString(0), (cursor.getFloat(1) + cursor.getFloat(2)) / 60);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return  map;
    }

    // Create a method to get count measures per day
    public LinkedHashMap<String, Float> getWorkoutVolumeList () {

        LinkedHashMap<String, Float> map = new LinkedHashMap<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT " + COL_DATE + ", SUM(" + COL_REPS + "*" + COL_WEIGHT + ") AS VOLUME FROM " + EXERCISE_JOURNAL_TABLE + " GROUP BY " + COL_DATE + " ORDER BY " + COL_DATE + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getString(0), cursor.getFloat(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return  map;
    }

    // Create a method to get count measures per day
    public LinkedHashMap<String, Float> getWorkoutsPerWeekList () {

        LinkedHashMap<String, Float> map = new LinkedHashMap<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT STRFTIME('%W'," + COL_DATE + "), COUNT(" + COL_DATE + ") FROM (SELECT " + COL_DATE + " FROM " + EXERCISE_JOURNAL_TABLE + " GROUP BY " + COL_DATE + ") GROUP BY STRFTIME('%W'," + COL_DATE + ")", null);

        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getString(0), cursor.getFloat(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return  map;
    }
}