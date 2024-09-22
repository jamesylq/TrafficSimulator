package com.example.trafficsimulator.Model;

import java.util.ArrayList;

public class RoadWrapper extends Wrapper {
    public double speed;

    public IntersectionWrapper start, end;
    public ArrayList<RoadObjectWrapper> fwdObjects = new ArrayList<>(), bckObjects = new ArrayList<>();

    public static final ArrayList<RoadWrapper> processedRoads = new ArrayList<>();

    public RoadWrapper(int i) {
        super(i);
    }

    public void load(Road road) {
        speed = road.speed;
        start = IntersectionWrapper.get(road.start.index);
        end = IntersectionWrapper.get(road.end.index);

        for (RoadObject obj: road.fwdObjects) {
            if (obj instanceof Vehicle vehicle) {
                fwdObjects.add(VehicleWrapper.get(vehicle.index));
            } else if (obj instanceof TrafficLight trafficLight) {
                fwdObjects.add(TrafficLightWrapper.get(trafficLight.index));
            }
        }

        for (RoadObject obj: road.bckObjects) {
            if (obj instanceof Vehicle vehicle) {
                bckObjects.add(VehicleWrapper.get(vehicle.index));
            } else if (obj instanceof TrafficLight trafficLight) {
                bckObjects.add(TrafficLightWrapper.get(trafficLight.index));
            }
        }
    }

    public static RoadWrapper get(int i) {
        return processedRoads.get(i);
    }
}
