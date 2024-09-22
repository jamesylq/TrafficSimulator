package com.example.trafficsimulator.Model;

import java.util.*;

import com.example.trafficsimulator.Controller.MainController;
import javafx.scene.shape.*;

public abstract class Road implements Iterable {
    public int index;
    public boolean selected = false;
    public double speed = 1.0;
    protected double length;
    protected double weight;

    protected Intersection start, end;
    public ArrayList<RoadObject> fwdObjects = new ArrayList<>(), bckObjects = new ArrayList<>();
    public static ArrayList<Road> roadList = new ArrayList<>();
    public ArrayList<Shape> curves = new ArrayList<>();

    Road() {
        roadList.add(this);
    }

    Road(Intersection start, Intersection end) {
        this();
        this.start = start;
        this.end = end;
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

    public void iterate() {
        fwdObjects.sort(Comparator.naturalOrder());
        bckObjects.sort(Comparator.reverseOrder());
    }

    public abstract Point getPoint(double roadRelPos);

    public abstract void updateDrag();

    public abstract void updateRender();

    public abstract double calculateLength();

    public abstract double getAngle(double roadRelPos);

    public abstract Point derivative(double roadRelPos);

    public abstract double getDistance(double roadRelPos1, double roadRelPos2);
}
