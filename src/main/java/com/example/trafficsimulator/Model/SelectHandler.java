package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;

import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.shape.*;

import java.util.*;

public class SelectHandler implements EventHandler<MouseEvent> {
    private static double minx, miny, maxx, maxy;
    public static ArrayList<Shape> curves = new ArrayList<>();

    @Override
    public void handle(MouseEvent e) {
        Shape source = (Shape) e.getSource();
        for (Road road: Road.roadList) {
            if (road.curves.contains(source)) {
                if (MainController.selectedRoad != null) {
                    MainController.selectedRoad.selected = false;
                    MainController.mainAnchorPane.getChildren().remove(MainController.selectedHighlight);
                }
                MainController.selectedRoad = road;
                road.selected = true;
                road.updateDrag();
                for (int i = 3; i >= 0; i--) road.curves.get(i).toBack();
                MainController.selectedHighlight.toBack();
                MainController.draggableNodesToFront();
                break;
            }
        }

        display();
    }

    public static void display() {
        for (Shape curve: curves) MainController.mainSelectedAnchorPane.getChildren().remove(curve);
        curves.clear();

        if (MainController.selectedRoad == null) return;

        Shape mainCurve = MainController.selectedRoad.curves.getFirst();
        
        minx = Double.MAX_VALUE;
        miny = Double.MAX_VALUE;
        maxx = Double.MIN_VALUE;
        maxy = Double.MIN_VALUE;

        if (mainCurve instanceof Line ln) {
            addPoint(ln.getStartX(), ln.getStartY());
            addPoint(ln.getEndX(), ln.getEndY());

        } else if (mainCurve instanceof CubicCurve) {
            for (int i = 0; i <= BezierRoad.APPROX_LENGTH_DIVISIONS; i++) {
                addPoint(MainController.selectedRoad.getPoint((double) i / BezierRoad.APPROX_LENGTH_DIVISIONS));
            }
        }

        if (mainCurve instanceof Line) {
            for (Shape curve: MainController.selectedRoad.curves) {
                if (curve instanceof Line ln) {
                    Line newCurve = new Line(
                        scaleX(ln.getStartX()), scaleY(ln.getStartY()),
                        scaleX(ln.getEndX()), scaleY(ln.getEndY())
                    );
                    newCurve.setFill(curve.getFill());
                    newCurve.setStroke(curve.getStroke());
                    newCurve.setStyle(curve.getStyle());
                    newCurve.setStrokeWidth(curve.getStrokeWidth());
                    MainController.mainSelectedAnchorPane.getChildren().add(newCurve);
                    curves.add(newCurve);
                }
            }

        } else if (mainCurve instanceof CubicCurve) {
            for (Shape curve: MainController.selectedRoad.curves) {
                if (curve instanceof CubicCurve cc) {
                    CubicCurve newCurve = new CubicCurve(
                        scaleX(cc.getStartX()), scaleY(cc.getStartY()),
                        scaleX(cc.getControlX1()), scaleY(cc.getControlY1()),
                        scaleX(cc.getControlX2()), scaleY(cc.getControlY2()),
                        scaleX(cc.getEndX()), scaleY(cc.getEndY())
                    );
                    newCurve.setFill(curve.getFill());
                    newCurve.setStroke(curve.getStroke());
                    newCurve.setStyle(curve.getStyle());
                    newCurve.setStrokeWidth(curve.getStrokeWidth());
                    MainController.mainSelectedAnchorPane.getChildren().add(newCurve);
                    curves.add(newCurve);
                }
            }
        }
    }

    public static void addPoint(Point point) {
        addPoint(point.getX(), point.getY());
    }

    public static void addPoint(double x, double y) {
        minx = Math.min(minx, x);
        maxx = Math.max(maxx, x);
        miny = Math.min(miny, y);
        maxy = Math.max(maxy, y);
    }

    public static double scaleX(double x) {
        final double w = MainController.mainSelectedAnchorPane.getWidth();
        double nx = (x - minx) / Math.max(maxx - minx, maxy - miny) * (w - 70);
        return nx + (maxy - miny > maxx - minx ? (w - (maxx - minx) / (maxy - miny) * (w - 70)) / 2 : 35);
    }

    public static double scaleY(double y) {
        return (y - miny) / Math.max(maxx - minx, maxy - miny) * (MainController.mainSelectedAnchorPane.getWidth() - 50) + 50;
    }
}
