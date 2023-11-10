package com.pathfoss.vivoxia.body;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;

public class BodyJournalDataBase extends SQLiteOpenHelper {

    public final String BODY_JOURNAL_TABLE = "BODY_JOURNAL_TABLE";
    public final String COL_DATE = "DATE";
    public final String COL_LEFT_CALF = "LEFT_CALF";
    public final String COL_RIGHT_CALF = "RIGHT_CALF";
    public final String COL_LEFT_THIGH = "LEFT_THIGH";
    public final String COL_RIGHT_THIGH = "RIGHT_THIGH";
    public final String COL_LEFT_FOREARM = "LEFT_FOREARM";
    public final String COL_RIGHT_FOREARM = "RIGHT_FOREARM";
    public final String COL_LEFT_BICEP = "LEFT_BICEP";
    public final String COL_RIGHT_BICEP = "RIGHT_BICEP";
    public final String COL_HIPS = "HIPS";
    public final String COL_WAIST = "WAIST";
    public final String COL_CHEST = "CHEST";
    public final String COL_SHOULDERS = "SHOULDERS";
    public final String COL_NECK = "NECK";
    public final String COL_WEIGHT = "WEIGHT";
    public final String COL_CLAVICLES = "CLAVICLES";
    public final String COL_HEIGHT = "HEIGHT";
    public final String COL_BODY_FAT = "BODY_FAT";

    public BodyJournalDataBase(@Nullable Context context) {
        super(context, "body_journal_table.db", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
    
    @Override
    public void onCreate(@NonNull SQLiteDatabase sqLiteDatabase) {

        // Create table if does not exist
        sqLiteDatabase.execSQL("CREATE TABLE " + BODY_JOURNAL_TABLE + " ("
                + COL_DATE + " TEXT PRIMARY KEY, "
                + COL_LEFT_CALF + " NUMERIC, "  
                + COL_RIGHT_CALF + " NUMERIC, "  
                + COL_LEFT_THIGH + " NUMERIC, "  
                + COL_RIGHT_THIGH + " NUMERIC, "  
                + COL_LEFT_FOREARM + " NUMERIC, "  
                + COL_RIGHT_FOREARM  + " NUMERIC, "  
                + COL_LEFT_BICEP + " NUMERIC, "  
                + COL_RIGHT_BICEP + " NUMERIC, "  
                + COL_HIPS + " NUMERIC, "  
                + COL_WAIST + " NUMERIC, "  
                + COL_CHEST + " NUMERIC, "  
                + COL_SHOULDERS + " NUMERIC, "  
                + COL_NECK + " NUMERIC, "
                + COL_CLAVICLES + " NUMERIC,"
                + COL_WEIGHT +  " NUMERIC, "
                + COL_BODY_FAT + " NUMERIC, "
                + COL_HEIGHT +  " NUMERIC)"
        );
    }

    // Create method to add new journal entries and prevent duplicates
    public void addEntry (@NonNull BodyJournalEntry bodyJournalEntry) {

        // Check for duplicates
        getWritableDatabase().delete(BODY_JOURNAL_TABLE, COL_DATE + "='" + bodyJournalEntry.getMeasurementDate() + "'", null);

        // Add new entry
        getWritableDatabase().execSQL("INSERT INTO " + BODY_JOURNAL_TABLE + " ("
                + COL_DATE + ", "
                + COL_LEFT_CALF + ", "
                + COL_RIGHT_CALF + ", "
                + COL_LEFT_THIGH + ", "
                + COL_RIGHT_THIGH + ", "
                + COL_LEFT_FOREARM + ", "
                + COL_RIGHT_FOREARM + ", "
                + COL_LEFT_BICEP + ", "
                + COL_RIGHT_BICEP + ", "
                + COL_HIPS + ", "
                + COL_WAIST + ", "
                + COL_CHEST + ", "
                + COL_SHOULDERS + ", "
                + COL_NECK + ", "
                + COL_CLAVICLES + ", "
                + COL_WEIGHT + ", "
                + COL_BODY_FAT + ", "
                + COL_HEIGHT + ") "
                + "VALUES ('"
                + bodyJournalEntry.getMeasurementDate() + "', "
                + bodyJournalEntry.getLeftCalf() + ", "
                + bodyJournalEntry.getRightCalf() + ", "
                + bodyJournalEntry.getLeftThigh() + ", "
                + bodyJournalEntry.getRightThigh() + ", "
                + bodyJournalEntry.getLeftForearm() + ", "
                + bodyJournalEntry.getRightForearm() + ", "
                + bodyJournalEntry.getLeftBicep() + ", "
                + bodyJournalEntry.getRightBicep() + ", "
                + bodyJournalEntry.getHips() + ", "
                + bodyJournalEntry.getWaist() + ", "
                + bodyJournalEntry.getChest() + ", "
                + bodyJournalEntry.getShoulders() + ", "
                + bodyJournalEntry.getNeck() + ", "
                + bodyJournalEntry.getClavicles() + ", "
                + bodyJournalEntry.getWeight() + ", "
                + bodyJournalEntry.getBodyFat() + ", "
                + bodyJournalEntry.getHeight() + ")"
        );
    }

    // Create method to fetch daily aggregates for charting
    public LinkedHashMap<String, Float> getBodyJournalEntriesByQuery (String COL_QUERIED1) {

        LinkedHashMap<String, Float> measuresLinkedHashMap = new LinkedHashMap<>();

        Cursor cursor = getReadableDatabase().rawQuery("SELECT " + COL_DATE + ", " + COL_QUERIED1 + " FROM " + BODY_JOURNAL_TABLE + " ORDER BY " + COL_DATE + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                measuresLinkedHashMap.put(cursor.getString(0), cursor.getFloat(1));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return measuresLinkedHashMap;
    }

    // Create method to fetch daily aggregates for charting
    public BodyJournalEntry getBodyJournalEntriesByDay (String date, boolean getDataFromLatestEntry) {

        BodyJournalEntry bodyJournalEntry;
        Cursor cursor;

        if (getDataFromLatestEntry) {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + BODY_JOURNAL_TABLE
                    + " WHERE " + COL_DATE
                    + " = (SELECT MAX(" + COL_DATE
                    + ") FROM " + BODY_JOURNAL_TABLE
                    + " WHERE " + COL_DATE + " <= '" + date + "')",null);
        } else {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + BODY_JOURNAL_TABLE
                    + " WHERE " + COL_DATE + " = '" + date + "'",null);
        }

        if (cursor.moveToFirst()) {
                bodyJournalEntry = new BodyJournalEntry(
                        cursor.getString(0),
                        cursor.getFloat(1),
                        cursor.getFloat(2),
                        cursor.getFloat(3),
                        cursor.getFloat(4),
                        cursor.getFloat(5),
                        cursor.getFloat(6),
                        cursor.getFloat(7),
                        cursor.getFloat(8),
                        cursor.getFloat(9),
                        cursor.getFloat(10),
                        cursor.getFloat(11),
                        cursor.getFloat(12),
                        cursor.getFloat(13),
                        cursor.getFloat(14),
                        cursor.getFloat(15),
                        cursor.getFloat(16),
                        cursor.getFloat(17)
                        );
        } else {
            bodyJournalEntry = null;
        }
        cursor.close();
        
        return bodyJournalEntry;
    }
}