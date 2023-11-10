package com.pathfoss.vivoxia.exercise;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDataBase extends SQLiteOpenHelper {

    private final String EXERCISE_ITEMS_TABLE = "EXERCISE_ITEMS_TABLE";
    private final String COL_NAME = "NAME";
    private final String COL_TYPE = "TYPE";
    
    private final String COL_BODY_PART_1 = "BODY_PART_1";
    private final String COL_BODY_PART_2 = "BODY_PART_2";
    private final String COL_BODY_PART_3 = "BODY_PART_3";
    private final String COL_BODY_PART_4 = "BODY_PART_4";
    private final String COL_BODY_PART_5 = "BODY_PART_5";
    private final String COL_BODY_PART_6 = "BODY_PART_6";
    private final String COL_BODY_PART_7 = "BODY_PART_7";
    private final String COL_BODY_PART_8 = "BODY_PART_8";
    private final String COL_BODY_PART_9 = "BODY_PART_9";
    private final String COL_BODY_PART_10 = "BODY_PART_10";
    
    private final String COL_PERCENT_1 = "PERCENT_1";
    private final String COL_PERCENT_2 = "PERCENT_2";
    private final String COL_PERCENT_3 = "PERCENT_3";
    private final String COL_PERCENT_4 = "PERCENT_4";
    private final String COL_PERCENT_5 = "PERCENT_5";
    private final String COL_PERCENT_6 = "PERCENT_6";
    private final String COL_PERCENT_7 = "PERCENT_7";
    private final String COL_PERCENT_8 = "PERCENT_8";
    private final String COL_PERCENT_9 = "PERCENT_9";
    private final String COL_PERCENT_10 = "PERCENT_10";

    public ExerciseDataBase(@Nullable Context context) {super(context, "exercise_items_table.db", null, 1);}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    @Override
    public void onCreate(@NonNull SQLiteDatabase sqLiteDatabase) {

        // Create table if does not exist
        sqLiteDatabase.execSQL("CREATE TABLE "
                + EXERCISE_ITEMS_TABLE + " ("
                + COL_NAME + " TEXT PRIMARY KEY,"
                + COL_TYPE + " NUMERIC,"

                + COL_BODY_PART_1 + " TEXT,"
                + COL_BODY_PART_2 + " TEXT,"
                + COL_BODY_PART_3 + " TEXT,"
                + COL_BODY_PART_4 + " TEXT,"
                + COL_BODY_PART_5 + " TEXT,"
                + COL_BODY_PART_6 + " TEXT,"
                + COL_BODY_PART_7 + " TEXT,"
                + COL_BODY_PART_8 + " TEXT,"
                + COL_BODY_PART_9 + " TEXT,"
                + COL_BODY_PART_10 + " TEXT,"

                + COL_PERCENT_1 + " NUMERIC,"
                + COL_PERCENT_2 + " NUMERIC,"
                + COL_PERCENT_3 + " NUMERIC, "
                + COL_PERCENT_4 + " NUMERIC, "
                + COL_PERCENT_5 + " NUMERIC, "
                + COL_PERCENT_6 + " NUMERIC,"
                + COL_PERCENT_7 + " NUMERIC,"
                + COL_PERCENT_8 + " NUMERIC, "
                + COL_PERCENT_9 + " NUMERIC, "
                + COL_PERCENT_10 + " NUMERIC)"
        );
    }

    // Create method to delete entry and update it
    public void addEntry(@NonNull Exercise exercise) {
        
        getWritableDatabase().execSQL("DELETE FROM " + EXERCISE_ITEMS_TABLE + " WHERE " + COL_NAME + "='" + exercise.getName() + "'");
        getWritableDatabase().execSQL("INSERT INTO " + EXERCISE_ITEMS_TABLE + " ("
                + COL_NAME + ", "
                + COL_TYPE + ", "
                + COL_BODY_PART_1 + ", "
                + COL_BODY_PART_2 + ", "
                + COL_BODY_PART_3 + ", "
                + COL_BODY_PART_4 + ", "
                + COL_BODY_PART_5 + ", "
                + COL_BODY_PART_6 + ", "
                + COL_BODY_PART_7 + ", "
                + COL_BODY_PART_8 + ", "
                + COL_BODY_PART_9 + ", "
                + COL_BODY_PART_10 + ", "
                + COL_PERCENT_1 + ", "
                + COL_PERCENT_2 + ", "
                + COL_PERCENT_3 + ", "
                + COL_PERCENT_4 + ", "
                + COL_PERCENT_5 + ", "
                + COL_PERCENT_6 + ", "
                + COL_PERCENT_7 + ", "
                + COL_PERCENT_8 + ", "
                + COL_PERCENT_9 + ", "
                + COL_PERCENT_10 + ") "
                + "VALUES ('"
                + exercise.getName() + "', '"
                + exercise.getType() + "', '"
                + exercise.getBodyPart1() + "', '"
                + exercise.getBodyPart2() + "', '"
                + exercise.getBodyPart3() + "', '"
                + exercise.getBodyPart4() + "', '"
                + exercise.getBodyPart5() + "', '"
                + exercise.getBodyPart6() + "', '"
                + exercise.getBodyPart7() + "', '"
                + exercise.getBodyPart8() + "', '"
                + exercise.getBodyPart9() + "', '"
                + exercise.getBodyPart10() + "', "
                + exercise.getPercent1() + ", "
                + exercise.getPercent2() + ", "
                + exercise.getPercent3() + ", "
                + exercise.getPercent4() + ", "
                + exercise.getPercent5() + ", "
                + exercise.getPercent6() + ", "
                + exercise.getPercent7() + ", "
                + exercise.getPercent8() + ", "
                + exercise.getPercent9() + ", "
                + exercise.getPercent10() + ")"
        );
    }

    // Create method to return an Exercise object when requested
    public Exercise getExercise (String queriedName) {
        Exercise exercise;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + EXERCISE_ITEMS_TABLE + " WHERE " + COL_NAME + "='" + queriedName + "'", null);

        if (cursor.moveToFirst()) {
            exercise = getExerciseFromCursor(cursor);
        } else {
            exercise = null;
        }
        cursor.close();
        return exercise;
    }

    // Create method to fetch all exercises as a list object
    public List<Exercise> getExerciseList () {
        List<Exercise> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + EXERCISE_ITEMS_TABLE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                returnList.add(getExerciseFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    // Create method to return an Exercise object from cursor
    @NonNull
    @Contract("_ -> new")
    private Exercise getExerciseFromCursor(@NonNull Cursor cursor) {
        return new Exercise(cursor.getString(0),
        cursor.getString(1),
        cursor.getString(2),
        cursor.getString(3),
        cursor.getString(4),
        cursor.getString(5),
        cursor.getString(6),
        cursor.getString(7),
        cursor.getString(8),
        cursor.getString(9),
        cursor.getString(10),
        cursor.getString(11),
        cursor.getFloat(12),
        cursor.getFloat(13),
        cursor.getFloat(14),
        cursor.getFloat(15),
        cursor.getFloat(16),
        cursor.getFloat(17),
        cursor.getFloat(18),
        cursor.getFloat(19),
        cursor.getFloat(20),
        cursor.getFloat(21));
    }
}
