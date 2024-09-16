package com.example.trafficsimulator.Model;

import java.util.*;

public class TrafficLight extends RoadObject implements Iterable {
    public static final int RED = 0, YELLOW = 1, GREEN = 2;
    public static ArrayList<TrafficLight> trafficLightList = new ArrayList<>();

    private int currState;
    private Point point;
    private Road road;

    TrafficLight(Road road, Point point) {
        super(road);

        this.road = road;
        this.point = point;
    }

    public int getCurrState() {
        return this.currState;
    }

    public void iterate() {

    }
}
