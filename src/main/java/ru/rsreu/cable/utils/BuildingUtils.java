package ru.rsreu.cable.utils;

import ru.rsreu.cable.graph.DijkstraSolver;
import ru.rsreu.cable.graph.Graph;
import ru.rsreu.cable.graph.exceptions.NotConnectBetweenNodesException;
import ru.rsreu.cable.graph.models.Coords;
import ru.rsreu.cable.graph.models.GraphEdge;
import ru.rsreu.cable.graph.models.GraphNode;
import ru.rsreu.cable.graph.models.GraphPath;
import ru.rsreu.cable.models.Building;
import ru.rsreu.cable.models.ElementType;

import java.util.*;
import java.util.stream.Collectors;

public class BuildingUtils {
    public static Building createBuilding(int height, int length, char[][] symbols) {
        ElementType[][] elements = new ElementType[height][length];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                elements[i][j] = ElementType.getBySymbol(symbols[i][j]);
            }
        }
        return new Building(height, length, elements);
    }

    public static char[][] printLikeSymbols(Building building) {
        int length = building.getLength();
        int height = building.getHeight();
        ElementType[][] elementTypes = building.getElements();

        char[][] symbols = new char[height][length];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                symbols[i][j] = elementTypes[i][j].getSymbol();
            }
        }
        return symbols;
    }

    public static String print(Building building) {
        return building.getCountCables() + "\n" + Arrays.stream(building.getElements()).map(elementRow -> Arrays.stream(elementRow).map(element -> String.valueOf(element.getSymbol())).collect(Collectors.joining(" "))).collect(Collectors.joining("\n"));
    }

    public static void setAllAvailableCables(Building building) {
        for (int i = 0; i < building.getHeight(); i++) {
            for (int j = 0; j < building.getLength(); j++) {
                ElementType element = building.getElements()[i][j];
                if (element == ElementType.WALL) {
                    setCablesAround(building.getHeight(), building.getLength(), building.getElements(), i, j);
                }
            }
        }
        for (int i = 0; i < building.getHeight(); i++) {
            for (int j = 0; j < building.getLength(); j++) {
                ElementType element = building.getElements()[i][j];
                if (element == ElementType.PIPE) {
                    deleteCablesAround(building.getHeight(), building.getLength(), building.getElements(), i, j);
                }
            }
        }
    }

    private static void deleteCablesAround(int height, int length, ElementType[][] elements, int i, int j) {
        for (int k = i - 1; k <= i + 1; k++) {
            for (int m = j - 1; m <= j + 1; m++) {
                if (k >= 0 && k < height) {
                    if (m >= 0 && m < length) {
                        ElementType element = elements[k][m];
                        if (element == ElementType.CABLE) {
                            elements[k][m] = ElementType.SPACE;
                        }
                    }
                }
            }
        }
    }

    private static void setCablesAround(int height, int length, ElementType[][] elements, int i, int j) {
        for (int k = i - 1; k <= i + 1; k++) {
            for (int m = j - 1; m <= j + 1; m++) {
                if (k >= 0 && k < height) {
                    if (m >= 0 && m < length) {
                        ElementType element = elements[k][m];
                        if (element == ElementType.SPACE) {
                            elements[k][m] = ElementType.CABLE;
                        }
                    }
                }
            }
        }
    }


    public static void setFlyingCables(Building building, Building originalBuilding) {
        for (int i = 0; i < building.getHeight(); i++) {
            for (int j = 0; j < building.getLength(); j++) {
                ElementType element = building.getElements()[i][j];
                if (element == ElementType.SPACE) {
                    trySetFlyingCable(building.getHeight(), building.getLength(), building.getElements(), originalBuilding.getElements(), i, j);
                }
            }
        }
    }

    private static void trySetFlyingCable(int height, int length, ElementType[][] elements, ElementType[][] originalElements, int i, int j) {
        int counter = 0;
        for (ElementType checkElement : getCheckingElements(height, length, originalElements, i, j)) {
            if (checkElement == ElementType.PIPE) return;
            if (checkElement == ElementType.CABLE || checkElement == ElementType.SUPPLIER || checkElement == ElementType.CONSUMER)
                counter++;

        }
        if (counter > 1) {
            elements[i][j] = ElementType.CABLE;
        }
    }

    public static void setNodes(Building building, Building originalBuilding) {
        for (int i = 0; i < building.getHeight(); i++) {
            for (int j = 0; j < building.getLength(); j++) {
                if (building.getElements()[i][j] == ElementType.CABLE) {
                    trySetNode(building.getHeight(), building.getLength(), building.getElements(), originalBuilding.getElements(), i, j);
                }
            }
        }
    }

    private static void trySetNode(int height, int length, ElementType[][] elements, ElementType[][] originalElements, int i, int j) {
        int counter = 0;
        for (ElementType checkElement : getCheckingElements(height, length, originalElements, i, j)) {
            if (checkElement == ElementType.CABLE || checkElement == ElementType.SUPPLIER || checkElement == ElementType.CONSUMER)
                counter++;
        }
        if (counter > 2) {
            elements[i][j] = ElementType.NODE;
        }
    }

    public static Map<GraphNode, List<GraphPath>> getConsumerPaths(Graph graph) {
        List<GraphNode> consumers = getConsumers(graph.getNodes());
        List<GraphNode> suppliers = getSuppliers(graph.getNodes());
        Map<GraphNode, List<GraphPath>> consumerPaths = new HashMap<>();
        for (GraphNode consumer : consumers) {
            List<GraphPath> paths = new ArrayList<>();
            for (GraphNode supplier : suppliers) {
                Graph subgraph = getSubgraphForConsumerAndSupplier(consumer, supplier, graph);
                if (!pathIsAvailable(consumer, supplier, subgraph.getEdges())) continue;
                try {
                    DijkstraSolver solver = new DijkstraSolver(subgraph.getNodes(), subgraph.getEdges());
                    GraphPath path = solver.solve(consumer, supplier);
                    paths.add(path);
                } catch (NotConnectBetweenNodesException ignored) {
                }
            }
            if (paths.isEmpty()) {
                continue;
            }
            consumerPaths.put(consumer, paths);
        }
        return consumerPaths;
    }

    public static Building getOptimalBuilding(Building originalBuilding, Map<GraphNode, List<GraphPath>> consumerPaths) {
        List<GraphPath> takenPaths = new ArrayList<>();
        consumerPaths.forEach((v, k) -> takenPaths.add(k.get(0)));
        Building copyBuilding = copyBuilding(originalBuilding);
        return putPaths(copyBuilding, takenPaths);
    }

    private static boolean pathIsAvailable(GraphNode consumer, GraphNode supplier, List<GraphEdge> edges) {
        boolean flagConsumer = false;
        boolean flagSupplier = false;
        for (GraphEdge edge : edges) {
            if (flagConsumer || edge.getFirstNode() == consumer || edge.getSecondNode() == consumer) {
                flagConsumer = true;
            }
            if (flagSupplier || edge.getFirstNode() == supplier || edge.getSecondNode() == supplier) {
                flagSupplier = true;
            }
        }
        return flagSupplier && flagConsumer;
    }

    private static Graph getSubgraphForConsumerAndSupplier(GraphNode consumer, GraphNode supplier, Graph graph) {
        List<GraphNode> newNodes = new ArrayList<>(graph.getNodes());
        List<GraphEdge> newEdges = new ArrayList<>(graph.getEdges());
        for (GraphNode node : graph.getNodes()) {
            if (node.getType() == ElementType.CONSUMER) {
                if (node != consumer) {
                    newNodes.remove(node);
                    for (GraphEdge edge : graph.getEdges()) {
                        if (edge.getFirstNode() == node || edge.getSecondNode() == node) {
                            newEdges.remove(edge);
                        }
                    }
                }
            }
            if (node.getType() == ElementType.SUPPLIER) {
                if (node != supplier) {
                    newNodes.remove(node);
                    for (GraphEdge edge : graph.getEdges()) {
                        if (edge.getFirstNode() == node || edge.getSecondNode() == node) {
                            newEdges.remove(edge);
                        }
                    }
                }
            }
        }
        return new Graph(newNodes, newEdges);
    }

    private static Building putPaths(Building building, List<GraphPath> paths) {
        ElementType[][] elements = building.getElements();
        for (GraphPath path : paths) {
            for (GraphEdge edge : path.getEdges()) {
                for (Coords coords : edge.getCoords()) {
                    elements[coords.getI()][coords.getJ()] = ElementType.CABLE;
                }
            }
            GraphNode consumer = path.getConsumer();
            GraphNode supplier = path.getSupplier();
            elements[consumer.getI()][consumer.getJ()] = ElementType.CONSUMER;
            elements[supplier.getI()][supplier.getJ()] = ElementType.SUPPLIER;
        }
        return building;
    }

    private static List<GraphNode> getSuppliers(List<GraphNode> nodes) {
        return getNodesByType(nodes, ElementType.SUPPLIER);
    }

    private static List<GraphNode> getConsumers(List<GraphNode> nodes) {
        return getNodesByType(nodes, ElementType.CONSUMER);
    }

    private static List<GraphNode> getNodesByType(List<GraphNode> nodes, ElementType type) {
        return nodes.stream().filter(node -> node.getType() == type).collect(Collectors.toList());

    }

    private static List<ElementType> getCheckingElements(int height, int length, ElementType[][] originalElements, int i, int j) {
        List<ElementType> checkElements = new ArrayList<>();
        if (i - 1 >= 0) {
            checkElements.add(originalElements[i - 1][j]);
        }
        if (i + 1 < height) {
            checkElements.add(originalElements[i + 1][j]);
        }
        if (j - 1 >= 0) {
            checkElements.add(originalElements[i][j - 1]);
        }
        if (j + 1 < length) {
            checkElements.add(originalElements[i][j + 1]);
        }
        return checkElements;
    }

    public static Building copyBuilding(Building building) {
        ElementType[][] copyElements = Arrays.stream(building.getElements()).map(ElementType[]::clone).toArray(ElementType[][]::new);
        return new Building(building.getHeight(), building.getLength(), copyElements);
    }
}
