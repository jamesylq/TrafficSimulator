package com.example.trafficsimulator.Model;

import com.example.trafficsimulator.Controller.MainController;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class EditableParameter {
    public static ArrayList<EditableParameter> params = new ArrayList<>();

    int ind;
    public Label settingName, leftLabel, rightLabel, valueLabel;
    public Slider slider;
    public boolean visible = false;
    private ObservableSetting onUpdate;

    public EditableParameter(String name, double minVal, double maxVal, double defaultVal, UpdatableSetting lambda, ObservableSetting lambda2) {
        slider = new Slider(minVal, maxVal, defaultVal);
        slider.valueProperty().addListener(e -> {
            lambda.update(slider.getValue());
            valueLabel.setText(Double.toString(slider.getValue()));
        });

        settingName = new Label(name);
        leftLabel = new Label(Double.toString(minVal));
        rightLabel = new Label(Double.toString(maxVal));
        valueLabel = new Label(Double.toString(defaultVal));
        onUpdate = lambda2;

        params.add(this);
    }

    public static void clear() {
        for (EditableParameter param: params) {
            if (param.visible) {
                param.hide();
            }
        }
    }

    public void hide() {
        if (!visible) return;
        visible = false;

        MainController.mainSettingsGridPane.getChildren().removeAll(settingName, slider, leftLabel, rightLabel, valueLabel);
    }

    public void show() {
        if (visible) return;
        visible = true;

        double val = onUpdate.onUpdate();

        MainController.mainSettingsGridPane.add(settingName, 0, ind * 2 + 1);
        GridPane.setValignment(settingName, VPos.BOTTOM);
        GridPane.setHalignment(settingName, HPos.CENTER);

        MainController.mainSettingsGridPane.add(leftLabel, 0, ind * 2 + 1);
        GridPane.setValignment(leftLabel, VPos.BOTTOM);
        GridPane.setHalignment(leftLabel, HPos.LEFT);
        GridPane.setMargin(leftLabel, new Insets(0, 0, 0, 10));

        MainController.mainSettingsGridPane.add(rightLabel, 0, ind * 2 + 1);
        GridPane.setValignment(rightLabel, VPos.BOTTOM);
        GridPane.setHalignment(rightLabel, HPos.RIGHT);
        GridPane.setMargin(rightLabel, new Insets(0, 10, 0, 0));

        MainController.mainSettingsGridPane.add(valueLabel, 1, ind * 2 + 2);
        GridPane.setValignment(valueLabel, VPos.TOP);
        GridPane.setHalignment(valueLabel, HPos.LEFT);
        valueLabel.setText(String.format("%.2f", val));

        MainController.mainSettingsGridPane.add(slider, 0, ind * 2 + 2);
        GridPane.setValignment(slider, VPos.TOP);
        GridPane.setHalignment(slider, HPos.CENTER);
        GridPane.setMargin(slider, new Insets(0, 10, 0, 10));
        slider.setValue(val);
    }
}
