package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public abstract class Vehicle extends RoadObject implements Iterable {
    protected String name;
    protected double speed, roadRelPos;
    protected Point pos;
    protected Intersection target, prev, next;

    protected Road road;
    protected ImageView vehicle;

    public static ArrayList<Vehicle> vehicleList;

    Vehicle() {
        vehicleList.add(this);
    }


    public void iterate() {
        double step = this.speed * road.speed / road.length;
        if (this.next == this.road.end) {
            this.roadRelPos += step;
            if (this.roadRelPos >= 1) {
                this.prev = this.next;
                this.findTarget();
            }
        } else {
            this.roadRelPos -= step;
            if (this.roadRelPos <= 0) {
                this.prev = this.next;
                this.findTarget();
            }
        }
    }

    public void findTarget() {
        GraphEdge graphEdge = MainController.dp[prev.index][target.index];
        road = graphEdge.edge;
        next = graphEdge.adj;
    }
}
