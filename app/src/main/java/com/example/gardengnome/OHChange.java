package com.example.gardengnome;

import java.sql.Date;

public class OHChange {
    private String changedBy, palletID, date;
    private int sku, previousQuantity, newQuantity;

    public OHChange() {

    }

    public OHChange(String palletID, int sku, int previousQuantity, int newQuantity, String date, String changedBy) {
        this.palletID = palletID;
        this.sku = sku;
        this.previousQuantity = previousQuantity;
        this.newQuantity = newQuantity;
        this.date = date;
        this.changedBy = changedBy;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public String getPalletID() {
        return palletID;
    }

    public int getSku() {
        return sku;
    }

    public int getPreviousQuantity() {
        return previousQuantity;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "OHChange{" +
                "changedBy='" + changedBy + '\'' +
                ", palletID='" + palletID + '\'' +
                ", sku=" + sku +
                ", previousQuantity=" + previousQuantity +
                ", newQuantity=" + newQuantity +
                '}';
    }
}
