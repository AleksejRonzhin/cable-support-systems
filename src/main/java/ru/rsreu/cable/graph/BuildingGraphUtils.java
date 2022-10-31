package ru.rsreu.cable.graph;

import ru.rsreu.cable.models.Building;
import ru.rsreu.cable.models.ElementType;

import java.util.ArrayList;
import java.util.List;

public class BuildingGraphUtils {

    public static List<GraphEdge> getGraphEdges(Building building, List<GraphNode> graphNodes) {
        List<GraphEdge> graphEdges = new ArrayList<>();
        for(GraphNode sourceNode: graphNodes){

        }
        return graphEdges;
    }

    public static List<GraphNode> getNodes(Building building) {
        List<GraphNode> graphNodes = new ArrayList<>();
        for (int i = 0; i < building.getHeight(); i++) {
            for (int j = 0; j < building.getLength(); j++) {
                ElementType element = building.getElements()[i][j];
                if (element == ElementType.NODE || element == ElementType.CONSUMER || element == ElementType.SUPPLIER) {
                    GraphNode graphNode = new GraphNode(i, j, element);
                    graphNodes.add(graphNode);
                }
            }
        }
        return graphNodes;
    }
}
