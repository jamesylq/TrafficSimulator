package com.example.trafficsimulator.Model;

import java.util.ArrayList;

public class VehicleWrapper extends RoadObjectWrapper {
    public double speed;

    public IntersectionWrapper target, prev, next;
    public static final ArrayList<VehicleWrapper> processedVehicles = new ArrayList<>();

    public VehicleWrapper(int i) {
        super(i);
    }

    public void load(Vehicle vehicle) {
        super.load(vehicle);
        speed = vehicle.speed;
        target = IntersectionWrapper.get(vehicle.target.index);
        prev = IntersectionWrapper.get(vehicle.prev.index);
        next = IntersectionWrapper.get(vehicle.next.index);
    }

    public static VehicleWrapper get(int i) {
        return processedVehicles.get(i);
    }
}
