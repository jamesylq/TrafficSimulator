package com.example.trafficsimulator.Model;

public class Car extends Vehicle {
    public static final double DEFAULT_SPEED = 10;

    public Car(Road road) {
        this(road, DEFAULT_SPEED);
    }

    public Car(Road road, double speed) {
        this.road = road;
        this.speed = speed;
        this.name = "Car";
    }

//    public void iterate() {
//        double step = this.speed * road.speed / road.length;
//        if (this.next == this.road.start) step *= -1;
//
//    }
}
