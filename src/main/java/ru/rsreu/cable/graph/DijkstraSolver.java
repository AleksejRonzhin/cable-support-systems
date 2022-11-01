package ru.rsreu.cable.graph;

import ru.rsreu.cable.graph.exceptions.NotConnectBetweenNodesException;
import ru.rsreu.cable.graph.models.GraphEdge;
import ru.rsreu.cable.graph.models.GraphNode;
import ru.rsreu.cable.graph.models.GraphPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DijkstraSolver {
    private final int[][] table;
    private final int[] distances;
    private final int[] visitedNodes;
    private final int size;
    private final List<GraphNode> nodes;
    private final List<GraphEdge> edges;

    public DijkstraSolver(List<GraphNode> nodes, List<GraphEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        this.size = nodes.size();
        this.table = createTable(nodes, edges);
        this.distances = new int[size];
        this.visitedNodes = new int[size];
    }

    private int[][] createTable(List<GraphNode> nodes, List<GraphEdge> edges) {
        int[][] table = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                table[i][j] = 0;
            }
        }
        for (GraphEdge edge : edges) {
            int i = nodes.indexOf(edge.getFirstNode());
            int j = nodes.indexOf(edge.getSecondNode());
            table[i][j] = edge.getDistance();
            table[j][i] = edge.getDistance();
        }
        return table;
    }

    public GraphPath solve(GraphNode beginNode, GraphNode targetNode) throws NotConnectBetweenNodesException {
        int beginIndex = nodes.indexOf(beginNode);
        int targetIndex = nodes.indexOf(targetNode);
        initStartValues(beginIndex);
        int minIndex, minValue;
        do {
            minIndex = Integer.MAX_VALUE;
            minValue = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                if ((visitedNodes[i] == 1) && (distances[i] < minValue)) {
                    minValue = distances[i];
                    minIndex = i;
                }
            }
            if (minIndex != Integer.MAX_VALUE) {
                for (int i = 0; i < size; i++) {
                    if (table[minIndex][i] > 0) {
                        int temp = minValue + table[minIndex][i];
                        if (temp < distances[i]) {
                            distances[i] = temp;
                        }
                    }
                }
                visitedNodes[minIndex] = 0;
            }
        } while (minIndex < Integer.MAX_VALUE);

        if(distances[nodes.indexOf(targetNode)] == Integer.MAX_VALUE){
            throw new NotConnectBetweenNodesException();
        }
        return getGraphPath(beginNode, targetNode);
    }

    private void initStartValues(int beginIndex) {
        for (int i = 0; i < size; i++) {
            distances[i] = Integer.MAX_VALUE;
            visitedNodes[i] = 1;
        }
        distances[beginIndex] = 0;
    }

    private GraphPath getGraphPath(GraphNode beginNode, GraphNode targetNode) {
        int beginIndex = nodes.indexOf(beginNode);
        int targetIndex = nodes.indexOf(targetNode);
        int[] pathNodesIndexes = new int[size];
        int currentIndex = targetIndex;
        pathNodesIndexes[0] = currentIndex;
        int indexCount = 1;
        int weight = distances[currentIndex];
        while (currentIndex != beginIndex) {
            for (int i = 0; i < size; i++) {
                if (table[i][currentIndex] != 0) {
                    int temp = weight - table[i][currentIndex];
                    if (temp == distances[i]) {
                        weight = temp;
                        currentIndex = i;
                        pathNodesIndexes[indexCount] = i;
                        indexCount++;
                    }
                }
            }
        }
        List<GraphEdge> pathEdges = getPathEdges(pathNodesIndexes, indexCount);
        return new GraphPath(pathEdges, beginNode, targetNode, distances[targetIndex]);
    }

    private List<GraphEdge> getPathEdges(int[] pathNodesIndexes, int indexesCount) {
        List<GraphEdge> pathEdges = new ArrayList<>();
        for (int i = 1; i < indexesCount; i++) {
            int firstIndex = pathNodesIndexes[i - 1];
            int secondIndex = pathNodesIndexes[i];
            GraphNode firstNode = nodes.get(firstIndex);
            GraphNode secondNode = nodes.get(secondIndex);
            GraphEdge edge = getEdge(firstNode, secondNode);
            pathEdges.add(edge);
        }
        return pathEdges;
    }

    private GraphEdge getEdge(GraphNode firstNode, GraphNode secondNode) {
        return edges.stream().filter(edge -> edge.getFirstNode() == firstNode && edge.getSecondNode() == secondNode || edge.getFirstNode() == secondNode && edge.getSecondNode() == firstNode).findFirst().orElseThrow(RuntimeException::new);
    }
}
