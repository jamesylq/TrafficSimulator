package com.example.trafficsimulator.Model;

import java.util.*;

import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.*;

public abstract class Road implements Iterable {
    public ArrayList<Shape> curves = new ArrayList<>();
    protected double speed, length;
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
}
