package ru.rsreu.cable.utils;

import ru.rsreu.cable.graph.BuildingGraphCreator;
import ru.rsreu.cable.graph.Graph;
import ru.rsreu.cable.graph.models.GraphNode;
import ru.rsreu.cable.graph.models.GraphPath;
import ru.rsreu.cable.models.Building;

import java.util.List;
import java.util.Map;

import static ru.rsreu.cable.utils.BuildingUtils.*;

public class CableSupportSystemStepper {
    private final Building originalBuilding;
    private Building currentBuilding;

    public CableSupportSystemStepper(Building originalBuilding) {
        this.originalBuilding = originalBuilding;
    }

    public CableSupportSystemStepper addCablesFrame() {
        currentBuilding = copyBuilding(originalBuilding);
        setAllAvailableCables(currentBuilding);
        return this;
    }

    public CableSupportSystemStepper addFlyingCables() {
        Building copyBuilding = copyBuilding(currentBuilding);
        setFlyingCables(copyBuilding, currentBuilding);
        currentBuilding = copyBuilding;
        return this;
    }

    public CableSupportSystemStepper addNodes() {
        Building copyBuilding = copyBuilding(currentBuilding);
        setNodes(copyBuilding, currentBuilding);
        currentBuilding = copyBuilding;
        return this;
    }

    public Building getResultBuilding() {
        BuildingGraphCreator creator = new BuildingGraphCreator(currentBuilding);
        Graph graph = creator.create();
        Map<GraphNode, List<GraphPath>> consumerPaths = getConsumerPaths(graph);
        return getOptimalBuilding(originalBuilding, consumerPaths);
    }
}
