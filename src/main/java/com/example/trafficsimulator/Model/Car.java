package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import com.example.trafficsimulator.MainApplication;
import javafx.geometry.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

import java.util.Objects;

public class Car extends Vehicle {
    public static final double DEFAULT_SPEED = 1.0;
    public static final Image CAR_IMAGE = new Image(
            Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/car.png")),
            38, 20, false, false
    );

    public Car(Road road) {
        this(road, DEFAULT_SPEED);
    }

    public Car(Road road, double speed) {
        super(road);

        WIDTH = 38;
        HEIGHT = 20;

        this.road = road;
        this.speed = speed;
        this.name = "Car";

        this.render = new ImageView(CAR_IMAGE);
        initRenderPane();

        MainController.mainAnchorPane.getChildren().add(this.renderPane);
        updateRender();
    }
}
