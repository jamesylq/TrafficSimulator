package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Random;

public abstract class Vehicle extends RoadObject implements Iterable {
    protected String name;
    protected double speed;
    protected Intersection target, prev, next;
    public ImageView render;

    public static ArrayList<Vehicle> vehicleList = new ArrayList<>();

    Vehicle(Road road) {
        super(road);

        vehicleList.add(this);
        this.prev = this.road.start;
        this.next = this.road.end;
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

        updateRender();

        System.out.println(this.roadRelPos + " " + this.getPoint());
    }

    public void findTarget() {
        Random random = new Random();

        if (target == null || target.index == prev.index) {
            target = Intersection.intersectionList.get(random.nextInt(Intersection.intersectionList.size()));
        }

        GraphEdge graphEdge = MainController.dp[prev.index][target.index];
        road = graphEdge.edge;
        next = graphEdge.adj;
    }

    public void updateRender() {
        this.render.setX(this.getX());
        this.render.setY(this.getY());
        this.render.setRotate(this.road.getAngle(this.roadRelPos) * 180 / Math.PI);
    }
}
