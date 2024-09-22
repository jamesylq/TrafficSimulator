package com.example.trafficsimulator.Model;

import java.util.ArrayList;

public class TrafficLightWrapper extends RoadObjectWrapper {
    public int phase;
    public IntersectionWrapper intersection;

    public static final ArrayList<TrafficLightWrapper> processedTrafficLights = new ArrayList<>();

    public TrafficLightWrapper(int i) {
        super(i);
    }

    public void load(TrafficLight trafficLight) {
        super.load(trafficLight);
        phase = trafficLight.phase;
        intersection = IntersectionWrapper.get(trafficLight.intersection.index);
    }

    public static TrafficLightWrapper get(int i) {
        return processedTrafficLights.get(i);
    }
}
