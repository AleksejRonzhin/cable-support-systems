package ru.rsreu.cable.utils;

import ru.rsreu.cable.graph.BuildingGraphEdgesFinder;
import ru.rsreu.cable.graph.GraphEdge;
import ru.rsreu.cable.models.Building;
import ru.rsreu.cable.models.ElementType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static Building getResult(Building building) {
        List<GraphEdge> edges = new BuildingGraphEdgesFinder(building).find();
        System.out.println("edges count =" + edges.size());
        edges.forEach(System.out::println);
        return building;
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
