package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;

import javafx.event.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.shape.*;

import java.util.*;

public class SelectHandler implements EventHandler<MouseEvent> {
    private static double minx, miny, maxx, maxy;
    public static ArrayList<Shape> curves = new ArrayList<>();

    @Override
    public void handle(MouseEvent e) {
        Object source = e.getSource();

        deselect();

        if (source instanceof Line || source instanceof CubicCurve) {
            for (Road road: Road.roadList) {
                if (road.curves.contains(source)) {
                    (MainController.selectedNode = road).setSelect(true);
                    road.updateDrag();
                    MainController.SETTINGS_OBJECTS[0].show();

                    break;
                }
            }

        } else if (source instanceof ImageView) {
            for (TrafficLight trafficLight: TrafficLight.trafficLightList) {
                if (trafficLight.render == source) {
                    trafficLight.onSelect();
                    trafficLight.setSelect(true);
                    for (int i = 1; i <= 3; i++) MainController.SETTINGS_OBJECTS[i].show();

                    break;
                }
            }

            for (Vehicle vehicle: Vehicle.vehicleList) {
                if (vehicle.render == source) {
                    vehicle.onSelect();
                    vehicle.setSelect(true);
                    MainController.SETTINGS_OBJECTS[4].show();

                    break;
                }
            }
        }

        display();
    }

    public static void display() {
        for (Shape curve: curves) MainController.mainSelectedAnchorPane.getChildren().remove(curve);
        curves.clear();

        if (MainController.selectedNode == null) return;

        if (MainController.selectedNode instanceof Road selectedRoad) {
            Shape mainCurve = selectedRoad.curves.getFirst();

            minx = Double.MAX_VALUE;
            miny = Double.MAX_VALUE;
            maxx = Double.MIN_VALUE;
            maxy = Double.MIN_VALUE;

            if (mainCurve instanceof Line ln) {
                addPoint(ln.getStartX(), ln.getStartY());
                addPoint(ln.getEndX(), ln.getEndY());

            } else if (mainCurve instanceof CubicCurve) {
                for (int i = 0; i <= BezierRoad.APPROX_LENGTH_DIVISIONS; i++) {
                    addPoint(selectedRoad.getPoint((double) i / BezierRoad.APPROX_LENGTH_DIVISIONS));
                }
            }

            if (mainCurve instanceof Line) {
                for (Shape curve: selectedRoad.curves) {
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
                for (Shape curve: selectedRoad.curves) {
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

        } else if (MainController.selectedNode instanceof TrafficLight) {
            MainController.mainDisplayImageView.setVisible(true);
            MainController.mainDisplayImageView.setImage(
                TrafficLight.TEXTURES_CLEAR[((TrafficLight) MainController.selectedNode).currState]
            );

        } else if (MainController.selectedNode instanceof Car) {
            MainController.mainDisplayImageView.setVisible(true);
            MainController.mainDisplayImageView.setImage(Car.TEXTURE_CLEAR);

        } else if (MainController.selectedNode instanceof Truck) {
            MainController.mainDisplayImageView.setVisible(true);
            MainController.mainDisplayImageView.setImage(Truck.TEXTURE_CLEAR);
        }
    }

    public static void deselect() {
        EditableParameter.clear();

        MainController.mainDisplayImageView.setVisible(false);
        if (MainController.selectedNode != null) {
            MainController.selectedNode.setSelect(false);
            MainController.mainAnchorPane.getChildren().removeAll(MainController.selectedHighlights);
            MainController.selectedHighlights.clear();
            MainController.selectedNode = null;
        }

        for (BezierRoad road: BezierRoad.bezierRoadList) road.hideWeights();
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

    public static double getScale() {
        final double w = MainController.mainSelectedAnchorPane.getWidth();
        final double h = MainController.mainSelectedAnchorPane.getHeight();
        return Math.min(1, Math.min(w / (maxx - minx + 70), h / (maxy - miny + 90)));
    }

    public static double scaleX(double x) {
        final double w = MainController.mainSelectedAnchorPane.getWidth();
        return (x - minx) * getScale() + (w - (maxx - minx) * getScale()) / 2;
    }

    public static double scaleY(double y) {
        final double h = MainController.mainSelectedAnchorPane.getHeight();
        return (y - miny) * getScale() + (h - (maxy - miny) * getScale()) / 2;
    }
}
