package com.example.trafficsimulator.Model;

public class Car extends Vehicle {
    public static final double DEFAULT_SPEED = 1.0;

    public Car(Road road) {
        this(road, DEFAULT_SPEED);
    }

    public Car(Road road, double speed) {
        this.road = road;
        this.speed = speed;
        this.name = "Car";
    }

    public void iterate() {

    }
}
