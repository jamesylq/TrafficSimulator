package com.example.trafficsimulator.Model;

public class Obstacle extends RoadObject {
    RoadObject parent;

    Obstacle(RoadObject parent, Road road) {
        super(road);
        this.parent = parent;

        if (this.parent instanceof TrafficLight tl) {
            this.roadRelPos = Math.min(0.1, 15 / this.road.length);
            if (tl.intersection == this.road.end) {
                this.roadRelPos = 1 - this.roadRelPos;
                this.road.fwdObjects.add(this);
            } else {
                this.road.bckObjects.add(this);
            }
        }
    }

    public boolean isCollidable(Vehicle vehicle) {
        if (this.parent instanceof TrafficLight tl) {
            return (this.collidable && vehicle.path.contains(tl.road));
        }
        return false;
    }

    public void updateRender() {}
}
