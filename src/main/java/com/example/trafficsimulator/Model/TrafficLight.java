package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import com.example.trafficsimulator.MainApplication;
import javafx.geometry.Pos;
import javafx.scene.image.*;
import javafx.scene.layout.FlowPane;

import java.util.*;

public class TrafficLight extends RoadObject implements Iterable {
    public static final int RED_DURATION = 1000, YELLOW_DURATION = 100, GREEN_DURATION = 900;
    public static final int TICK_CYCLE = RED_DURATION + YELLOW_DURATION + GREEN_DURATION;

    public static final Image[] TEXTURES = {
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightGreen.png")),
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightYellow.png")),
        new Image(MainApplication.class.getResourceAsStream("Images/trafficLightRed.png"))
    };

    public static ArrayList<TrafficLight> trafficLightList = new ArrayList<>();

    private int tick;
    public int currState;
    private Road road;
    public Intersection intersection;

    public TrafficLight(Road road, Intersection intersection) {
        this(road, intersection, 0);
    }

    public TrafficLight(Road road, Intersection intersection, int phase) {
        super(road);

        if (alreadyExists(road, intersection)) return;

        WIDTH = 24;
        HEIGHT = 60;

        this.road = road;
        this.tick = phase % TICK_CYCLE;
        this.intersection = intersection;

        this.roadRelPos = Math.min(0.1, 50 / this.road.length);
        if (this.road.end == intersection) this.roadRelPos = 1 - this.roadRelPos;
        getPoint();

        trafficLightList.add(this);
        this.intersection.trafficLights.add(this);

        this.render = new ImageView(TEXTURES[updateState()]);
        initRenderPane();

        MainController.mainAnchorPane.getChildren().add(this.renderPane);
        updateRender();
    }

    public int updateState() {
        if (tick < GREEN_DURATION) return currState = 0;
        if (tick < GREEN_DURATION + YELLOW_DURATION) return currState = 1;
        return currState = 2;
    }

    public void updateRender() {
        this.roadRelPos = Math.min(0.3, 100 / this.road.length);
        if (this.road.end == this.intersection) this.roadRelPos = 1 - this.roadRelPos;
        getPoint();

        this.render.setRotate(this.road.getAngle(this.roadRelPos) * 180 / Math.PI);

        this.renderPane.setLayoutX(this.getX() - MAXSIDE / 2);
        this.renderPane.setLayoutY(this.getY() - MAXSIDE / 2);
    }

    public void iterate() {
        tick = ++tick % TICK_CYCLE;

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
}
