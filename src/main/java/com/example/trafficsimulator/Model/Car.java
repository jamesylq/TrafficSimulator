package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import com.example.trafficsimulator.MainApplication;
import javafx.scene.image.*;

import java.util.Objects;

public class Car extends Vehicle {
    public static final double DEFAULT_SPEED = 10;
    public static final Image CAR_IMAGE = new Image(
            Objects.requireNonNull(MainApplication.class.getResourceAsStream("Images/car.png")),
            38, 20, false, false
    );

    public Car(Road road) {
        this(road, DEFAULT_SPEED);
    }

    public Car(Road road, double speed) {
        super(road);

        this.road = road;
        this.speed = speed;
        this.name = "Car";
        this.render = new ImageView(CAR_IMAGE);

        MainController.mainAnchorPane.getChildren().add(this.render);
        updateRender();
    }

//    public void iterate() {
//        double step = this.speed * road.speed / road.length;
//        if (this.next == this.road.start) step *= -1;
//
//    }
}
