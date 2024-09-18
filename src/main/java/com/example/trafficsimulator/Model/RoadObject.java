package com.example.trafficsimulator.Model;

import javafx.geometry.Pos;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public abstract class RoadObject implements Comparable<RoadObject> {
    protected double roadRelPos = 0, WIDTH, HEIGHT, MAXSIDE;
    public boolean collidable = true;
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

    @Override
    public int compareTo(RoadObject o) {
        return (int) Math.signum(this.roadRelPos - o.roadRelPos);
    }
}
