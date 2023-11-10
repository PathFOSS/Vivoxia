package com.pathfoss.vivoxia.exercise;

public class ExerciseJournalEntry {

    private final int rowID;
    private final String name;
    private final float reps;
    private final float weight;
    private final float rest;
    private final float work;
    private final float effort;

    public ExerciseJournalEntry(int rowID, String name, float reps, float weight, float rest, float work, float effort) {
        this.rowID = rowID;
        this.name = name;
        this.reps = reps;
        this.weight = weight;
        this.rest = rest;
        this.work = work;
        this.effort = effort;
    }

    public int getRowID() {
        return rowID;
    }

    public String getName() {
        return name;
    }

    public float getReps() {
        return reps;
    }

    public float getWeight() {
        return weight;
    }

    public float getRest() {
        return rest;
    }

    public float getWork() {
        return work;
    }

    public float getEffort() {
        return effort;
    }
}