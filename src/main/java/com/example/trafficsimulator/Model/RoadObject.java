package com.example.trafficsimulator.Model;

import javafx.scene.image.*;
import javafx.scene.layout.*;

public class RoadObject implements Comparable<RoadObject> {
    protected double roadRelPos = 0;
    protected Point point;
    protected Road road;
    public ImageView render;
    public FlowPane renderPane;

    RoadObject(Road road) {
        this.road = road;
        this.getPoint();
    }

    public Point getPoint() {
        return this.point = this.road.getPoint(this.roadRelPos);
    }

    public double getX() {
        return this.getPoint().getX();
    }

    public double getY() {
        return this.getPoint().getY();
    }

    @Override
    public int compareTo(RoadObject o) {
        return (int) Math.signum(this.roadRelPos - o.roadRelPos);
    }
}
