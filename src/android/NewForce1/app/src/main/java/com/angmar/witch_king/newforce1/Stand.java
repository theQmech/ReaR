package com.angmar.witch_king.newforce1;

/**
 * Created by amey on 21/11/16.
 */

public class Stand{
    double latitude = 0;
    double longitude = 0;
    String name;
    String address;
    Integer numBikes = 0;
    String standId;
    Integer numCars = 0;
    Integer capacityBikes = 0;
    Integer capacityCars = 0;

    @Override
    public String toString() {
        return name;
    }
}
