package ru.rsreu.cable.models;

public enum ElementType {
    SPACE('O'),
    WALL('X'),
    PIPE('T'),
    SUPPLIER('E'),
    CONSUMER('C'),
    CABLE('S'),
    NODE('N');

    private final char symbol;

    ElementType(char symbol) {
        this.symbol = symbol;
    }

    public static ElementType getBySymbol(char symbol) {
        for (ElementType type : values()) {
            if (type.symbol == symbol) {
                return type;
            }
        }
        return SPACE;
    }

    public char getSymbol() {
        return symbol;
    }
}