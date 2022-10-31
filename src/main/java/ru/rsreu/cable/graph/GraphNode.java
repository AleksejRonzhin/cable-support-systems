package ru.rsreu.cable.graph;

import ru.rsreu.cable.models.ElementType;

public class GraphNode {
    private final int i;
    private final int j;
    private final ElementType type;
    public GraphNode(int i, int j, ElementType type) {
        this.i = i;
        this.j = j;
        this.type = type;
    }

    @Override
    public String toString() {
        return "GraphNode{" + "i=" + i + ", j=" + j + ", type=" + type + '}';
    }

    public ElementType getType() {
        return type;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}
