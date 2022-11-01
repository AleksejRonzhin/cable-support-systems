package ru.rsreu.cable.graph;

import ru.rsreu.cable.graph.models.Coords;
import ru.rsreu.cable.graph.models.GraphEdge;
import ru.rsreu.cable.graph.models.GraphNode;
import ru.rsreu.cable.models.Building;
import ru.rsreu.cable.models.ElementType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        this.nodes = BuildingGraphUtils.getNodes(building);
    }

    private static List<GraphEdge> deleteExtraEdges(List<GraphEdge> edges) {
        List<GraphEdge> edgesWithoutRepeats = deleteRepeatEdges(edges);
        return deleteReverseEdges(edgesWithoutRepeats);
    }

    private static List<GraphEdge> deleteReverseEdges(List<GraphEdge> edges) {
        List<GraphEdge> newEdges = new ArrayList<>();
        for (GraphEdge graphEdge : edges) {
            Optional<GraphEdge> reverseEdge =
                    newEdges.stream().filter(newEdge -> newEdge.getFirstNode() == graphEdge.getSecondNode()
                            && newEdge.getSecondNode() == graphEdge.getFirstNode()).findFirst();
            if (reverseEdge.isPresent()) {
                if (reverseEdge.get().getDistance() > graphEdge.getDistance()) {
                    newEdges.remove(reverseEdge.get());
                    newEdges.add(graphEdge);
                }
            } else {
                newEdges.add(graphEdge);
            }
        }
        return newEdges;
    }

    private static List<GraphEdge> deleteRepeatEdges(List<GraphEdge> edges) {
        List<GraphEdge> newEdges = new ArrayList<>();
        for (GraphEdge graphEdge : edges) {
            Optional<GraphEdge> takenEdge = newEdges.stream().filter(newEdge -> newEdge.getFirstNode() == graphEdge.getFirstNode() && newEdge.getSecondNode() == graphEdge.getSecondNode()).findFirst();
            if (takenEdge.isPresent()) {
                if (takenEdge.get().getDistance() > graphEdge.getDistance()) {
                    newEdges.remove(takenEdge.get());
                    newEdges.add(graphEdge);
                }
            } else {
                newEdges.add(graphEdge);
            }
        }
        return newEdges;
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public void rec(GraphNode sourceNode, List<Coords> prev, int distance, int i, int j) {

        List<Coords> relatedCoords = getRelatedCoords(i, j);
        for (Coords coords : relatedCoords) {
            if ((prev.stream().anyMatch(prevCoords -> prevCoords.getI() == coords.getI() && prevCoords.getJ() == coords.getJ()))) {
                continue;
            }

            List<Coords> newPrev = new ArrayList<>(prev);
            newPrev.add(coords);

            ElementType element = elements[coords.getI()][coords.getJ()];
            if (element == ElementType.NODE || element == ElementType.CONSUMER || element == ElementType.SUPPLIER) {
                GraphNode foundNode = getByCoords(coords);
                GraphEdge newEdge = new GraphEdge(sourceNode, foundNode, distance + 1, newPrev);
                edges.add(newEdge);
            }
            if (element == ElementType.CABLE) {

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

    public List<GraphEdge> find() {
        for (GraphNode sourceNode : nodes) {
            List<Coords> prevCoords = new ArrayList<>();
            prevCoords.add(new Coords(sourceNode.getI(), sourceNode.getJ()));
            rec(sourceNode, prevCoords, 0, sourceNode.getI(), sourceNode.getJ());
        }
        return deleteExtraEdges(edges);
    }


}
