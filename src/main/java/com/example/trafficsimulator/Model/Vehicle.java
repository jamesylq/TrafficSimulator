package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.*;

public abstract class Vehicle extends RoadObject implements Iterable, Selectable {
    public int index;
    public double speed;
    public Intersection target, prev, next;
    public static ArrayList<Vehicle> vehicleList = new ArrayList<>();
    public ArrayList<Road> path = new ArrayList<>();
    public boolean selected, deleted = false;

    private static final Random random = new Random();

    Vehicle(Road road) {
        super(road);

        vehicleList.add(this);
    }

    public void iterate() {
        double step = this.speed * road.speed / road.length;
        GraphEdge graphEdge;
        Intersection cur = this.prev;

        this.path.clear();
        while (cur != this.target) {
            graphEdge = MainController.dp[cur.index][this.target.index];
            cur = graphEdge.adj;
            this.path.add(graphEdge.edge);
        }

        if (this.target != null) step *= distanceFactor(getDistance(nextCollidable()));

        this.roadRelPos = bound(this.roadRelPos + flip() * step);

        if (this.isFwd() && this.roadRelPos >= 1 || !this.isFwd() && this.roadRelPos <= 0) {
            this.prev = this.next;
            this.findTarget();
        }

        updateRender();
    }

    public static Intersection generateTarget() {
        return Destination.destinationList.get(random.nextInt(Destination.destinationList.size()));
    }

    public static Intersection generateTarget(Intersection exclude) {
        int excl = Destination.destinationList.indexOf(exclude);
        if (excl == -1) return generateTarget();

        int ind = random.nextInt(Destination.destinationList.size() - 1);
        return Destination.destinationList.get(ind >= excl ? ++ind : ind);
    }

    public void findTarget() {
        if (target == prev) {
            this.delete();
            return;
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

    public void delete() {
        MainController.mainAnchorPane.getChildren().remove(this.renderPane);
        this.deleted = true;
        getRoadObjects().remove(this);
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
                if ((current = objList.get(ind)).isCollidable(this)) {
                    return current;
                }
            }
        }

        return null;
    }

    public double getDistance(RoadObject roadObject) {
        if (roadObject == null) return Double.MAX_VALUE;
        if (road == roadObject.road) return road.getDistance(roadRelPos, roadObject.roadRelPos);

        double distance = road.getDistance(roadRelPos, isFwd() ? 1 : 0);

        Intersection cur = this.next;
        GraphEdge graphEdge = MainController.dp[cur.index][this.target.index];
        Road curRoad = graphEdge.edge;
        cur = graphEdge.adj;

        while (curRoad != roadObject.road) {
            if (cur.index == this.target.index) throw new IllegalArgumentException("Object not sighted!");

            graphEdge = MainController.dp[cur.index][this.target.index];
            curRoad = graphEdge.edge;
            cur = graphEdge.adj;

            distance += graphEdge.edge.calculateLength();
        }

        return distance + curRoad.getDistance(graphEdge.isFwd() ? 0 : 1, roadObject.roadRelPos);
    }

    public static double distanceFactor(double x) {
        return bound(1 / (0.91 * (1 + Math.pow(Math.E, -(x - 90.0) / 20.0))) - 0.08);
    }

    public boolean isCollidable(Vehicle vehicle) {
        return true;
    }

    public void setSelect(boolean b) {
        selected = b;
    }

    public boolean getSelect() {
        return selected;
    }

    public void onSelect() {
        MainController.mainAnchorPane.getChildren().removeAll(MainController.permaFront);
        MainController.permaFront.clear();

        GraphEdge graphEdge;
        Intersection cur = this.prev;

        while (cur != this.target) {
            graphEdge = MainController.dp[cur.index][this.target.index];
            Line l = new Line(
                    cur.getX(), cur.getY(),
                    graphEdge.adj.getX(), graphEdge.adj.getY()
            );
            l.setStroke(Color.WHITE);
            l.setStrokeWidth(5);
            MainController.permaFront.add(l);
            for (Shape shape: MainController.permaFront) {
                if (!MainController.mainAnchorPane.getChildren().contains(shape)) {
                    MainController.mainAnchorPane.getChildren().add(shape);
                }
            }
            cur = graphEdge.adj;
        }

        System.out.println(getDistance(nextCollidable()));
        RoadObject ro = nextCollidable();
        Circle c = new Circle(50);
        c.setStroke(Color.AQUA);
        c.centerXProperty().bind(ro.renderPane.layoutXProperty().add(ro.MAXSIDE / 2));
        c.centerYProperty().bind(ro.renderPane.layoutYProperty().add(ro.MAXSIDE / 2));
        MainController.mainAnchorPane.getChildren().add(c);
    }

    public void addToRoad() {
        if (isFwd()) {
            this.road.fwdObjects.add(this);
            this.road.fwdObjects.sort(Comparator.naturalOrder());
        } else {
            this.road.bckObjects.add(this);
            this.road.bckObjects.sort(Comparator.reverseOrder());
        }

    }

    public static double bound(double x) {
        return Math.max(0.0, Math.min(1.0, x));
    }
}
