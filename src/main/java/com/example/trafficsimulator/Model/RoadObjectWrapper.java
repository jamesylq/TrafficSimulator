package com.example.trafficsimulator.Model;

public abstract class RoadObjectWrapper extends Wrapper {
    public boolean collidable;
    public double roadRelPos, WIDTH, HEIGHT;

    public Point point;
    public RoadWrapper road;

    RoadObjectWrapper(int i) {
        super(i);
    }

    public void load(RoadObject obj) {
        roadRelPos = obj.roadRelPos;
        WIDTH = obj.WIDTH;
        HEIGHT = obj.HEIGHT;
        collidable = obj.collidable;
        road = RoadWrapper.get(obj.road.index);
    }
}
