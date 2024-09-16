package com.example.trafficsimulator.Model;

public class Obstacle extends RoadObject {
    Obstacle(Road road, Point point) {
        super(road);

        this.road = road;
        this.point = point;
        this.road.numObstacles++;
    }

    public void delete() {
        this.road.numObstacles--;
    }
}
