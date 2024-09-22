package com.example.trafficsimulator.Model;

import java.util.ArrayList;

public class DestinationWrapper extends Wrapper {
    public IntersectionWrapper intersection;
    public static final ArrayList<DestinationWrapper> processedDestinations = new ArrayList<>();

    public DestinationWrapper(int i) {
        super(i);
    }

    public void load(Destination dest) {
        intersection = IntersectionWrapper.get(dest.intersection.index);
    }

    public static DestinationWrapper get(int i) {
        return processedDestinations.get(i);
    }
}
