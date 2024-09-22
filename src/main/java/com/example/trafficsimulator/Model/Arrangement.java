package com.example.trafficsimulator.Model;

import java.io.Serializable;
import java.util.*;

public class Arrangement implements Serializable {
    public ArrayList<RoadWrapper> roadList = RoadWrapper.processedRoads;
    public ArrayList<TrafficLightWrapper> trafficLightList = TrafficLightWrapper.processedTrafficLights;
    public ArrayList<VehicleWrapper> vehicleList = VehicleWrapper.processedVehicles;
    public ArrayList<IntersectionWrapper> intersectionList = IntersectionWrapper.processedIntersections;
    public ArrayList<DestinationWrapper> destinationList = DestinationWrapper.processedDestinations;

    public Arrangement() {
        roadList.clear();
        trafficLightList.clear();
        vehicleList.clear();
        intersectionList.clear();
        destinationList.clear();

        Road road;
        Vehicle vehicle;

        for (int i = 0; i < Road.roadList.size(); i++) {
            (road = Road.roadList.get(i)).index = i;
            if (road instanceof LinearRoad) {
                RoadWrapper.processedRoads.add(new LinearRoadWrapper(i));
            } else if (road instanceof BezierRoad) {
                RoadWrapper.processedRoads.add(new BezierRoadWrapper(i));
            }
        }
        for (int i = 0; i < Intersection.intersectionList.size(); i++) {
            Intersection.intersectionList.get(i).index = i;
            IntersectionWrapper.processedIntersections.add(new IntersectionWrapper(i));
        }
        for (int i = 0; i < TrafficLight.trafficLightList.size(); i++) {
            TrafficLight.trafficLightList.get(i).index = i;
            TrafficLightWrapper.processedTrafficLights.add(new TrafficLightWrapper(i));
        }
        for (int i = 0; i < Vehicle.vehicleList.size(); i++) {
            (vehicle = Vehicle.vehicleList.get(i)).index = i;
            if (vehicle instanceof Car) {
                VehicleWrapper.processedVehicles.add(new CarWrapper(i));
            } else if (vehicle instanceof Truck) {
                VehicleWrapper.processedVehicles.add(new TruckWrapper(i));
            }
        }
        for (int i = 0; i < Destination.destinationList.size(); i++) {
            Destination.destinationList.get(i).destinationObj.index = i;
            DestinationWrapper.processedDestinations.add(new DestinationWrapper(i));
        }

        for (int i = 0; i < Road.roadList.size(); i++) {
            road = Road.roadList.get(i);
            if (road instanceof LinearRoad linear) {
                ((LinearRoadWrapper) (RoadWrapper.get(i))).load(linear);
            } else if (road instanceof BezierRoad bezier) {
                ((BezierRoadWrapper) (RoadWrapper.get(i))).load(bezier);
            }
        }
        for (int i = 0; i < Intersection.intersectionList.size(); i++) {
            IntersectionWrapper.get(i).load(Intersection.intersectionList.get(i));
        }
        for (int i = 0; i < TrafficLight.trafficLightList.size(); i++) {
            TrafficLightWrapper.get(i).load(TrafficLight.trafficLightList.get(i));
        }
        for (int i = 0; i < Vehicle.vehicleList.size(); i++) {
            vehicle = Vehicle.vehicleList.get(i);
            if (vehicle instanceof Car car) {
                ((CarWrapper) (VehicleWrapper.get(i))).load(car);
            } else if (vehicle instanceof Truck truck) {
                ((TruckWrapper) (VehicleWrapper.get(i))).load(truck);
            }
        }
        for (int i = 0; i < Destination.destinationList.size(); i++) {
            DestinationWrapper.get(i).load(Destination.destinationList.get(i).destinationObj);
        }
    }
}
