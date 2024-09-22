package com.example.trafficsimulator.Model;

public class BezierRoadWrapper extends RoadWrapper {
    public Point weightStart, weightEnd;

    public BezierRoadWrapper(int i) {
        super(i);
    }

    public void load(BezierRoad road) {
        super.load(road);
        weightStart = new Point(road.getWeightStart());
        weightEnd = new Point(road.getWeightEnd());
    }
}