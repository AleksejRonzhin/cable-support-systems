package ru.rsreu.cable.graph;

import ru.rsreu.cable.graph.models.GraphEdge;
import ru.rsreu.cable.graph.models.GraphNode;

import java.util.List;

public class Graph {
    private final List<GraphEdge> edges;
    private final List<GraphNode> nodes;

    public Graph(List<GraphNode> nodes, List<GraphEdge> edges) {
        this.edges = edges;
        this.nodes = nodes;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }
}
