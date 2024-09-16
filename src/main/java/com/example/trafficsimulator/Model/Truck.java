package com.example.trafficsimulator.Model;

public class Truck extends Vehicle {
    static final double DEFAULT_SPEED = 0.7;

    public Truck(Road road) {
        this(road, DEFAULT_SPEED);
    }

    public Truck(Road road, double speed) {
        super(road);

        this.road = road;
        this.speed = speed;
        this.name = "Truck";
    }

//    public void iterate() {
//
//    }
}
