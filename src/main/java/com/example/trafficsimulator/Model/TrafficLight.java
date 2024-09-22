package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import com.example.trafficsimulator.MainApplication;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.*;

public class TrafficLight extends RoadObject implements Iterable, Selectable {
    public int index;
    public double angle;
    public int YELLOW_DURATION = 100, GREEN_DURATION = 900;
    public int RED_DURATION = YELLOW_DURATION + GREEN_DURATION, TICK_CYCLE = RED_DURATION * 2;
    public static final double ALPHA = Math.atan2(56, 26);
    public static final double DIAG = Math.sqrt(953) + 5;
    public static final double[] CORNERANGLES = {ALPHA, Math.PI - ALPHA, Math.PI + ALPHA, -ALPHA};

    public static final Image[] TEXTURES = {
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightGreen.png")),
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightYellow.png")),
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightRed.png"))
    };

    public static final Image[] TEXTURES_CLEAR = {
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightGreenClear.png")),
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightYellowClear.png")),
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightRedClear.png"))
    };

    public static ArrayList<TrafficLight> trafficLightList = new ArrayList<>();

    public int phase, tick;
    public static long globalTick = 0;
    public int currState;
    public Intersection intersection;
    public double rx, ry;
    public boolean selected = false;
    public static final Random random = new Random();
    public final ArrayList<Obstacle> obstacles = new ArrayList<>();

    public TrafficLight(Road road, Intersection intersection) {
        this(road, intersection, -1);
    }

    public TrafficLight(Road road, Intersection intersection, int phase) {
        super(road);

        if (alreadyExists(road, intersection)) return;

        this.WIDTH = 24;
        this.HEIGHT = 60;
        this.collidable = false;

        this.road = road;
        this.phase = (phase < 0 ? random.nextInt(TICK_CYCLE) : phase);
        this.intersection = intersection;
        updateState();

        this.angle = this.road.getAngle(isFront() ? 1e-4 : 1 - 1e-4);
        if (!isFront()) this.angle -= Math.PI;
        this.angle = (this.angle % Math.TAU + Math.TAU) % Math.TAU;

        this.road.fwdObjects.add(this);
        this.road.bckObjects.add(this);

        for (Road r: this.intersection.adjList.keySet()) {
            if (r == this.road) continue;
            this.obstacles.add(new Obstacle(this, r));
        }

        this.roadRelPos = Math.min(0.1, 50 / this.road.length);
        if (!isFront()) this.roadRelPos = 1 - this.roadRelPos;
        getPoint();

        trafficLightList.add(this);
        this.intersection.trafficLights.add(this);

        this.render = new ImageView(TEXTURES[updateState()]);
        initRenderPane();

        MainController.mainAnchorPane.getChildren().add(this.renderPane);
        updateRender();
    }

    public boolean isFront() {
        return this.road.start == this.intersection;
    }

    public int updateState() {
        RED_DURATION = GREEN_DURATION + YELLOW_DURATION;
        TICK_CYCLE = RED_DURATION * 2;

        if (tick < GREEN_DURATION) return currState = 0;
        if (tick < GREEN_DURATION + YELLOW_DURATION) return currState = 1;
        return currState = 2;
    }

    public void updateRender() {
        this.roadRelPos = Math.min(0.3, 100 / this.road.length);
        if (this.road.end == this.intersection) this.roadRelPos = 1 - this.roadRelPos;
        getPoint();

        this.render.setRotate(this.road.getAngle(this.roadRelPos) * 180 / Math.PI);

        this.renderPane.setLayoutX((this.rx = this.road.getPoint(this.roadRelPos).getX()) - MAXSIDE / 2);
        this.renderPane.setLayoutY((this.ry = this.road.getPoint(this.roadRelPos).getY()) - MAXSIDE / 2);

        if (this.selected) onSelect();
    }

    public void iterate() {
        tick = ((int) ((globalTick + phase) % TICK_CYCLE) + TICK_CYCLE) % TICK_CYCLE;

        if (tick >= GREEN_DURATION + YELLOW_DURATION) {
            for (Obstacle obst: obstacles) obst.collidable = true;
        } else {
            for (Obstacle obst: obstacles) obst.collidable = false;
        }

        if (tick == 0 || tick == GREEN_DURATION || tick == GREEN_DURATION + YELLOW_DURATION) {
            render.setImage(TEXTURES[updateState()]);

            if (this.selected) SelectHandler.display();
        }

        updateRender();
    }

    public boolean isCollidable(Vehicle vehicle) {
        return false;
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
        final double angle = this.road.getAngle(this.roadRelPos);
        for (double cornerAngle: CORNERANGLES) {
            highlight.getPoints().add(this.rx + Math.cos(angle + cornerAngle) * DIAG);
            highlight.getPoints().add(this.ry + Math.sin(angle + cornerAngle) * DIAG);
        }
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.AQUA);
        highlight.setStrokeWidth(3);

        if (!MainController.selectedHighlights.isEmpty()) {
            MainController.mainAnchorPane.getChildren().removeAll(MainController.selectedHighlights);
        }
        MainController.selectedHighlights.clear();
        MainController.mainAnchorPane.getChildren().add(highlight);
        MainController.selectedHighlights.add(highlight);
        MainController.selectedNode = this;

        if (this.selected) SelectHandler.display();
    }

    public void setSelect(boolean b) {
        selected = b;
    }

    public boolean getSelect() {
        return selected;
    }
}
