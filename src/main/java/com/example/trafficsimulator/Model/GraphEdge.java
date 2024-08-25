package com.example.trafficsimulator.Model;

public class GraphEdge {
    public double dist;
    public Road edge;
    public Intersection adj;

    public GraphEdge(double dist, Road edge, Intersection adj) {
        this.dist = dist;
        this.edge = edge;
        this.adj = adj;
    }

    @Override
    public String toString() {
        return Double.toString(this.dist);
    }
}
