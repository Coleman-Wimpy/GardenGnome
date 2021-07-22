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
}
