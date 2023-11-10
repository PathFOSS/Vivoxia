package com.pathfoss.vivoxia.body;

public class BodyJournalEntry {

    private final String measurementDate;
    private final float leftCalf;
    private final float rightCalf;
    private final float leftThigh;
    private final float rightThigh;
    private final float leftForearm;
    private final float rightForearm;
    private final float leftBicep;
    private final float rightBicep;
    private final float hips;
    private final float waist;
    private final float chest;
    private final float shoulders;
    private final float neck;
    private final float clavicles;
    private final float weight;
    private final float bodyFat;
    private final float height;

    // Create a constructor
    public BodyJournalEntry(String measurementDate, float leftCalf, float rightCalf, float leftThigh, float rightThigh, float leftForearm, float rightForearm, float leftBicep, float rightBicep, float hips, float waist, float chest, float shoulders, float neck, float clavicles, float weight, float bodyFat, float height) {
        this.measurementDate = measurementDate;
        this.leftCalf = leftCalf;
        this.rightCalf = rightCalf;
        this.leftThigh = leftThigh;
        this.rightThigh = rightThigh;
        this.leftForearm = leftForearm;
        this.rightForearm = rightForearm;
        this.leftBicep = leftBicep;
        this.rightBicep = rightBicep;
        this.hips = hips;
        this.waist = waist;
        this.chest = chest;
        this.shoulders = shoulders;
        this.neck = neck;
        this.clavicles = clavicles;
        this.weight = weight;
        this.bodyFat = bodyFat;
        this.height = height;
    }

    // Create getters for each entry attribute
    public String getMeasurementDate() {
        return measurementDate;
    }

    public float getLeftCalf() {
        return leftCalf;
    }

    public float getRightCalf() {
        return rightCalf;
    }

    public float getLeftThigh() {
        return leftThigh;
    }

    public float getRightThigh() {
        return rightThigh;
    }

    public float getLeftForearm() {
        return leftForearm;
    }

    public float getRightForearm() {
        return rightForearm;
    }

    public float getLeftBicep() {
        return leftBicep;
    }

    public float getRightBicep() {
        return rightBicep;
    }

    public float getHips() {
        return hips;
    }

    public float getWaist() {
        return waist;
    }

    public float getChest() {
        return chest;
    }

    public float getShoulders() {
        return shoulders;
    }

    public float getNeck() {
        return neck;
    }

    public float getClavicles() {
        return clavicles;
    }

    public float getWeight() {
        return weight;
    }

    public float getBodyFat() {
        return bodyFat;
    }

    public float getHeight() {
        return height;
    }
}
