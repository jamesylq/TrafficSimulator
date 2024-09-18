package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class LinearRoad extends Road implements Selectable {
    private double SIN, COS;

    public void normal() {
        Point normalised = Point.normal(start.getPoint(), end.getPoint());
        this.COS = normalised.getX();
        this.SIN = normalised.getY();
        calculateLength();
    }

    public Point derivative(double t) {
        return new Point(this.COS, this.SIN);
    }

    public LinearRoad(Intersection start, Intersection end) {
        super(start, end);
        for (int i = 0; i < 4; i++) this.addCurve(null);
        updateRender();

        this.addCurve(start.getCircleObj());
        this.addCurve(end.getCircleObj());
    }

    public void updateRender() {
        if (this.selected) onSelect();

        normal();

        Line A = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        A.setStrokeWidth(50);
        this.curves.set(0, A);
        Line B = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        B.setStrokeWidth(45);
        B.setStroke(Color.WHITE);
        this.curves.set(1, B);
        Line C = new Line(
                start.getX() - 5 * this.COS, start.getY() - 5 * this.SIN,
                end.getX() + 5 * this.COS, end.getY() + 5 * this.SIN
        );
        C.setStrokeWidth(40);
        this.curves.set(2, C);
        Line D = new Line(
                start.getX(), start.getY(),
                end.getX(), end.getY()
        );
        D.setStrokeWidth(5);
        D.setStroke(Color.YELLOW);
        D.setStyle("-fx-stroke-dash-array: 2 12 12 2;");
        this.curves.set(3, D);

        calculateLength();

        for (int i = 0; i < 4; i++) this.curves.get(i).setOnMouseClicked(new SelectHandler());
    }

    public void updateDrag() {
        for (int i = 0; i < 4; i++) {
            if (this.curves.get(i) == null) continue;
            MainController.mainAnchorPane.getChildren().remove(this.curves.get(i));
        }
        updateRender();
        for (int i = 0; i < 4; i++) {
            if (this.curves.get(i) == null) continue;
            MainController.mainAnchorPane.getChildren().add(this.curves.get(i));
        }
    }

    public Point getPoint(double roadRelPos) {
        return new Point(
            this.getStart().getX() * (1 - roadRelPos) + this.getEnd().getX() * roadRelPos,
            this.getStart().getY() * (1 - roadRelPos) + this.getEnd().getY() * roadRelPos
        );
    }

    public double calculateLength() {
        this.length = getDistance(0, 1);
        this.weight = this.length * this.speed;
        return this.length;
    }

    public double getAngle(double roadRelPos) {
        return Math.atan2(this.getEnd().getY() - this.getStart().getY(), this.getEnd().getX() - this.getStart().getX());
    }

    public double getDistance(double roadRelPos1, double roadRelPos2) {
        return getPoint(roadRelPos1).getDistance(getPoint(roadRelPos2));
    }

    public void onSelect() {
        Line highlight = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        highlight.setStroke(Color.AQUA);
        highlight.setStrokeWidth(55);
        if (MainController.selectedHighlight != null) {
            MainController.mainAnchorPane.getChildren().remove(MainController.selectedHighlight);
        }
        MainController.mainAnchorPane.getChildren().add(highlight);
        MainController.selectedHighlight = highlight;

        for (BezierRoad road: BezierRoad.bezierRoadList) road.hideWeights();
    }

    public void setSelect(boolean b) {
        selected = b;
    }

    public boolean getSelect() {
        return selected;
    }
}
