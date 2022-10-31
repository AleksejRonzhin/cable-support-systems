package ru.rsreu.cable.graph;

import java.util.List;

public class GraphEdge {
    private final GraphNode firstNode;
    private final GraphNode secondNode;
    private final int distance;
    private final List<Coords> coords;

    @Override
    public String toString() {
        return "GraphEdge{" +
                "firstNode=" + firstNode +
                ", secondNode=" + secondNode +
                ", distance=" + distance +
                ", coords=" + coords +
                '}';
    }

    public GraphEdge(GraphNode firstNode, GraphNode secondNode, int distance, List<Coords> coords) {
        this.firstNode = firstNode;
        this.secondNode = secondNode;
        this.distance = distance;
        this.coords = coords;
    }

    public GraphNode getFirstNode() {
        return firstNode;
    }

    public GraphNode getSecondNode() {
        return secondNode;
    }

    public int getDistance() {
        return distance;
    }
}
