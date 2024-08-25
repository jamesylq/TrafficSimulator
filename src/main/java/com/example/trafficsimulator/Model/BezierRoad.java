package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;

import javafx.event.ActionEvent;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.*;

public class BezierRoad extends Road {
    private Circle weightStart, weightEnd;
    public static ArrayList<Circle> weights = new ArrayList<>();
    public static ArrayList<BezierRoad> bezierRoadList = new ArrayList<>();

    public static final int APPROX_LENGTH_DIVISIONS = 20;

    public BezierRoad(Intersection start, Circle weightStart, Circle weightEnd, Intersection end) {
        super(start, end);

        bezierRoadList.add(this);

        this.weightStart = weightStart;
        this.weightEnd = weightEnd;

        weights.add(this.weightStart);
        weights.add(this.weightEnd);

        this.weightStart.setOnDragDetected(e -> {
            Dragboard db = this.weightStart.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString("bezierWeight");
            db.setContent(content);

            e.consume();
        });

        this.weightEnd.setOnDragDetected(e -> {
            Dragboard db = this.weightEnd.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString("bezierWeight");
            db.setContent(content);

            e.consume();
        });

        for (int i = 0; i < 4; i++) this.addCurve(null);
        updateRender();

        this.addCurve(start.getCircleObj());
        this.addCurve(weightStart);
        this.addCurve(weightEnd);
        this.addCurve(end.getCircleObj());
    }

    public void updateRender() {
        if (this.selected) {
            CubicCurve highlight = new CubicCurve(
                    start.getX(), start.getY(),
                    weightStart.getCenterX(), weightStart.getCenterY(),
                    weightEnd.getCenterX(), weightEnd.getCenterY(),
                    end.getX(), end.getY()
            );
            highlight.setFill(Color.TRANSPARENT);
            highlight.setStroke(Color.AQUA);
            highlight.setStrokeWidth(55);
            if (MainController.selectedHighlight != null) {
                MainController.mainAnchorPane.getChildren().remove(MainController.selectedHighlight);
            }
            MainController.mainAnchorPane.getChildren().add(highlight);
            MainController.selectedHighlight = highlight;
        }

        CubicCurve A = new CubicCurve(
                start.getX(), start.getY(),
                weightStart.getCenterX(), weightStart.getCenterY(),
                weightEnd.getCenterX(), weightEnd.getCenterY(),
                end.getX(), end.getY()
        );
        A.setFill(Color.TRANSPARENT);
        A.setStroke(Color.BLACK);
        A.setStrokeWidth(50);
        this.curves.set(0, A);

        CubicCurve B = new CubicCurve(
                start.getX(), start.getY(),
                weightStart.getCenterX(), weightStart.getCenterY(),
                weightEnd.getCenterX(), weightEnd.getCenterY(),
                end.getX(), end.getY()
        );
        B.setFill(Color.TRANSPARENT);
        B.setStroke(Color.WHITE);
        B.setStrokeWidth(45);
        this.curves.set(1, B);

        Point sDer = Point.normal(derivative(0));
        Point eDer = Point.normal(derivative(1));

        CubicCurve C = new CubicCurve(
                start.getX() - 5 * sDer.getX(), start.getY() - 5 * sDer.getY(),
                weightStart.getCenterX(), weightStart.getCenterY(),
                weightEnd.getCenterX(), weightEnd.getCenterY(),
                end.getX() + 5 * eDer.getX(), end.getY() + 5 * eDer.getY()
        );
        C.setFill(Color.TRANSPARENT);
        C.setStroke(Color.BLACK);
        C.setStrokeWidth(40);
        this.curves.set(2, C);

        CubicCurve D = new CubicCurve(
                start.getX(), start.getY(),
                weightStart.getCenterX(), weightStart.getCenterY(),
                weightEnd.getCenterX(), weightEnd.getCenterY(),
                end.getX(), end.getY()
        );
        D.setFill(Color.TRANSPARENT);
        D.setStroke(Color.YELLOW);
        D.setStrokeWidth(5);
        D.setStyle("-fx-stroke-dash-array: 2 12 12 2;");
        this.curves.set(3, D);

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

    public Point getPoint(double t) {
        return new Point(
                (1 - t) * (1 - t) * (1 - t) * start.getX() +
                3 * (1 - t) * (1 - t) * t * weightStart.getCenterX() +
                3 * (1 - t) * t * t * weightEnd.getCenterX() +
                t * t * t * end.getX(),
                (1 - t) * (1 - t) * (1 - t) * start.getY() +
                3 * (1 - t) * (1 - t) * t * weightStart.getCenterY() +
                3 * (1 - t) * t * t * weightEnd.getCenterY() +
                t * t * t * end.getY()
        );
    }

    public Point derivative(double t) {
        return new Point(
                3 * (1 - t) * (1 - t) * (weightStart.getCenterX() - start.getX()) +
                6 * (1 - t) * t * (weightEnd.getCenterX() - weightStart.getCenterX()) +
                3 * t * t * (end.getX() - weightEnd.getCenterX()),
                3 * (1 - t) * (1 - t) * (weightStart.getCenterY() - start.getY()) +
                6 * (1 - t) * t * (weightEnd.getCenterY() - weightStart.getCenterY()) +
                3 * t * t * (end.getY() - weightEnd.getCenterY())
        );
    }

    public void iterate() {

    }

    public Circle getWeightStart() {
        return this.weightStart;
    }

    public Circle getWeightEnd() {
        return this.weightEnd;
    }

    public double calculateLength() {
        double len = 0;
        Point prev = this.getStart().getPoint(), curr;

        for (int i = 1; i <= APPROX_LENGTH_DIVISIONS; i++) {
            curr = getPoint((double) i / APPROX_LENGTH_DIVISIONS);
            len += prev.getDistance(curr);
            prev = curr;
        }

        return this.length = len;
    }

    public static BezierRoad getBezierFromWeight(Circle weight) {
        for (BezierRoad road: bezierRoadList) {
            if (road.getWeightStart() == weight || road.getWeightEnd() == weight) {
                return road;
            }
        }
        return null;
    }
}
