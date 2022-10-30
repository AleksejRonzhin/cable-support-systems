package ru.rsreu.cable.models;

public class Building {
    private final int height;
    private final int length;
    private final ElementType[][] elements;

    public Building(int height, int length, ElementType[][] elements) {
        this.length = length;
        this.height = height;
        this.elements = elements;

    }

    public int getCountCables() {
        int counter = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                if (elements[i][j] == ElementType.CABLE) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public ElementType[][] getElements() {
        return elements;
    }
}
