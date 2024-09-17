package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;

import java.util.*;

public abstract class Vehicle extends RoadObject implements Iterable {
    protected String name;
    protected double speed;
    public Intersection target, prev, next;
    public static ArrayList<Vehicle> vehicleList = new ArrayList<>();

    Vehicle(Road road) {
        super(road);

        vehicleList.add(this);
        this.prev = this.road.start;
        this.next = this.road.end;
    }


    public void iterate() {
        double step = this.speed * road.speed / road.length;
        if (target != null) step *= distanceFactor(getDistance(nextVehicle()));

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

        if (isFwd()) {
            roadRelPos = 0;
            road.fwdObjects.addFirst(this);
        } else {
            roadRelPos = 1;
            road.bckObjects.add(this);
        }
    }

    public void updateRender() {
        Point der = this.road.derivative(this.roadRelPos);
        double SIN = der.getY();
        double COS = der.getX();

        this.render.setRotate(this.road.getAngle(this.roadRelPos) * 180 / Math.PI);

        this.renderPane.setLayoutX(this.getX() - MAXSIDE / 2 + SIN * (25 - HEIGHT / 2) * flip());
        this.renderPane.setLayoutY(this.getY() - MAXSIDE / 2 - COS * (25 - HEIGHT / 2) * flip());
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

    public Vehicle nextVehicle() {
        GraphEdge graphEdge;
        ArrayList<RoadObject> objList;
        Intersection cur = this.prev;

        while (cur != this.target) {
            graphEdge = MainController.dp[cur.index][this.target.index];
            cur = graphEdge.adj;
            objList = (graphEdge.isFwd() ? graphEdge.edge.fwdObjects : graphEdge.edge.bckObjects);

            int ind = objList.indexOf(this);
            while (++ind < objList.size()) {
                if (objList.get(ind) instanceof Vehicle vehicle) {
                    return vehicle;
                }
            }
        }

        return null;
    }

    public double getDistance(Vehicle vehicle) {
        if (vehicle == null) return Double.MAX_VALUE;
        if (road == vehicle.road) return road.getDistance(roadRelPos, vehicle.roadRelPos);

        double distance = road.getDistance(roadRelPos, isFwd() ? 1 : 0);

        Road curRoad;
        GraphEdge graphEdge;
        Intersection cur = this.next;

        do {
            if (cur.index == this.target.index) throw new IllegalArgumentException("Vehicle not sighted!");

            graphEdge = MainController.dp[cur.index][this.target.index];
            curRoad = graphEdge.edge;
            cur = graphEdge.adj;

            distance += graphEdge.dist;

        } while (curRoad != vehicle.road);

        return distance + curRoad.getDistance(graphEdge.isFwd() ? 0 : 1, vehicle.roadRelPos);
    }

    public static double distanceFactor(double x) {
        return Math.tanh(x / 100);
    }

    @Override
    public String toString() {
        return Double.toString(this.roadRelPos);
    }
}
