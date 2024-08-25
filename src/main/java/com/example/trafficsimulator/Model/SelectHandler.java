package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;

import javafx.event.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;

public class SelectHandler implements EventHandler<MouseEvent> {
    @Override
    public void handle(MouseEvent e) {
        Shape source = (Shape) e.getSource();
        for (Road road: Road.roadList) {
            if (road.curves.contains(source)) {
                if (MainController.selectedRoad != null) {
                    MainController.selectedRoad.selected = false;
                    MainController.mainAnchorPane.getChildren().remove(MainController.selectedHighlight);
                }
                MainController.selectedRoad = road;
                road.selected = true;
                road.updateDrag();
                for (int i = 3; i >= 0; i--) road.curves.get(i).toBack();
                MainController.selectedHighlight.toBack();
                MainController.draggableNodesToFront();
                break;
            }
        }
    }
}
