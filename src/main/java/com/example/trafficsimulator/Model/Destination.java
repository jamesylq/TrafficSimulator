package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import com.example.trafficsimulator.MainApplication;
import javafx.scene.image.*;

import java.util.*;

public class Destination {
    public int index;
    public ImageView render;
    public Intersection intersection;
    public static final Image TEXTURE = new Image(
            MainApplication.class.getResourceAsStream("Images/destination.png")
    );
    public static final double SIDE = 64;

    public static ArrayList<Intersection> destinationList = new ArrayList<>();

    public Destination(Intersection intersection) {
        this.intersection = intersection;
        this.intersection.destinationObj = this;
        destinationList.add(this.intersection);

        this.render = new ImageView(TEXTURE);
        this.render.xProperty().bind(intersection.borderCircleObj.centerXProperty().subtract(SIDE / 2));
        this.render.yProperty().bind(intersection.borderCircleObj.centerYProperty().subtract(SIDE));
        this.render.setMouseTransparent(true);

        MainController.mainAnchorPane.getChildren().add(this.render);
    }

    public void delete() {
        destinationList.remove(this.intersection);
        MainController.mainAnchorPane.getChildren().remove(this.render);
    }
}
