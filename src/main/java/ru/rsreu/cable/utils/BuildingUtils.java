package ru.rsreu.cable.utils;

import ru.rsreu.cable.graph.BuildingGraphEdgesFinder;
import ru.rsreu.cable.graph.DijkstraSolver;
import ru.rsreu.cable.graph.exceptions.NotConnectBetweenNodesException;
import ru.rsreu.cable.graph.models.Coords;
import ru.rsreu.cable.graph.models.GraphEdge;
import ru.rsreu.cable.graph.models.GraphNode;
import ru.rsreu.cable.graph.models.GraphPath;
import ru.rsreu.cable.models.Building;
import ru.rsreu.cable.models.ElementType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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

    public static Building addCablesFrame(Building building) {
        ElementType[][] copyElements = Arrays.stream(building.getElements()).map(ElementType[]::clone).toArray(ElementType[][]::new);
        setAllAvailableCables(building.getHeight(), building.getLength(), copyElements);
        return new Building(building.getHeight(), building.getLength(), copyElements);
    }

    private static void setAllAvailableCables(int height, int length, ElementType[][] elements) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                ElementType element = elements[i][j];
                if (element == ElementType.WALL) {
                    setCablesAround(height, length, elements, i, j);
                }
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                ElementType element = elements[i][j];
                if (element == ElementType.PIPE) {
                    deleteCablesAround(height, length, elements, i, j);
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

    public static Building addFlyingCables(Building building) {
        ElementType[][] copyElements = Arrays.stream(building.getElements()).map(ElementType[]::clone).toArray(ElementType[][]::new);

        setFlyingCables(building.getHeight(), building.getLength(), copyElements, building.getElements());
        return new Building(building.getHeight(), building.getLength(), copyElements);
    }

    private static void setFlyingCables(int height, int length, ElementType[][] copyElements, ElementType[][] originalElements) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                ElementType element = copyElements[i][j];
                if (element == ElementType.SPACE) {
                    trySetFlyingCable(height, length, copyElements, originalElements, i, j);
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

    public static Building addNodes(Building building) {
        ElementType[][] copyElements = Arrays.stream(building.getElements()).map(ElementType[]::clone).toArray(ElementType[][]::new);

        setNodes(building.getHeight(), building.getLength(), copyElements, building.getElements());
        return new Building(building.getHeight(), building.getLength(), copyElements);
    }

    private static void setNodes(int height, int length, ElementType[][] elements, ElementType[][] originalElements) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                if (elements[i][j] == ElementType.CABLE) {
                    trySetNode(height, length, elements, originalElements, i, j);
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

    public static Building getResultBuilding(Building buildingWithNodes, Building sourceBuilding) {
        BuildingGraphEdgesFinder finder = new BuildingGraphEdgesFinder(buildingWithNodes);
        List<GraphEdge> edges = finder.find();
        List<GraphNode> nodes = finder.getNodes();
        List<GraphNode> consumers = getConsumers(nodes);
        List<GraphNode> suppliers = getSuppliers(nodes);


        List<GraphPath> takenPaths = new ArrayList<>();
        for(GraphNode consumer: consumers){
            List<GraphPath> paths = new ArrayList<>();
            for(GraphNode supplier: suppliers){
                List<GraphNode> nodes2 = new ArrayList<>(nodes);
                List<GraphEdge> edges2 = new ArrayList<>(edges);
                for(GraphNode consumer2: consumers){
                    if(consumer2 != consumer){
                        nodes2.remove(consumer2);
                        for(GraphEdge edge: edges){
                            if(edge.getFirstNode() == consumer2 || edge.getSecondNode() == consumer2){
                                edges2.remove(edge);
                            }
                        }
                    }

                }
                for(GraphNode supplier2: suppliers){
                    if(supplier2 != supplier){
                        nodes2.remove(supplier2);
                        for(GraphEdge edge: edges){
                            if(edge.getFirstNode() == supplier2 || edge.getSecondNode() == supplier2){
                                edges2.remove(edge);
                            }
                        }
                    }
                }

                boolean flagConsumer = true;
                boolean flagSupplier = true;
                for(GraphEdge edge: edges2){
                    if (edge.getFirstNode() == consumer || edge.getSecondNode() == consumer) {
                        flagConsumer = false;
                    }
                    if (edge.getFirstNode() == supplier || edge.getSecondNode() == supplier) {
                        flagSupplier = false;
                    }
                }
                if(flagConsumer){
                    System.out.println("CONSUMER " + consumer + " have not connect");
                    continue;
                }
                if(flagSupplier){
                    System.out.println("SUPPLIER " + supplier + " have not connect");
                    continue;
                }

                try{

                    DijkstraSolver solver = new DijkstraSolver(nodes2, edges2);
                    GraphPath path = solver.solve(consumer, supplier);
                    paths.add(path);
                } catch (NotConnectBetweenNodesException e) {
                    System.out.println("SUPPLIER " + supplier + " AND CONSUMER" + consumer + " have not connect");
                }
            }
            if(paths.isEmpty()){
                continue;
            }
            takenPaths.add(paths.stream().min(Comparator.comparingInt(GraphPath::getDistance)).get());
            paths.forEach(System.out::println);
        }

        ElementType[][] copyElements = Arrays.stream(sourceBuilding.getElements()).map(ElementType[]::clone).toArray(ElementType[][]::new);
        Building copySourceBuilding = new Building(sourceBuilding.getHeight(), sourceBuilding.getLength(), copyElements);
        return putPaths(copySourceBuilding, takenPaths);
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

    public static List<ElementType> getCheckingElements(int height, int length, ElementType[][] originalElements, int i, int j) {
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
}
