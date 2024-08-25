package com.example.trafficsimulator.Model;

import java.util.ArrayList;

public abstract class Vehicle extends RoadObject implements Iterable {
    protected String name;
    protected double speed, roadRelPos;

    protected Point pos;
    protected Intersection target;

    public static ArrayList<Vehicle> vehicleList;

    Vehicle() {
        vehicleList.add(this);
    }


    public abstract void iterate();

    public void findTarget() {

    }

    public void getPos() {

    }
}
