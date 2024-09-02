package com.example.trafficsimulator.Model;

public class RoadObject {
    protected double roadRelPos = 0;
    protected Point point;
    protected Road road;

    RoadObject(Road road) {
        this.road = road;
        this.getPoint();
    }

    public void delete() {
        road.removeObject(this);
    }

    public Point getPoint() {
        this.point = this.road.getPoint(this.roadRelPos);
        return point;
    }

    public double getX() {
        return this.getPoint().getX();
    }

    public double getY() {
        return this.getPoint().getY();
    }
}
