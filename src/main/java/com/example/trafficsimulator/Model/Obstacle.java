package com.example.trafficsimulator.Model;

public class Obstacle extends RoadObject {
    Object parent;

    Obstacle(Object parent, Road road) {
        super(road);
        this.parent = parent;
        this.roadRelPos = Math.min(0.1, 15 / this.road.length);
    }

    public void updateRender() {}
}
