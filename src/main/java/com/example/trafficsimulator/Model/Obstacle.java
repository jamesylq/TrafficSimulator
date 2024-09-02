package com.example.trafficsimulator.Model;

public class Obstacle extends RoadObject {
    Obstacle(Road road, Point point) {
        super(road);

        this.road = road;
        this.point = point;
        this.road.numObstacles++;
    }

    @Override
    public void delete() {
        super.delete();
        this.road.numObstacles--;
    }
}
