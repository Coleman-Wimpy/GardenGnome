package com.example.gardengnome;

public class Pallet {

    public int sku, quantity;
    public String location, createdBy, palletID;

    public Pallet() {

    }

    public Pallet(String palletID, int sku, int quantity, String location, String createdBy) {
        this.palletID = palletID;
        this.sku = sku;
        this.quantity = quantity;
        this.location = location;
        this.createdBy = createdBy;
    }

    public int getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getPalletID() {
        return palletID;
    }

    @Override
    public String toString() {
        return "Pallet{" +
                "sku=" + sku +
                ", quantity=" + quantity +
                ", location='" + location + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", palletID='" + palletID + '\'' +
                '}';
    }
}
