/*
package com.example.trafficsimulator.Test;

import com.example.trafficsimulator.Model.Road;

public class Path extends Road {
    private double m, c;

    private Point start, end;

    public Path(Point start, Point end, double speed) {
        super(speed);
        this.start = start;
        this.end = end;
        this.m = (end.getY() - start.getY()) / (end.getX() - start.getX());
        this.c = start.getY() - start.getX() * this.m;
    }

    public Path(Point point, double m, double speed) {
        super(speed);
//        this.start =
        this.m = m;
        this.c = -m * point.getX() + point.getY();
    }

    public Point intersect(Path p) throws ArithmeticException {
        double x = (this.c - p.getC()) / (p.getM() - this.m);
        return new Point(x, getY(x));
    }

    public double getM() {
        return this.m;
    }

    public double getC() {
        return this.c;
    }

    public double getY(double x) {
        return this.m * x + this.c;
    }

    public void render() {

    }
}

*/