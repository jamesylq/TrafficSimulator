package com.example.trafficsimulator.Model;

public class RoadObject {
    protected Point point;
    protected Road road;

    public double getX() {
        return this.point.getX();
    }

    public double getY() {
        return this.point.getY();
    }

    public void delete() {
        road.removeObject(this);
    }
}
