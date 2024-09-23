package com.example.trafficsimulator.Controller;

import com.example.trafficsimulator.MainApplication;
import com.example.trafficsimulator.Model.*;

import javafx.application.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.stage.FileChooser.*;

import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.time.format.*;

public class MainController implements Initializable {
    @FXML
    private ComboBox<String> graphCB;

    @FXML
    private TextArea graphTA, statsTA;

    @FXML
    private AnchorPane anchorPane, selectedAnchorPane;
    public static AnchorPane mainAnchorPane, mainSelectedAnchorPane;

    @FXML
    private GridPane settingsGridPane;
    public static GridPane mainSettingsGridPane;

    @FXML
    private Accordion menuAccordion, selectedAccordion;

    @FXML
    private TitledPane menuTP, selectedTP;

    @FXML
    private Hyperlink aboutHL;

    @FXML
    private ImageView templateImg1, templateImg2, templateImg3, templateImg4, templateImg5;

    @FXML
    private Button simulateBtn, disconnectSelBtn, deleteSelBtn, clearBtn, loadBtn, saveBtn;
    public static Node[] disableOnSimulate;

    @FXML
    private ImageView displayImageView;
    public static ImageView mainDisplayImageView;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private Rectangle rect;

    public static Stage aboutPage;
    public static GraphEdge[][] dp;

    public static final String[] graphStats = {"Traffic Flow Rate", "Average Vehicle Speed"};

    public static Selectable selectedNode = null;
    public static ArrayList<Shape> selectedHighlights = new ArrayList<>();
    public static boolean isSimulating = false;
    public static final Random random = new Random();
    public static double avgSpeed;
    public static long ticksSinceStart = 0;
    public static int tripsMade = 0;

    public static final int FPS = 100;
    public static int SPAWN_VEHICLE_RAND_NUM = FPS;
    public static int SINCE_LAST_UPDATE_CHART = 0;
    public static ArrayList<Double> avgSpeedHist = new ArrayList<>(), tripRateHist = new ArrayList<>();

    public static final EditableParameter[] SETTINGS_OBJECTS = {
        new EditableParameter(
            "Road Speed", 0,
            0.1, 1.0, 1.0,
            v -> ((Road) selectedNode).speed = v,
            () -> ((Road) selectedNode).speed
        ),
        new EditableParameter(
            "Traffic Light Offest", 0,
            0.0, 1.0, 0.0,
            v -> {
                TrafficLight sel = (TrafficLight) selectedNode;
                final int delta = (int) (v * sel.TICK_CYCLE) - sel.phase;

                for (TrafficLight tl: sel.intersection.trafficLights) {
                    tl.phase = ((tl.phase + delta) % tl.TICK_CYCLE + tl.TICK_CYCLE) % tl.TICK_CYCLE;
                    tl.render.setImage(TrafficLight.TEXTURES[tl.updateState()]);
                    tl.iterate();
                }
            },
            () -> (double) ((TrafficLight) selectedNode).phase / ((TrafficLight) selectedNode).TICK_CYCLE
        ),
        new EditableParameter(
            "Green Duration", 1,
            100, 1000, 450,
            v -> {
                TrafficLight sel = (TrafficLight) selectedNode;

                for (TrafficLight tl : sel.intersection.trafficLights) {
                    tl.GREEN_DURATION = (int) v;
                    tl.render.setImage(TrafficLight.TEXTURES[tl.updateState()]);
                    tl.sync();
                    tl.iterate();
                }
            },
            () -> ((TrafficLight) selectedNode).GREEN_DURATION
        ),
        new EditableParameter(
            "Yellow Duration", 2,
            10, 100, 50,
            v -> {
                TrafficLight sel = (TrafficLight) selectedNode;

                for (TrafficLight tl : sel.intersection.trafficLights) {
                    tl.YELLOW_DURATION = (int) v;
                    tl.render.setImage(TrafficLight.TEXTURES[tl.updateState()]);
                    tl.sync();
                    tl.iterate();
                }
            },
            () -> ((TrafficLight) selectedNode).YELLOW_DURATION
        ),
        new EditableParameter(
            "Vehicle Speed", 0,
            0.1, 2.0, 1.0,
            v -> ((Vehicle) selectedNode).speed = v,
            () -> ((Vehicle) selectedNode).speed
        )
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainAnchorPane = anchorPane;
        mainDisplayImageView = displayImageView;
        mainSettingsGridPane = settingsGridPane;
        mainSelectedAnchorPane = selectedAnchorPane;

        rect.widthProperty().bind(anchorPane.widthProperty());
        rect.heightProperty().bind(anchorPane.heightProperty());

        disableOnSimulate = new Node[] {
                disconnectSelBtn, deleteSelBtn, clearBtn, loadBtn, saveBtn,
                templateImg1, templateImg2, templateImg3, templateImg4, templateImg5
        };

        graphCB.getItems().addAll(graphStats);
        graphCB.getSelectionModel().selectFirst();

        rect.setOnMouseClicked(e -> {
            SelectHandler.deselect();
            SelectHandler.display();
        });

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

        templateImg3.setOnDragDetected(e -> {
            Dragboard db = templateImg3.startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putImage(templateImg3.getImage());
            content.putString("img3");
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

                        anchorPane.getChildren().addAll(bezierRoad.curves);
                        updateLayers();

                        e.setDropCompleted(true);
                        e.consume();
                        break;

                    case "img3":
                        final int RAD = 75;

                        Intersection p = new Intersection(new Point(e.getX() + RAD, e.getY()));
                        Intersection q = new Intersection(new Point(e.getX(), e.getY() + RAD));
                        Intersection r = new Intersection(new Point(e.getX() - RAD, e.getY()));
                        Intersection s = new Intersection(new Point(e.getX(), e.getY() - RAD));

                        final double[] ANGLES = {
                                Math.PI / 6, Math.PI / 3, 2 * Math.PI / 3, 5 * Math.PI / 6,
                                7 * Math.PI / 6, 4 * Math.PI / 3, 5 * Math.PI / 3, 11 * Math.PI / 6
                        };

                        final ArrayList<Circle> CIRCLES = new ArrayList<>();
                        for (int i = 0; i < 8; i++) CIRCLES.add(new Circle(
                                e.getX() + Math.cos(ANGLES[i]) * RAD * 6 / 5,
                                e.getY() + Math.sin(ANGLES[i]) * RAD * 6 / 5,
                                20
                        ));

                        BezierRoad b1 = new BezierRoad(p, CIRCLES.get(0), CIRCLES.get(1), q);
                        BezierRoad b2 = new BezierRoad(q, CIRCLES.get(2), CIRCLES.get(3), r);
                        BezierRoad b3 = new BezierRoad(r, CIRCLES.get(4), CIRCLES.get(5), s);
                        BezierRoad b4 = new BezierRoad(s, CIRCLES.get(6), CIRCLES.get(7), p);

                        for (Shape shape: b1.curves) {
                            if (!anchorPane.getChildren().contains(shape)) {
                                anchorPane.getChildren().add(shape);
                            }
                        }
                        for (Shape shape: b2.curves) {
                            if (!anchorPane.getChildren().contains(shape)) {
                                anchorPane.getChildren().add(shape);
                            }
                        }
                        for (Shape shape: b3.curves) {
                            if (!anchorPane.getChildren().contains(shape)) {
                                anchorPane.getChildren().add(shape);
                            }
                        }
                        for (Shape shape: b4.curves) {
                            if (!anchorPane.getChildren().contains(shape)) {
                                anchorPane.getChildren().add(shape);
                            }
                        }
                        updateLayers();

                        e.setDropCompleted(true);
                        e.consume();
                        break;

                    case "img4":
                        Intersection closestIntersect = Intersection.closestIntersection(50, new Point(e.getX(), e.getY()));
                        if (closestIntersect == null) {
                            alertError("Invalid Placement!", "Traffic lights can only be placed on intersections!", "Try dragging a traffic light onto an intersection!");
                        } else if (closestIntersect.adjList.size() <= 2) {
                            alertError("Invalid Placement!", "Traffic lights can only be placed on intersections of 3 or more roads!", "Try connecting more roads to your intersection!");
                        } else {
                            for (Road adj: closestIntersect.adjList.keySet()) new TrafficLight(adj, closestIntersect);
                            for (Road adj: closestIntersect.adjList.keySet()) {
                                adj.fwdObjects.sort(Comparator.naturalOrder());
                                adj.bckObjects.sort(Comparator.reverseOrder());
                            }
                            TrafficLight.sync(closestIntersect);
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
        rect.toBack();

        if (!menuTP.isExpanded()) menuAccordion.toFront();
        if (!selectedTP.isExpanded()) selectedAccordion.toFront();

        for (Intersection intersection: Intersection.intersectionList) intersection.borderCircleObj.toFront();
        for (Road road: Road.roadList) road.curves.get(2).toFront();
        for (Road road: Road.roadList) road.curves.get(3).toFront();
        for (Vehicle vehicle: Vehicle.vehicleList) vehicle.renderPane.toFront();
        for (TrafficLight trafficLight: TrafficLight.trafficLightList) trafficLight.renderPane.toFront();
        for (Intersection intersection: Destination.destinationList) intersection.destinationObj.render.toFront();
        draggableNodesToFront();

        if (!selectedHighlights.isEmpty() && selectedHighlights.getFirst() instanceof Polygon) {
            for (Shape s: selectedHighlights) {
                s.toFront();
            }
        }

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

            ticksSinceStart = 0;
            tripsMade = 0;

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

        } else if (selectedNode instanceof TrafficLight trafficLight) {
            while (!trafficLight.intersection.trafficLights.isEmpty()) {
                trafficLight.intersection.trafficLights.getFirst().delete();
            }

        } else if (selectedNode instanceof Vehicle vehicle) {
            vehicle.delete();
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

    public static void alertInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    public void saveGraph() {
        try {
            String filename = String.format("%s.tsim.txt", DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()));
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/savefiles/" + filename));
            out.writeObject(new Arrangement());
            out.close();

            IntersectionWrapper.processedIntersections.clear();
            RoadWrapper.processedRoads.clear();
            VehicleWrapper.processedVehicles.clear();
            TrafficLightWrapper.processedTrafficLights.clear();
            DestinationWrapper.processedDestinations.clear();
            
            alertInfo("File Created!", "Savefile successfully created!", String.format("Your savefile is saved under the name %s.", filename));
            
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
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Savefile");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/savefiles"));
            fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());

            if (selectedFile == null) {
                alertError("File Selection Error!", "No file selected!", "Try selecting a TrafficSimulator .txt save file!");
                return;
            }

            if (!selectedFile.getName().matches("\\d{8}-\\d{6}\\.tsim\\.txt")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Operation?");
                alert.setHeaderText("This savefile seems to have been renamed, and its contents could have been modified.");
                alert.setContentText("Are you sure you want to open this file? Doing so will erase all your current progress.");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.CANCEL) return;
            }

            clearGraph();

            ObjectInputStream input = new ObjectInputStream(new FileInputStream(selectedFile));
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
                } else if (vWrap instanceof TruckWrapper truckWrap) {
                    vehicle = new Truck(roadAt(truckWrap.road));
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
                TrafficLight tl = new TrafficLight(roadAt(tlWrap.road), intAt(tlWrap.intersection), tlWrap.phase);
                tl.iterate();
                tl.render.setImage(TrafficLight.TEXTURES[tl.updateState()]);
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

    @FXML
    public void highlightCongestion() {
        Road congestedRoad = null;
        double congestion = -1;

        for (Road road: Road.roadList) {
            if (road.getCongestion() > congestion) {
                congestion = road.congestion;
                congestedRoad = road;
            }
        }

        if (congestedRoad == null) {
            alertInfo("Ineffective Operation!", "There are no active roads!", "Try running the simulation and waiting for vehicles to spawn!");
            return;
        }

        SelectHandler.deselect();
        (MainController.selectedNode = congestedRoad).setSelect(true);
        congestedRoad.updateDrag();
        MainController.SETTINGS_OBJECTS[0].show();
        SelectHandler.display();
    }

    public void tick() {
        Platform.runLater(() -> {
            if (new File(System.getProperty("user.dir") + "/savefiles").mkdirs()) {
                alertInfo("Directory created!", "Directory \"savefiles\" generated!", "This will be the location where your savefiles read and write to.");
            }
        });

        try {
            while (true) {
                avgSpeed = 0;
                int count = 0;

                for (Vehicle vehicle: Vehicle.vehicleList) {
                    if (vehicle.currSpeed >= 0) {
                        avgSpeed += vehicle.currSpeed;
                        count++;
                    }
                }

                if (count == 0) avgSpeed = -1;
                else avgSpeed /= count;

                Platform.runLater(() -> statsTA.setText(String.format(
                    "Statistics of the Current Save File:\n\n\n" +
                        "Number of Vehicles:\n%d\n\n" +
                        "Number of Roads:\n%d\n\n" +
                        "Number of Intersections:\n%d\n\n" +
                        "Number of Destinations: \n%d\n\n" +
                        "Average Speed of Cars:\n%s pixels/s\n\n" +
                        "Traffic Flow Rate:\n%s trips/min",
                    Vehicle.vehicleList.size(),
                    Road.roadList.size(),
                    Intersection.intersectionList.size(),
                    Destination.destinationList.size(),
                    (avgSpeed < 0 ? "-" : String.format("%.2f", avgSpeed)),
                    (ticksSinceStart == 0 ? "-" : String.format("%.2f", (double) tripsMade * FPS / ticksSinceStart * 60))
                )));

                if (isSimulating) {
                    Platform.runLater(() -> {
                        if (random.nextInt(SPAWN_VEHICLE_RAND_NUM) == 0) {
                            Intersection start = Vehicle.generateTarget();
                            for (Road road: start.adjList.keySet()) {
                                boolean blocked = false;
                                ArrayList<RoadObject> roadObjs = (road.getStart() == start ? road.fwdObjects : road.bckObjects);

                                for (RoadObject ro: roadObjs) {
                                    if (ro instanceof Vehicle v) {
                                        if (road.getDistance(0, v.roadRelPos) <= 30 + v.MAXSIDE / 2) blocked = true;
                                        break;
                                    }
                                }

                                if (blocked) break;

                                if (random.nextInt(10) >= 3) {
                                    new Car(road, start);
                                } else {
                                    new Truck(road, start);
                                }
                            }
                        }

                        for (Road road: Road.roadList) road.iterate();
                        for (Vehicle vehicle: Vehicle.vehicleList) vehicle.iterate();
                        for (TrafficLight trafficLight: TrafficLight.trafficLightList) trafficLight.iterate();
                        updateLayers();
                        TrafficLight.globalTick = ++TrafficLight.globalTick;

                        ArrayList<Vehicle> remVehicles = new ArrayList<>();
                        for (Vehicle vehicle: Vehicle.vehicleList) if (!vehicle.deleted) remVehicles.add(vehicle);
                        Vehicle.vehicleList = remVehicles;
                    });

                    ticksSinceStart++;
                    if (++SINCE_LAST_UPDATE_CHART >= FPS / 2) {
                        SINCE_LAST_UPDATE_CHART = 0;
                        if (avgSpeed >= 0) avgSpeedHist.add(avgSpeed);
                        tripRateHist.add((double) tripsMade * FPS / ticksSinceStart * 60);

                        if (avgSpeedHist.size() > 20) avgSpeedHist.removeFirst();
                        if (tripRateHist.size() > 20) tripRateHist.removeFirst();

                        Platform.runLater(() -> {
                            lineChart.getData().clear();
                            XYChart.Series<String, Number> series = new XYChart.Series<>();
                            int ind = graphCB.getSelectionModel().getSelectedIndex();
                            StringBuilder str = new StringBuilder(String.format("Current %s: ", graphStats[ind]));

                            if (ind == 0) {
                                for (int i = 0; i < tripRateHist.size(); i++) {
                                    series.getData().add(new XYChart.Data<>(Integer.toString(i), tripRateHist.get(i)));
                                }
                                str.append(String.format("%.2f", tripRateHist.getLast()));

                            } else {
                                for (int i = 0; i < avgSpeedHist.size(); i++) {
                                    series.getData().add(new XYChart.Data<>(Integer.toString(i), avgSpeedHist.get(i)));
                                }
                                str.append(String.format("%.2f", avgSpeedHist.getLast()));
                            }

                            graphTA.setText(str.toString());
                            lineChart.getData().add(series);
                        });
                    }

                } else {
                    Platform.runLater(() -> {
                        deleteSelBtn.setDisable(selectedNode == null);
                        disconnectSelBtn.setDisable(!(selectedNode instanceof Road));

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
