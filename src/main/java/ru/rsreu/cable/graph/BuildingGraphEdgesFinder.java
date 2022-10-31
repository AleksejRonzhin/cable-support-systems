package ru.rsreu.cable.graph;

import ru.rsreu.cable.models.Building;
import ru.rsreu.cable.models.ElementType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.rsreu.cable.graph.BuildingGraphUtils.getNodes;

public class BuildingGraphEdgesFinder {
    private final List<GraphEdge> edges;
    private final ElementType[][] elements;
    private final int height;
    private final int length;
    private final List<GraphNode> nodes;

    public BuildingGraphEdgesFinder(Building building) {
        this.edges = new ArrayList<>();
        this.elements = building.getElements();
        this.height = building.getHeight();
        this.length = building.getLength();
        this.nodes = getNodes(building);
        System.out.println("nodes =" + nodes.size());
    }

    public List<GraphEdge> find() {
        for (GraphNode sourceNode : nodes) {
            List<Coords> prevCoords = new ArrayList<>();
            prevCoords.add(new Coords(sourceNode.getI(), sourceNode.getJ()));
            rec(sourceNode, prevCoords, 0, sourceNode.getI(), sourceNode.getJ());
        }
        return edges;
    }

    public void rec(GraphNode sourceNode, List<Coords> prev, int distance, int i, int j) {

        List<Coords> relatedCoords = getRelatedCoords(i, j);
        for (Coords coords : relatedCoords) {
            if ((prev.stream().anyMatch(prevCoords -> prevCoords.getI() == coords.getI() && prevCoords.getJ() == coords.getJ()))) {
                continue;
            }

            ElementType element = elements[coords.getI()][coords.getJ()];
            if (element == ElementType.NODE || element == ElementType.CONSUMER || element == ElementType.SUPPLIER) {
                GraphNode foundNode = getByCoords(coords);
                GraphEdge newEdge = new GraphEdge(sourceNode, foundNode, distance + 1);
                edges.add(newEdge);
            }
            if (element == ElementType.CABLE) {
                List<Coords> newPrev = new ArrayList<>(prev);
                newPrev.add(coords);
                rec(sourceNode, newPrev, distance + 1, coords.getI(), coords.getJ());
            }
        }
    }

    private GraphNode getByCoords(Coords coords) {
        return nodes.stream().filter(node -> node.getI() == coords.getI() && node.getJ() == coords.getJ()).findFirst().orElseThrow(RuntimeException::new);
    }

    private List<Coords> getRelatedCoords(int i, int j) {
        List<Coords> relatedElements = new ArrayList<>();
        if (i - 1 >= 0) {
            relatedElements.add(new Coords(i - 1, j));
        }
        if (i + 1 < height) {
            relatedElements.add(new Coords(i + 1, j));
        }
        if (j - 1 >= 0) {
            relatedElements.add(new Coords(i, j - 1));
        }
        if (j + 1 < length) {
            relatedElements.add(new Coords(i, j + 1));
        }
        return relatedElements;
    }
}
