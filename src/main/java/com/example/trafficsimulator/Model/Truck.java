package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import com.example.trafficsimulator.MainApplication;
import javafx.scene.image.*;

import java.util.*;

public class Truck extends Vehicle {
    public static final double DEFAULT_SPEED = 0.7;
    public static final Image TEXTURE = new Image(
            Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/truck.png")),
            60, 20, false, false
    );

    public static final Image TEXTURE_CLEAR = new Image(
            Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/truckClear.png"))
    );

    public Truck(Road road) {
        super(road);

        this.WIDTH = 60;
        this.HEIGHT = 20;
        this.road = road;
        this.DIAG = Math.sqrt(1082) + 5;

        final double ALPHA = Math.atan2(11, 31);
        this.CORNERANGLES = new double[] {-ALPHA, ALPHA, Math.PI - ALPHA, Math.PI + ALPHA};

        this.render = new ImageView(TEXTURE);
        initRenderPane();
        MainController.mainAnchorPane.getChildren().add(this.renderPane);
    }

    public Truck(Road road, Intersection intersection) {
        this(road);

        this.speed = DEFAULT_SPEED;

        if (intersection == this.road.start) {
            this.prev = this.road.start;
            this.next = this.road.end;

            roadRelPos = 0;

        } else {
            this.prev = this.road.end;
            this.next = this.road.start;

            roadRelPos = 1;
        }

        addToRoad();
        this.target = Vehicle.generateTarget(this.prev);

        updateRender();
    }
}
