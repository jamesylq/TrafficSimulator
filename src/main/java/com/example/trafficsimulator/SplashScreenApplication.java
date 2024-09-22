package com.example.trafficsimulator;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;
import java.io.*;
import java.util.Random;

import javafx.scene.image.Image;


public class SplashScreenApplication extends Application {
    public static final Image SPLASH_IMAGE =  new Image(
            MainApplication.class.getResourceAsStream("Images/splash.png"),
            600, 540, false, false
    );

    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        ImageView splash = new ImageView(SPLASH_IMAGE);
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(600);
        progressText = new Label("Tip");
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setStyle("-fx-padding: 5; -fx-background-color: cornsilk; -fx-border-width:5; -fx-border-color: linear-gradient(to bottom, chocolate, derive(chocolate, 50%));");
        splashLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(final Stage initStage) throws Exception {
        final Task<Object> loading = new Task<>() {
            @Override
            protected Object call() throws InterruptedException {
                final Random random = new Random();
                final String[] TIPS = {
                    "Click on a road object to view its properties!",
                    "Trucks move slower than Cars!",
                    "Traffic Lights can only be connected to 3-road or 4-road intersections!",
                    "Drag roads on top of each other to connect them!",
                    "Destinations can only have one road connected!",
                    "Vehicles always prefer a shorter path!"
                };

                for (int i = 0; i < 3; i++) {
                    updateMessage(TIPS[random.nextInt(TIPS.length)]);
                    updateProgress(i + 1, 3);
                    Thread.sleep(1000);
                }
                
                return null;
            }
        };

        showSplash(initStage, loading, () -> {
            MainApplication app = new MainApplication();
            try { app.start(new Stage()); }
            catch (IOException ignored) {}
        });
        new Thread(loading).start();
    }

    private void showSplash(final Stage initStage, Task<?> task, InitCompletionHandler initCompletionHandler) {
        progressText.textProperty().bind(task.messageProperty());
        loadProgress.progressProperty().bind(task.progressProperty());
        task.stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loadProgress.progressProperty().unbind();
                loadProgress.setProgress(1);
                initStage.toFront();
                FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
                fadeSplash.setFromValue(1.0);
                fadeSplash.setToValue(0.0);
                fadeSplash.setOnFinished(actionEvent -> initStage.hide());
                fadeSplash.play();

                initCompletionHandler.complete();
            }
        });

        Scene splashScene = new Scene(splashLayout);
        initStage.initStyle(StageStyle.UNDECORATED);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - 300);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - 400);
        initStage.show();
    }

    public interface InitCompletionHandler {
        void complete();
    }
}
