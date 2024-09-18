package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import com.example.trafficsimulator.MainApplication;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.*;

public class TrafficLight extends RoadObject implements Iterable, Selectable {
    public static final int RED_DURATION = 1000, YELLOW_DURATION = 100, GREEN_DURATION = 900;
    public static final int TICK_CYCLE = RED_DURATION + YELLOW_DURATION + GREEN_DURATION;
    public static final double ALPHA = Math.atan2(56, 26);
    public static final double DIAG = Math.sqrt(953) + 5;
    public static final double[] CORNERANGLES = {ALPHA, Math.PI - ALPHA, Math.PI + ALPHA, -ALPHA};

    public static final Image[] TEXTURES = {
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightGreen.png")),
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightYellow.png")),
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightRed.png"))
    };

    public static ArrayList<TrafficLight> trafficLightList = new ArrayList<>();

    private int phase, tick;
    public static int globalTick = 0;
    public int currState;
    private Road road;
    public Intersection intersection;
    public double renderRoadRelPos, rx, ry;
    public boolean selected = false;

    public TrafficLight(Road road, Intersection intersection) {
        this(road, intersection, (new Random()).nextDouble());
    }

    public TrafficLight(Road road, Intersection intersection, double phase) {
        super(road);

        if (alreadyExists(road, intersection)) return;

        WIDTH = 24;
        HEIGHT = 60;

        this.road = road;
        this.phase = (int) (phase * TICK_CYCLE);
        this.intersection = intersection;

        if (this.road.end == this.intersection) {
            this.roadRelPos = 1;
            this.road.fwdObjects.add(this);
            this.road.bckObjects.addFirst(this);
        } else {
            this.roadRelPos = 0;
            this.road.fwdObjects.addFirst(this);
            this.road.bckObjects.add(this);
        }

        this.renderRoadRelPos = Math.min(0.1, 50 / this.road.length);
        if (this.road.end == this.intersection) this.renderRoadRelPos = 1 - this.renderRoadRelPos;
        getPoint();

        trafficLightList.add(this);
        this.intersection.trafficLights.add(this);

        this.render = new ImageView(TEXTURES[updateState()]);
        initRenderPane();

        MainController.mainAnchorPane.getChildren().add(this.renderPane);
        updateRender();
    }

    public int updateState() {
        if (tick < GREEN_DURATION) {
            collidable = false;
            return currState = 0;
        }
        if (tick < GREEN_DURATION + YELLOW_DURATION) {
            collidable = false;
            return currState = 1;
        }
        collidable = true;
        return currState = 2;
    }

    public void updateRender() {
        this.renderRoadRelPos = Math.min(0.3, 100 / this.road.length);
        if (this.road.end == this.intersection) this.renderRoadRelPos = 1 - this.renderRoadRelPos;
        getPoint();

        this.render.setRotate(this.road.getAngle(this.renderRoadRelPos) * 180 / Math.PI);

        this.renderPane.setLayoutX((this.rx = this.road.getPoint(this.renderRoadRelPos).getX()) - MAXSIDE / 2);
        this.renderPane.setLayoutY((this.ry = this.road.getPoint(this.renderRoadRelPos).getY()) - MAXSIDE / 2);

        if (this.selected) onSelect();
    }

    public void iterate() {
        tick = ((globalTick + phase) % TICK_CYCLE + TICK_CYCLE) % TICK_CYCLE;

        if (tick == 0 || tick == GREEN_DURATION || tick == GREEN_DURATION + YELLOW_DURATION) {
            render.setImage(TEXTURES[updateState()]);
        }

        updateRender();
    }

    public static boolean alreadyExists(Road road, Intersection intersection) {
        for (TrafficLight trafficLight: trafficLightList) {
            if (trafficLight.road == road && trafficLight.intersection == intersection) {
                return true;
            }
        }
        return false;
    }

    public void delete() {
        MainController.mainAnchorPane.getChildren().remove(this.renderPane);
        this.intersection.trafficLights.remove(this);
        trafficLightList.remove(this);
    }

    public void onSelect() {
        Polygon highlight = new Polygon();
        final double angle = this.road.getAngle(this.renderRoadRelPos);
        for (double cornerAngle: CORNERANGLES) {
            highlight.getPoints().add(this.rx + Math.cos(angle + cornerAngle) * DIAG);
            highlight.getPoints().add(this.ry + Math.sin(angle + cornerAngle) * DIAG);
        }
        highlight.setFill(Color.AQUA);

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
