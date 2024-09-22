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
import javafx.scene.shape.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    private ComboBox<String> graphCB;

    @FXML
    private TextArea graphTA;

    @FXML
    private AnchorPane anchorPane, selectedAnchorPane;
    public static AnchorPane mainAnchorPane, mainSelectedAnchorPane;

    public static ArrayList<Shape> permaFront = new ArrayList<>();

    @FXML
    private Accordion menuAccordion, selectedAccordion;

    @FXML
    private TitledPane menuTP, selectedTP;

    @FXML
    private Hyperlink aboutHL;

    @FXML
    private ImageView templateImg1, templateImg2, templateImg3, templateImg4, templateImg5;

    @FXML
    private Button simulateBtn, disconnectRoadBtn, deleteRoadBtn, clearBtn, loadBtn, saveBtn;
    public static Node[] disableOnSimulate;

    @FXML
    private Label settingL1, settingL2, settingL3, settingL4;
    public static Label MSL1, MSL2, MSL3, MSL4;

    @FXML
    private Slider settingS1, settingS2, settingS3, settingS4;
    public static Slider MSS1, MSS2, MSS3, MSS4;

    @FXML
    private ImageView displayImageView;
    public static ImageView mainDisplayImageView;

    public static Stage aboutPage;
    public static GraphEdge[][] dp;

    final String[] graphStats = {"Traffic Flow", "Average Vehicle Speed"};

    public static Selectable selectedNode = null;
    public static Shape selectedHighlight = null;
    public static boolean isSimulating = false;
    public static final Random random = new Random();

    public static final int FPS = 100;
    public static int SPAWN_VEHICLE_RAND_NUM = FPS / 2;

    @FXML
    public void updateCB() {
        int ind = graphCB.getSelectionModel().getSelectedIndex();
        graphTA.setText(String.format("Current %s: ", graphStats[ind]));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainAnchorPane = anchorPane;
        mainDisplayImageView = displayImageView;
        mainSelectedAnchorPane = selectedAnchorPane;
        MSL1 = settingL1;
        MSL2 = settingL2;
        MSL3 = settingL3;
        MSL4 = settingL4;
        MSS1 = settingS1;
        MSS2 = settingS2;
        MSS3 = settingS3;
        MSS4 = settingS4;

        disableOnSimulate = new Node[] {disconnectRoadBtn, deleteRoadBtn, clearBtn, loadBtn, saveBtn, templateImg1, templateImg2, templateImg3, templateImg4, templateImg5};

        graphCB.getItems().addAll(graphStats);
        graphCB.getSelectionModel().selectFirst();

        anchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                SelectHandler.deselect();
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

        templateImg4.setOnDragDetected(e -> {
            Dragboard db = templateImg4.startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putImage(templateImg4.getImage());
            content.putString("img4");
            db.setContent(content);

            e.consume();
        });

        templateImg5.setOnDragDetected(e -> {
            Dragboard db = templateImg5.startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putImage(templateImg5.getImage());

            content.putString("img5");
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
                        a.add(linearRoad, b);
                        b.add(linearRoad, a);

                        anchorPane.getChildren().addAll(linearRoad.curves);
                        updateLayers();

                        e.setDropCompleted(true);
                        e.consume();
                        break;

                    case "img2":
                        Intersection x = new Intersection(new Point(e.getX() - 50, e.getY() - 60));
                        Circle m = new Circle(e.getX() + 70, e.getY() - 20, 20);
                        Circle n = new Circle(e.getX() - 70, e.getY() + 20, 20);
                        Intersection y = new Intersection(new Point(e.getX() + 50, e.getY() + 60));
                        BezierRoad bezierRoad = new BezierRoad(x, m, n, y);
                        x.add(bezierRoad, y);
                        y.add(bezierRoad, x);

                        m.setVisible(false);
                        n.setVisible(false);

                        anchorPane.getChildren().addAll(bezierRoad.curves);
                        updateLayers();

                        e.setDropCompleted(true);
                        e.consume();
                        break;

                    case "img4":
                        Intersection closestIntersect = Intersection.closestIntersection(50, new Point(e.getX(), e.getY()));
                        if (closestIntersect == null) {
                            alertError("Invalid Placement!", "Traffic lights can only be placed on intersections!", "Try dragging a traffic light onto an intersection!");
                        } else {
                            for (Road adj: closestIntersect.adjList.keySet()) {
                                new TrafficLight(adj, closestIntersect);
                            }
                        }

                        e.setDropCompleted(true);
                        e.consume();
                        break;

                    case "img5":
                        Intersection closestIntersection = Intersection.closestIntersection(50, new Point(e.getX(), e.getY()));
                        if (closestIntersection == null) {
                            alertError("Invalid Placement!", "Only intersections can be destinations!", "Try dragging a destination object onto an intersection!");
                        } else if (closestIntersection.adjList.size() > 1) {
                            alertError("Invalid Placement!", "Destinations cannot be connected by more than one road!", "Try disconnecting one of the roads!");
                        } else {
                            new Destination(closestIntersection);
                        }

                        e.setDropCompleted(true);
                        e.consume();
                        break;

                    case "node":
                        Intersection node = Intersection.getIntersectionFromCircle((Circle) e.getGestureSource());
                        Intersection closest = node.closestIntersection(50);

                        for (Road road: node.adjList.keySet()) {
                            road.calculateLength();
                            if (road.selected) SelectHandler.display();
                        }

                        if (closest != null && !closest.adjList.containsValue(node)) {
                            if (node.adjList.size() + closest.adjList.size() > 4) {
                                alertError("Invalid Operation!", "Too many roads!", "Each intersection can have at most 4 roads.");
                                break;
                            }
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
        if (!menuTP.isExpanded()) menuAccordion.toFront();
        if (!selectedTP.isExpanded()) selectedAccordion.toFront();

        for (Intersection intersection: Intersection.intersectionList) intersection.borderCircleObj.toFront();
        for (Road road: Road.roadList) road.curves.get(2).toFront();
        for (Road road: Road.roadList) road.curves.get(3).toFront();
        for (Vehicle vehicle: Vehicle.vehicleList) vehicle.renderPane.toFront();
        for (TrafficLight trafficLight: TrafficLight.trafficLightList) trafficLight.renderPane.toFront();
        for (Intersection intersection: Destination.destinationList) intersection.destinationObj.render.toFront();
        draggableNodesToFront();
        for (Shape shape: permaFront) shape.toFront();

        if (menuTP.isExpanded()) menuAccordion.toFront();
        if (selectedTP.isExpanded()) selectedAccordion.toFront();
    }

    public static void draggableNodesToFront() {
        for (Circle circle: BezierRoad.weights) {
            circle.toFront();
        }
        for (Intersection intersection: Intersection.intersectionList) {
            intersection.circleObj.toFront();
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
    protected void toggleSimulate() {
        if (isSimulating) {
            simulateBtn.setText("Begin Simulation");
            for (Node dn: disableOnSimulate) dn.setDisable(false);

            for (Intersection intersection: Intersection.intersectionList) {
                intersection.circleObj.setVisible(true);
            }

        } else {
            if (Destination.destinationList.size() < 2) {
                alertError("Invalid Layout!", "Too little destinations!", "At least 2 destinations are required to be placed on intersections!");
                return;
            }

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

            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (dp[i][j].dist == 1e9) {
                        alertError("Error!", "Road Network Not Connected", "Ensure all roads are connected by at least one path!");
                        return;
                    }
                }
            }

            simulateBtn.setText("End Simulation");
            for (Node dn: disableOnSimulate) dn.setDisable(true);

            for (Intersection intersection: Intersection.intersectionList) {
                intersection.circleObj.setVisible(false);
            }

            for (Circle circle: BezierRoad.weights) {
                circle.setVisible(false);
            }

            SelectHandler.deselect();
            SelectHandler.display();
        }

        isSimulating = !isSimulating;
    }

    @FXML
    public void disconnectSelected() {
        if (selectedNode == null) {
            alertError("Invalid Operation!", "No road selected!", "Select a road by clicking on it!");
            return;
        }

        if (selectedNode instanceof Road selectedRoad) {
            if (selectedRoad.getStart().adjList.size() == 1 && selectedRoad.getEnd().adjList.size() == 1) {
                alertError("Invalid Operation!", "The selected road is not connected to any other road!", "Use this operation on a road which is connected to at least 1 other road.");
                return;
            }

            while (!selectedRoad.getStart().trafficLights.isEmpty()) {
                selectedRoad.getStart().trafficLights.getFirst().delete();
            }

            while (!selectedRoad.getEnd().trafficLights.isEmpty()) {
                selectedRoad.getEnd().trafficLights.getFirst().delete();
            }

            if (selectedRoad.getStart().adjList.size() > 1) {
                selectedRoad.getStart().remove(selectedRoad);
                Intersection start = new Intersection(selectedRoad.getPoint(0.1));
                changeAllVehicleNode(selectedRoad.getStart(), start);
                selectedRoad.setStart(start);
                start.add(selectedRoad, selectedRoad.getEnd());
                anchorPane.getChildren().add(start.circleObj);
            }

            if (selectedRoad.getEnd().adjList.size() > 1) {
                selectedRoad.getEnd().remove(selectedRoad);
                Intersection end = new Intersection(selectedRoad.getPoint(0.9));
                changeAllVehicleNode(selectedRoad.getEnd(), end);
                selectedRoad.setEnd(end);
                end.add(selectedRoad, selectedRoad.getStart());
                anchorPane.getChildren().add(end.circleObj);
            }

//            Intersection start = new Intersection(selectedRoad.getPoint(selectedRoad.getStart().adjList.isEmpty() ? 0.0 : 0.1));
//            Intersection end = new Intersection(selectedRoad.getPoint(selectedRoad.getEnd().adjList.isEmpty() ? 1.0 : 0.9));
//
//            changeAllVehicleNode(selectedRoad.getStart(), start);
//            changeAllVehicleNode(selectedRoad.getEnd(), end);
//
//            selectedRoad.setStart(start);
//            selectedRoad.setEnd(end);
//            start.add(selectedRoad, end);
//            end.add(selectedRoad, start);

//            anchorPane.getChildren().addAll(start.circleObj, end.circleObj);

            selectedRoad.updateDrag();
            updateLayers();
        }
    }

    public static void changeAllVehicleNode(Intersection oldIntersection, Intersection newIntersection) {
        for (Vehicle vehicle: Vehicle.vehicleList) {
            if (vehicle.next == oldIntersection) vehicle.next = newIntersection;
            if (vehicle.prev == oldIntersection) vehicle.prev = newIntersection;
            if (vehicle.target == oldIntersection) vehicle.target = newIntersection;
        }
    }

    @FXML
    public void deleteSelected() {
        if (selectedNode == null) return;

        if (selectedNode instanceof Road selectedRoad) {
            selectedRoad.delete();

            for (RoadObject roadObject: selectedRoad.fwdObjects) {
                if (roadObject instanceof Vehicle vehicle) {
                    vehicle.delete();
                }
            }

            for (RoadObject roadObject: selectedRoad.bckObjects) {
                if (roadObject instanceof Vehicle vehicle) {
                    vehicle.delete();
                }
            }

            while (!selectedRoad.getStart().trafficLights.isEmpty()) {
                selectedRoad.getStart().trafficLights.getFirst().delete();
            }

            while (!selectedRoad.getEnd().trafficLights.isEmpty()) {
                selectedRoad.getEnd().trafficLights.getFirst().delete();
            }
        }

        SelectHandler.deselect();
        SelectHandler.display();
    }

    public static void alertError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void saveGraph() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("savefiles/save.txt"));
            out.writeObject(new Arrangement());
            out.close();

            IntersectionWrapper.processedIntersections.clear();
            RoadWrapper.processedRoads.clear();
            VehicleWrapper.processedVehicles.clear();
            TrafficLightWrapper.processedTrafficLights.clear();
            DestinationWrapper.processedDestinations.clear();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Road roadAt(RoadWrapper roadWrap) {
        return Road.roadList.get(roadWrap.index);
    }

    public static Intersection intAt(IntersectionWrapper intWrap) {
        return Intersection.intersectionList.get(intWrap.index);
    }

    @FXML
    public void loadGraph() {
        clearGraph();

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream("savefiles/save.txt"));
            Arrangement arrangement = (Arrangement) input.readObject();
            input.close();

            for (IntersectionWrapper intWrap: arrangement.intersectionList) {
                new Intersection(intWrap.point);
            }

            for (RoadWrapper roadWrap: arrangement.roadList) {
                Road road = null;

                if (roadWrap instanceof BezierRoadWrapper bezier) {
                    road = new BezierRoad(
                        intAt(bezier.start),
                        new Circle(bezier.weightStart.getX(), bezier.weightStart.getY(), 20),
                        new Circle(bezier.weightEnd.getX(), bezier.weightEnd.getY(), 20),
                        intAt(bezier.end)
                    );

                } else if (roadWrap instanceof LinearRoadWrapper linear) {
                    road = new LinearRoad(intAt(linear.start), intAt(linear.end));
                }

                road.speed = roadWrap.speed;
                for (Shape shape: road.curves) {
                    if (!anchorPane.getChildren().contains(shape)) {
                        anchorPane.getChildren().add(shape);
                    }
                }
            }

            for (int i = 0; i < arrangement.intersectionList.size(); i++) {
                IntersectionWrapper intWrap = arrangement.intersectionList.get(i);
                for (Map.Entry<RoadWrapper, IntersectionWrapper> entry: intWrap.adjList.entrySet()) {
                    Intersection.intersectionList.get(i).adjList.put(roadAt(entry.getKey()), intAt(entry.getValue()));
                }
            }

            for (int i = 0; i < arrangement.vehicleList.size(); i++) {
                VehicleWrapper vWrap = arrangement.vehicleList.get(i);
                Vehicle vehicle = null;

                if (vWrap instanceof CarWrapper carWrap) {
                    vehicle = new Car(roadAt(carWrap.road));
                }

                vehicle.prev = intAt(vWrap.prev);
                vehicle.next = intAt(vWrap.next);
                vehicle.target = intAt(vWrap.target);
                vehicle.roadRelPos = vWrap.roadRelPos;

                vehicle.speed = vWrap.speed;
                vehicle.getPoint();
                vehicle.addToRoad();
            }

            for (DestinationWrapper destWrap: arrangement.destinationList) {
                new Destination(intAt(destWrap.intersection));
            }

            for (TrafficLightWrapper tlWrap: arrangement.trafficLightList) {
                new TrafficLight(roadAt(tlWrap.road), intAt(tlWrap.intersection));
            }

            updateLayers();

            IntersectionWrapper.processedIntersections.clear();
            RoadWrapper.processedRoads.clear();
            VehicleWrapper.processedVehicles.clear();
            TrafficLightWrapper.processedTrafficLights.clear();
            DestinationWrapper.processedDestinations.clear();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clearGraph() {
        anchorPane.getChildren().removeAll(BezierRoad.weights);
        for (Road road: Road.roadList) anchorPane.getChildren().removeAll(road.curves);
        for (Vehicle vehicle: Vehicle.vehicleList) anchorPane.getChildren().remove(vehicle.renderPane);
        for (Intersection intersection: Intersection.intersectionList) {
            anchorPane.getChildren().remove(intersection.circleObj);
            anchorPane.getChildren().remove(intersection.borderCircleObj);
            anchorPane.getChildren().remove(intersection.borderCircleObj2);

            if (intersection.destinationObj != null) intersection.destinationObj.delete();
        }

        while (!TrafficLight.trafficLightList.isEmpty()) TrafficLight.trafficLightList.getFirst().delete();

        Road.roadList.clear();
        BezierRoad.weights.clear();
        Vehicle.vehicleList.clear();
        BezierRoad.bezierRoadList.clear();
        Intersection.intersectionList.clear();

        SelectHandler.deselect();
        SelectHandler.display();
    }

    public void tick() {
        try {
            while (true) {
                if (isSimulating) {
                    Platform.runLater(() -> {
                        if (random.nextInt(SPAWN_VEHICLE_RAND_NUM) == 0) {
                            Intersection start = Vehicle.generateTarget();
                            for (Road road: start.adjList.keySet()) {
                                new Car(road, start);
                            }
                        }

                        for (Road road: Road.roadList) road.iterate();
                        for (Vehicle vehicle: Vehicle.vehicleList) vehicle.iterate();
                        for (TrafficLight trafficLight: TrafficLight.trafficLightList) trafficLight.iterate();
                        updateLayers();
                        TrafficLight.globalTick = ++TrafficLight.globalTick % TrafficLight.TICK_CYCLE;

                        ArrayList<Vehicle> remVehicles = new ArrayList<>();
                        for (Vehicle vehicle: Vehicle.vehicleList) if (!vehicle.deleted) remVehicles.add(vehicle);
                        Vehicle.vehicleList = remVehicles;
                    });

                } else {
                    Platform.runLater(() -> {
                        for (Vehicle vehicle: Vehicle.vehicleList) vehicle.updateRender();
                        for (TrafficLight trafficLight: TrafficLight.trafficLightList) trafficLight.updateRender();
                        updateLayers();
                    });
                }

                Thread.sleep((999 + FPS) / FPS);
            }

        } catch (InterruptedException ignored) {}
    }
}
