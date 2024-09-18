package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.*;

public class Intersection {
    private Point point;
    private Circle circleObj;
    public ArrayList<TrafficLight> trafficLights = new ArrayList<>();
    public int index = -1;
    public HashMap<Road, Intersection> adjList = new HashMap<>();
    public static ArrayList<Intersection> intersectionList = new ArrayList<>();

    public Intersection() {
        intersectionList.add(this);
    }

    public Intersection(Point point) {
        this();
        this.point = new Point(point);
        this.circleObj = new Circle(point.getX(), point.getY(), 30);
        this.circleObj.setFill(Color.TRANSPARENT);  //TODO

        this.circleObj.setOnDragDetected(e -> {
            Dragboard db = this.circleObj.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString("node");
            db.setContent(content);

            e.consume();
        });
    }

    public void merge(Intersection intersection) {
        MainController.changeAllVehicleNode(intersection, this);

        for (Map.Entry<Road, Intersection> adj: intersection.adjList.entrySet()) {
            Road road = adj.getKey();
            Intersection adjInt = adj.getValue();

            if (road.getStart() == intersection) {
                road.start = this;
            } else {
                road.end = this;
            }

            adjInt.adjList.put(road, this);
            road.updateDrag();
            intersection.delete();
        }

        MainController.mainAnchorPane.getChildren().remove(intersection.getCircleObj());
        this.adjList.putAll(intersection.adjList);
    }

    public void add(Road road, Intersection intersection) {
        this.adjList.put(road, intersection);
    }

    public double getX() {
        return this.point.getX();
    }

    public double getY() {
        return this.point.getY();
    }

    public Circle getCircleObj() {
        return this.circleObj;
    }

    public void updateDrag() {
        for (Road adj: adjList.keySet()) {
            adj.updateDrag();
        }
    }

    public void setPoint(double x, double y) {
        this.point = new Point(x, y);
    }

    public void setPoint(Point point) {
        this.point = new Point(point);
    }

    public Point getPoint() {
        return new Point(this.point);
    }

    public double getDistance(Intersection intersection) {
        return Point.getDistance(this.point, intersection.getPoint());
    }

    public double getDistance(Point point) {
        return point.getDistance(this.point);
    }

    public static double getDistance(Intersection a, Intersection b) {
        return Point.getDistance(a.getPoint(), b.getPoint());
    }

    public static Intersection getIntersectionFromCircle(Circle node) {
        for (Intersection intersection: intersectionList) {
            if (intersection.getCircleObj().equals(node)) {
                return intersection;
            }
        }
        return new Intersection();
    }

    public Intersection closestIntersection() {
        return closestIntersection(Double.MAX_VALUE);
    }

    public Intersection closestIntersection(double maxDist) {
        Intersection ans = null;
        double currMin = Double.MAX_VALUE, curr;
        for (Intersection intersection: intersectionList) {
            if (intersection == this) continue;
            if ((curr = this.getDistance(intersection)) < currMin && curr < maxDist) {
                currMin = curr;
                ans = intersection;
            }
        }
        return ans;
    }

    public static Intersection closestIntersection(double maxDist, Point point) {
        Intersection ans = null;
        double currMin = Double.MAX_VALUE, curr;
        for (Intersection intersection: intersectionList) {
            if ((curr = intersection.getDistance(point)) < currMin && curr < maxDist) {
                currMin = curr;
                ans = intersection;
            }
        }
        return ans;
    }

    public void delete() {
        intersectionList.remove(this);
        MainController.mainAnchorPane.getChildren().remove(this.getCircleObj());

        for (Vehicle vehicle: Vehicle.vehicleList) {
            if (vehicle.target == this) {
                vehicle.target = null;
            }
        }
    }

    public void remove(Road road) {
        this.adjList.remove(road);
        if (this.adjList.isEmpty()) this.delete();
    }
}
