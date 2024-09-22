package com.example.trafficsimulator.Model;

import javafx.scene.shape.*;

import java.io.Serializable;

public class Point implements Serializable {
    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        this(p.getX(), p.getY());
    }

    public Point(Circle c) {
        this(c.getCenterX(), c.getCenterY());
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getDistance(Point point) {
        return getDistance(this, point);
    }

    public static double getDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2.0) + Math.pow(a.getY() - b.getY(), 2.0));
    }

    public static Point normal(Point start, Point end) {
        return normal(new Point(end.getX() - start.getX(), end.getY() - start.getY()));
    }

    public static Point normal(Point p) {
        double mag = Math.sqrt(Math.pow(p.getX(), 2) + Math.pow(p.getY(), 2));
        return new Point (p.getX() / mag, p.getY() / mag);
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", this.x, this.y);
    }
}
