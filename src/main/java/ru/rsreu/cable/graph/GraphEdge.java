package ru.rsreu.cable.graph;

public class GraphEdge {
    private final GraphNode firstNode;
    private final GraphNode secondNode;
    private final int distance;

    @Override
    public String toString() {
        return "GraphEdge{" +
                "firstNode=" + firstNode +
                ", secondNode=" + secondNode +
                ", distance=" + distance +
                '}';
    }

    public GraphEdge(GraphNode firstNode, GraphNode secondNode, int distance) {
        this.firstNode = firstNode;
        this.secondNode = secondNode;
        this.distance = distance;
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
