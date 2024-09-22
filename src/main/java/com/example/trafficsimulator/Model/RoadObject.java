package com.example.trafficsimulator.Model;

import javafx.geometry.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.util.ArrayList;

public abstract class RoadObject implements Comparable<RoadObject> {
    public double roadRelPos = 0, WIDTH, HEIGHT, MAXSIDE;
    public boolean collidable = true;
    public static ArrayList<RoadObject> roadObjectList;

    protected Point point;
    protected Road road;
    public ImageView render;
    public FlowPane renderPane;

    RoadObject(Road road) {
        this.road = road;
        this.getPoint();
    }

    public Point getPoint() {
        return this.point = this.road.getPoint(this.roadRelPos);
    }

    public double getX() {
        return this.getPoint().getX();
    }

    public double getY() {
        return this.getPoint().getY();
    }

    public void initRenderPane() {
        this.renderPane = new FlowPane(this.render);
        this.renderPane.setAlignment(Pos.CENTER);
        this.renderPane.setMaxWidth(MAXSIDE = Math.max(WIDTH, HEIGHT));
        this.renderPane.setMinWidth(MAXSIDE);
        this.renderPane.setMaxHeight(MAXSIDE);
        this.renderPane.setMinHeight(MAXSIDE);
        this.render.setOnMouseClicked(new SelectHandler());
    }

    public abstract void updateRender();

    public abstract boolean isCollidable(Vehicle vehicle);

    @Override
    public int compareTo(RoadObject o) {
        return (int) Math.signum(this.roadRelPos - o.roadRelPos);
    }

    @Override
    public String toString() {
        String[] className = this.getClass().getName().split("\\.");
        return String.format("%s: %f", className[className.length - 1], this.roadRelPos);
    }
}
