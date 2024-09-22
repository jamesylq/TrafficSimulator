package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import com.example.trafficsimulator.MainApplication;
import javafx.geometry.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.Comparator;
import java.util.Objects;

public class Car extends Vehicle {
    public static final double DEFAULT_SPEED = 1.0;
    public static final Image TEXTURE = new Image(
            Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/car.png")),
            38, 20, false, false
    );

    public Car(Road road) {
        super(road);

        this.WIDTH = 38;
        this.HEIGHT = 20;
        this.road = road;

        this.render = new ImageView(TEXTURE);
        initRenderPane();
        MainController.mainAnchorPane.getChildren().add(this.renderPane);
    }

    public Car(Road road, Intersection intersection) {
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
