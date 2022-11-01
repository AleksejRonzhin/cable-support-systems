package ru.rsreu.cable.graph.models;

import java.util.List;

public class GraphPath {
    private final List<GraphEdge> edges;
    private final GraphNode consumer;
    private final GraphNode supplier;
    private final int distance;

    public GraphPath(List<GraphEdge> edges, GraphNode consumer, GraphNode supplier, int distance) {
        this.edges = edges;
        this.consumer = consumer;
        this.supplier = supplier;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "GraphPath{" + "consumer=" + consumer + ", supplier=" + supplier + ", distance=" + distance + '}';
    }

    public int getDistance() {
        return distance;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public GraphNode getConsumer() {
        return consumer;
    }

    public GraphNode getSupplier() {
        return supplier;
    }
}
