package com.example.trafficsimulator.Model;

import java.io.Serializable;
import java.util.*;

public class Arrangement implements Serializable {
    public ArrayList<Vehicle> vehicleList;
    public ArrayList<Road> roadList;
    public ArrayList<TrafficLight> trafficLightlist;
    public ArrayList<Intersection> destinationList, intersectionList;

    public Arrangement(ArrayList<Vehicle> vehicleList, ArrayList<Road> roadList, ArrayList<TrafficLight> trafficLightlist, ArrayList<Intersection> destinationList, ArrayList<Intersection> intersectionList) {
        this.vehicleList = vehicleList;
        this.roadList = roadList;
        this.trafficLightlist = trafficLightlist;
        this.destinationList = destinationList;
        this.intersectionList = intersectionList;
    }
}
