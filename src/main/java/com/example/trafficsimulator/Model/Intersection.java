package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.io.Serializable;
import java.util.*;

public class Intersection {
    public int index;
    private Point point;
    public Destination destinationObj = null;
    public Circle circleObj, borderCircleObj, borderCircleObj2;
    public HashMap<Road, Intersection> adjList = new HashMap<>();
    public ArrayList<TrafficLight> trafficLights = new ArrayList<>();
    public static ArrayList<Intersection> intersectionList = new ArrayList<>();

    public Intersection() {
        intersectionList.add(this);
    }

    public Intersection(Point point) {
        this();
        this.point = new Point(point);
        this.circleObj = new Circle(point.getX(), point.getY(), 37);
        this.circleObj.setFill(Color.TRANSPARENT);

        this.borderCircleObj = new Circle(32);
        this.borderCircleObj.setStroke(Color.WHITE);
        this.borderCircleObj.setMouseTransparent(true);
        this.borderCircleObj.setStrokeWidth(2.5);
        this.borderCircleObj.centerXProperty().bind(this.circleObj.centerXProperty());
        this.borderCircleObj.centerYProperty().bind(this.circleObj.centerYProperty());

        this.borderCircleObj2 = new Circle(point.getX(), point.getY(), 37);
        this.borderCircleObj2.setFill(Color.BLACK);
        this.borderCircleObj2.centerXProperty().bind(this.circleObj.centerXProperty());
        this.borderCircleObj2.centerYProperty().bind(this.circleObj.centerYProperty());

        MainController.mainAnchorPane.getChildren().addAll(this.borderCircleObj, this.borderCircleObj2);

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

        boolean hasTrafficLight = false;

        if (!intersection.trafficLights.isEmpty()) {
            hasTrafficLight = true;
            while (!intersection.trafficLights.isEmpty()) intersection.trafficLights.getFirst().delete();
        }

        if (!this.trafficLights.isEmpty()) {
            hasTrafficLight = true;
            while (!this.trafficLights.isEmpty()) this.trafficLights.getFirst().delete();
        }

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

        MainController.mainAnchorPane.getChildren().remove(intersection.circleObj);
        this.adjList.putAll(intersection.adjList);

        if (hasTrafficLight) {
            for (Road adj : this.adjList.keySet()) new TrafficLight(adj, this);
            for (Road adj : this.adjList.keySet()) {
                adj.fwdObjects.sort(Comparator.naturalOrder());
                adj.bckObjects.sort(Comparator.reverseOrder());
            }
            TrafficLight.sync(this);
        }
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

    public static Intersection getIntersectionFromCircle(Circle node) {
        for (Intersection intersection: intersectionList) {
            if (intersection.circleObj.equals(node)) {
                return intersection;
            }
        }
        return null;
    }

    public Intersection closestIntersection(double maxDist) {
        boolean destinationWarning = false;
        Intersection ans = null;
        double currMin = Double.MAX_VALUE, curr;
        for (Intersection intersection: intersectionList) {
            if (intersection == this) continue;

            curr = this.getDistance(intersection);

            if (intersection.destinationObj != null) {
                if (curr <= 40) destinationWarning = true;
                continue;
            }

            if (curr < Math.min(currMin, maxDist)) {
                currMin = curr;
                ans = intersection;
            }
        }

        if (ans == null && destinationWarning || this.destinationObj != null && currMin <= 40) {
            MainController.alertError("Invalid Placement!", "Destinations cannot be connected by more than one road!", "Try disconnecting one of the roads!");
        }

        return (this.destinationObj == null ? ans : null);
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
        if (this.destinationObj != null) this.destinationObj.delete();

        intersectionList.remove(this);
        MainController.mainAnchorPane.getChildren().removeAll(this.borderCircleObj, this.borderCircleObj2);

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
