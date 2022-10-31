package ru.rsreu.cable.services;

import org.springframework.stereotype.Service;
import ru.rsreu.cable.exceptions.IncorrectFileException;
import ru.rsreu.cable.models.Building;
import ru.rsreu.cable.utils.BuildingUtils;

import java.util.List;

import static ru.rsreu.cable.utils.BuildingUtils.*;

@Service
public class CableSupportSystemService {
    public Building createBuilding(List<String> args) throws IncorrectFileException {
        String firstLine = args.get(0);
        String[] substrings = firstLine.split(" ");
        if(substrings.length != 2){
            throw new IncorrectFileException("Отсуствует значение параметра высота и (или) длина");
        }
        try {
            int height = Integer.parseInt(substrings[1]);
            int length = Integer.parseInt(substrings[0]);
            char[][] symbols = getSymbols(args, height, length);
            return BuildingUtils.createBuilding(height, length, symbols);
        } catch (NumberFormatException exception){
            throw new IncorrectFileException("Задано нецелочисленное значение для параметра высота и (или) длина.");
        }
    }

    private static char[][] getSymbols(List<String> args, int height, int length) throws IncorrectFileException {
        if(args.size() != height + 1){
            throw new IncorrectFileException("Высота исходной таблицы не соответствует заданному значению.");
        }
        char[][] symbols = new char[height][length];
        for (int i = 0; i < height; i++) {
            String line = args.get(i + 1);
            String[] lineSymbols = line.split(" ");
            if(lineSymbols.length != length){
                throw new IncorrectFileException(String.format("Длина %d строки исходной таблицы не соответствует заданному значению.", i + 1));
            }
            for (int j = 0; j < length; j++) {
                symbols[i][j] = lineSymbols[j].charAt(0);
            }
        }
        return symbols;
    }

    public Building buildCableSupportSystem(Building sourceBuilding) {
        Building building = addCablesFrame(sourceBuilding);
        building = addFlyingCables(building);
        building = addNodes(building);
        building = getResult(building);
        return building;
    }
}