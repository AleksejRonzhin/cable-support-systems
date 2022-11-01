package ru.rsreu.cable.graph.models;

public class Coords {
    private final int i;
    private final int j;

    public Coords(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    @Override
    public String toString() {
        return "Coords{" +
                "i=" + i +
                ", j=" + j +
                '}';
    }
}
