package com.example.trafficsimulator.Model;

import java.util.*;

import com.example.trafficsimulator.Controller.MainController;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.*;

public abstract class Road implements Iterable {
    public ArrayList<Shape> curves = new ArrayList<>();
    protected double speed = 1.0, length, weight;
    protected Intersection start, end;
    public int numObstacles = 0;
    protected ArrayList<RoadObject> currObjects = new ArrayList<>();
    public static ArrayList<Road> roadList = new ArrayList<>();
    public boolean selected = false;

    Road() {
        roadList.add(this);
    }

    Road(Intersection start, Intersection end) {
        this();
        this.currObjects = new ArrayList<>();
        this.start = start;
        this.end = end;
    }

    public void addObject(RoadObject roadObject) {
        currObjects.add(roadObject);
    }

    public void removeObject(RoadObject roadObject) {
        currObjects.remove(roadObject);
    }

    public void delete() {
        roadList.remove(this);
        this.getStart().remove(this);
        this.getEnd().remove(this);

        MainController.mainAnchorPane.getChildren().removeAll(this.curves);
    }

    public void addCurve(Shape curve) {
        this.curves.add(curve);
    }

    public Intersection getStart() {
        return this.start;
    }

    public Intersection getEnd() {
        return this.end;
    }

    public void setStart(Intersection intersection) {
        this.start = intersection;
    }

    public void setEnd(Intersection intersection) {
        this.end = intersection;
    }

    public double getLength() {
        return this.length;
    }

    public double getSpeed() {
        return this.speed;
    }

    public abstract Point getPoint(double roadRelPos);

    public abstract void updateDrag();

    public abstract void updateRender();

    public abstract void iterate();

    public abstract double calculateLength();

    public abstract double getAngle(double roadRelPos);

    public abstract Point derivative(double roadRelPos);
}
