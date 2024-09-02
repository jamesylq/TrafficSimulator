package com.example.trafficsimulator.Controller;

import com.example.trafficsimulator.MainApplication;
import com.example.trafficsimulator.Model.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    private ComboBox<String> graphCB;

    @FXML
    private TextArea graphTA;

    @FXML
    private AnchorPane anchorPane;
    public static AnchorPane mainAnchorPane;

    @FXML
    private Accordion accordion;

    @FXML
    private Hyperlink aboutHL;

    @FXML
    private ImageView templateImg1, templateImg2, templateImg3;

    public static Stage aboutPage;

    public static GraphEdge[][] dp;

    final String[] graphStats = {"Traffic Flow", "Average Vehicle Speed"};

    public static Road selectedRoad = null;
    public static Shape selectedHighlight = null;

    @FXML
    public void updateCB() {
        int ind = graphCB.getSelectionModel().getSelectedIndex();
        graphTA.setText(String.format("Current %s: ", graphStats[ind]));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainAnchorPane = anchorPane;

        graphCB.getItems().addAll(graphStats);
        graphCB.getSelectionModel().selectFirst();

        anchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (selectedRoad != null) {
                    selectedRoad.selected = false;
                    anchorPane.getChildren().remove(selectedHighlight);
                    selectedRoad = null;
                    selectedHighlight = null;
                }
            }
        });

        anchorPane.requestFocus();

        templateImg1.setOnDragDetected(e -> {
            Dragboard db = templateImg1.startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putImage(templateImg1.getImage());
            content.putString("img1");
            db.setContent(content);

            e.consume();
        });

        templateImg2.setOnDragDetected(e -> {
            Dragboard db = templateImg2.startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putImage(templateImg2.getImage());
            content.putString("img2");
            db.setContent(content);

            e.consume();
        });

        anchorPane.setOnDragOver(e -> {
            e.acceptTransferModes(TransferMode.ANY);
            e.consume();

            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                switch (db.getString()) {
                    case "bezierWeight":
                        Circle source1 = (Circle) e.getGestureSource();
                        BezierRoad.getBezierFromWeight(source1).updateDrag();
                        updateLayers();
                        source1.setCenterX(e.getX());
                        source1.setCenterY(e.getY());

                        break;

                    case "node":
                        Circle source2 = (Circle) e.getGestureSource();
                        Intersection node = Intersection.getIntersectionFromCircle(source2);

                        source2.setCenterX(e.getX());
                        source2.setCenterY(e.getY());
                        node.setPoint(e.getX(), e.getY());
                        node.updateDrag();
                        updateLayers();
                        break;
                }

            }
        });

        anchorPane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();

            if (db.hasString()) {
                switch (db.getString()) {
                    case "img1":
                        Intersection a = new Intersection(new Point(e.getX() - 50, e.getY()));
                        Intersection b = new Intersection(new Point(e.getX() + 50, e.getY()));
                        LinearRoad linearRoad = new LinearRoad(a, b);
                        a.adjList.put(linearRoad, b);
                        b.adjList.put(linearRoad, a);

                        anchorPane.getChildren().addAll(linearRoad.curves);
                        updateLayers();

                        e.setDropCompleted(true);
                        e.consume();
                        break;

                    case "img2":
                        Intersection x = new Intersection(new Point(e.getX() - 50, e.getY() - 60));
                        Circle m = new Circle(e.getX() + 70, e.getY() - 20, 20);
                        m.setStroke(Color.AQUA);
                        m.setStrokeWidth(5);
                        Circle n = new Circle(e.getX() - 70, e.getY() + 20, 20);
                        n.setStroke(Color.AQUA);
                        n.setStrokeWidth(5);
                        Intersection y = new Intersection(new Point(e.getX() + 50, e.getY() + 60));
                        BezierRoad bezierRoad = new BezierRoad(x, m, n, y);
                        x.adjList.put(bezierRoad, y);
                        y.adjList.put(bezierRoad, x);

                        anchorPane.getChildren().addAll(bezierRoad.curves);
                        updateLayers();

                        e.setDropCompleted(true);
                        e.consume();
                        break;

                    case "node":
                        Intersection node = Intersection.getIntersectionFromCircle((Circle) e.getGestureSource());
                        Intersection closest = node.closestIntersection(50);

                        for (Road road: node.adjList.keySet()) {
                            road.calculateLength();
                        }

                        if (closest != null && !closest.adjList.containsValue(node)) {
                            closest.merge(node);
                            updateLayers();
                        }

                        break;

                    case "bezierWeight":
                        BezierRoad.getBezierFromWeight((Circle) e.getGestureSource()).calculateLength();
                        break;

                }
            }
        });


        Runnable simulation = this::tick;
        Thread simulationThread = new Thread(simulation);
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    public void updateLayers() {
        accordion.toFront();
        for (Vehicle vehicle: Vehicle.vehicleList) vehicle.render.toFront();
        draggableNodesToFront();
    }

    public static void draggableNodesToFront() {
        for (Circle circle: BezierRoad.weights) {
            circle.toFront();
        }
        for (Intersection intersection: Intersection.intersectionList) {
            intersection.getCircleObj().toFront();
        }
    }

    @FXML
    protected void aboutPage(ActionEvent ignored) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("View/about-view.fxml")));
            stage.setScene(new Scene(root));
            stage.setTitle("About the Programmer");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(aboutHL.getScene().getWindow());

            stage.setMinWidth(340);
            stage.setMaxWidth(340);
            stage.setMinHeight(500);
            stage.setMaxHeight(500);

            aboutPage = stage;
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void beginSimulate() {
        final int N = Intersection.intersectionList.size();

        dp = new GraphEdge[N][N];
        for (int i = 0; i < N; i++) {
            Intersection.intersectionList.get(i).index = i;

            for (int j = 0; j < N; j++) {
                if (i != j) {
                    dp[i][j] = new GraphEdge(1e9, null, null);
                } else {
                    dp[i][j] = new GraphEdge(0, null, null);
                }
            }
        }

        for (Road road: Road.roadList) {
            int u = road.getStart().index;
            int v = road.getEnd().index;
            dp[u][v] = new GraphEdge(road.getLength(), road, road.getEnd());
            dp[v][u] = new GraphEdge(road.getLength(), road, road.getStart());
        }

        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (dp[i][j].dist > dp[i][k].dist + dp[k][j].dist) {
                        dp[i][j] = new GraphEdge(
                                dp[i][k].dist + dp[k][j].dist,
                                dp[i][k].edge,
                                dp[i][k].adj
                        );
                    }
                }
            }
        }

        System.out.println(Arrays.deepToString(dp));
    }

    public void tick() {
        Platform.runLater(() -> {
            for (Vehicle vehicle: Vehicle.vehicleList) vehicle.iterate();
        });

        try {
            Thread.sleep(100);
            tick();
        } catch (InterruptedException ignored) {}
    }
}
