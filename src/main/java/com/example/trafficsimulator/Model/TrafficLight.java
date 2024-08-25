package com.example.trafficsimulator.Model;

public class TrafficLight extends RoadObject implements Iterable {
    public static final int RED = 0, YELLOW = 1, GREEN = 2;

    private int currState;
    private Point point;
    private Road road;

    TrafficLight(Road road, Point point) {
        this.road = road;
        this.point = point;
    }

    public int getCurrState() {
        return this.currState;
    }

    public void iterate() {

    }
}
