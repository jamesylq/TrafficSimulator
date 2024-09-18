package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;

import java.util.*;

public abstract class Vehicle extends RoadObject implements Iterable, Selectable {
    protected String name;
    protected double speed;
    public Intersection target, prev, next;
    public static ArrayList<Vehicle> vehicleList = new ArrayList<>();
    public boolean selected;

    Vehicle(Road road) {
        super(road);

        vehicleList.add(this);
        this.prev = this.road.start;
        this.next = this.road.end;
    }


    public void iterate() {
        double step = this.speed * road.speed / road.length;
        if (target != null) step *= distanceFactor(getDistance(nextCollidable()));

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

    public RoadObject nextCollidable() {
        GraphEdge graphEdge;
        RoadObject current;
        ArrayList<RoadObject> objList;
        Intersection cur = this.prev;

        while (cur != this.target) {
            graphEdge = MainController.dp[cur.index][this.target.index];
            cur = graphEdge.adj;
            objList = (graphEdge.isFwd() ? graphEdge.edge.fwdObjects : graphEdge.edge.bckObjects);

            int ind = objList.indexOf(this);
            while (++ind < objList.size()) {
                if ((current = objList.get(ind)).collidable) {
                    return current;
                }
            }
        }

        return null;
    }

//    public RoadObject nextObject() {
//        GraphEdge graphEdge;
//        ArrayList<RoadObject> objList;
//        Intersection cur = this.prev;
//
//        while (cur != this.target) {
//            graphEdge = MainController.dp[cur.index][this.target.index];
//            cur = graphEdge.adj;
//            objList = (graphEdge.isFwd() ? graphEdge.edge.fwdObjects : graphEdge.edge.bckObjects);
//
//            int ind = objList.indexOf(this);
//            if (++ind < objList.size()) return objList.get(ind);
//        }
//
//        return null;
//    }

    public double getDistance(RoadObject roadObject) {
        if (roadObject == null) return Double.MAX_VALUE;
        if (road == roadObject.road) return road.getDistance(roadRelPos, roadObject.roadRelPos);

        double distance = road.getDistance(roadRelPos, isFwd() ? 1 : 0);

        Road curRoad;
        GraphEdge graphEdge;
        Intersection cur = this.next;

        do {
            if (cur.index == this.target.index) throw new IllegalArgumentException("Object not sighted!");

            graphEdge = MainController.dp[cur.index][this.target.index];
            curRoad = graphEdge.edge;
            cur = graphEdge.adj;

            distance += graphEdge.dist;

        } while (curRoad != roadObject.road);

        return distance + curRoad.getDistance(graphEdge.isFwd() ? 0 : 1, roadObject.roadRelPos);
    }

    public static double distanceFactor(double x) {
        return Math.max(0, 1 / (1 + Math.pow(Math.E, -0.1 * (x - 50))) - 0.08);
    }

    @Override
    public String toString() {
        return Double.toString(this.roadRelPos);
    }

    public void setSelect(boolean b) {
        selected = b;
    }

    public boolean getSelect() {
        return selected;
    }

    public void onSelect() {

    }
}
