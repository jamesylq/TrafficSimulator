package com.example.trafficsimulator.Model;

import java.util.*;

public class IntersectionWrapper extends Wrapper {
    public Point point;
    public DestinationWrapper destinationObj;
    public HashMap<RoadWrapper, IntersectionWrapper> adjList = new HashMap<>();
    public ArrayList<TrafficLightWrapper> trafficLights = new ArrayList<>();

    public static final ArrayList<IntersectionWrapper> processedIntersections = new ArrayList<>();

    public IntersectionWrapper(int i) {
        super(i);
    }

    public void load(Intersection intersection) {
        point = intersection.getPoint();
        assert(point != null);
        if (intersection.destinationObj == null) destinationObj = null;
        else destinationObj = DestinationWrapper.processedDestinations.get(intersection.destinationObj.index);

        for (Map.Entry<Road, Intersection> adj: intersection.adjList.entrySet()) {
            adjList.put(
                RoadWrapper.get(adj.getKey().index),
                get(adj.getValue().index)
            );
        }

        for (TrafficLight trafficLight: intersection.trafficLights) {
            trafficLights.add(TrafficLightWrapper.get(trafficLight.index));
        }
    }

    public static IntersectionWrapper get(int i) {
        return processedIntersections.get(i);
    }
}
