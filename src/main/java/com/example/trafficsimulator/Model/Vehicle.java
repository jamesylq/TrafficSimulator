package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;

import java.util.*;

public abstract class Vehicle extends RoadObject implements Iterable {
    protected String name;
    protected double speed, WIDTH, HEIGHT;
    protected Intersection target, prev, next;
    public static ArrayList<Vehicle> vehicleList = new ArrayList<>();

    Vehicle(Road road) {
        super(road);

        vehicleList.add(this);
        this.prev = this.road.start;
        this.next = this.road.end;
    }


    public void iterate() {
//        getRoadObjects().ceiling(this);

        double step = this.speed * road.speed / road.length;

        this.roadRelPos += flip() * step;
        if (this.roadRelPos >= 1 || this.roadRelPos <= 0) {
            this.prev = this.next;
            this.findTarget();
        }

        updateRender();
    }

    public void findTarget() {
        Random random = new Random();

        while (target == null || target == prev) {
            target = Intersection.intersectionList.get(random.nextInt(Intersection.intersectionList.size()));
        }

        GraphEdge graphEdge = MainController.dp[prev.index][target.index];
        getRoadObjects().remove(this);
        road = graphEdge.edge;
        next = graphEdge.adj;
        getRoadObjects().add(this);

        roadRelPos = (isFwd() ? 0 : 1);

        System.out.println(road.fwdObjects);
    }

    public void updateRender() {
        Point der = this.road.derivative(this.roadRelPos);
        double SIN = der.getY();
        double COS = der.getX();

        this.render.setRotate(this.road.getAngle(this.roadRelPos) * 180 / Math.PI);

        this.renderPane.setLayoutX(this.getX() - Math.max(WIDTH, HEIGHT) / 2 + SIN * (25 - HEIGHT / 2) * flip());
        this.renderPane.setLayoutY(this.getY() - Math.max(WIDTH, HEIGHT) / 2 - COS * (25 - HEIGHT / 2) * flip());
    }

    public boolean isFwd() {
        return (this.next == this.road.end);
    }

    public double flip() {
        return (isFwd() ? 1.0 : -1.0);
    }

    public ArrayList<RoadObject> getRoadObjects() {
        return (isFwd() ? road.fwdObjects : road.bckObjects);
    }

    @Override
    public String toString() {
        return Double.toString(this.roadRelPos);
    }
}
