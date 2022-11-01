package ru.rsreu.cable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.rsreu.cable.models.Building;
import ru.rsreu.cable.utils.BuildingUtils;
import ru.rsreu.cable.utils.CableSupportSystemStepper;

class CableSupportSystemTest {
    @Test
    public void test1() {
        int height = 5;
        int length = 6;
        char[][] symbols = {
                {'X', 'X', 'X', 'X', 'X', 'X'},
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'C', 'O', 'O', 'O', 'E', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'},};

        Building building = BuildingUtils.createBuilding(height, length, symbols);
        CableSupportSystemStepper stepper = new CableSupportSystemStepper(building);
        Building result = stepper.addCablesFrame().addFlyingCables().addNodes().getResultBuilding();
        Assertions.assertEquals(5, result.getCablesCount());
    }

    @Test
    public void test2() {
        int height = 5;
        int length = 6;
        char[][] symbols = {
                {'X', 'X', 'X', 'X', 'X', 'X'},
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'C', 'O', 'O', 'O', 'E', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'C', 'O'},};

        Building building = BuildingUtils.createBuilding(height, length, symbols);
        CableSupportSystemStepper stepper = new CableSupportSystemStepper(building);
        Building result = stepper.addCablesFrame().addFlyingCables().addNodes().getResultBuilding();
        Assertions.assertEquals(6, result.getCablesCount());
    }

    @Test
    public void test3() {
        int height = 5;
        int length = 6;
        char[][] symbols = {
                {'X', 'X', 'X', 'X', 'X', 'X'},
                {'O', 'O', 'X', 'O', 'O', 'O'},
                {'C', 'O', 'X', 'O', 'E', 'O'},
                {'O', 'O', 'X', 'O', 'O', 'O'},
                {'O', 'O', 'X', 'O', 'O', 'O'},};

        Building building = BuildingUtils.createBuilding(height, length, symbols);
        CableSupportSystemStepper stepper = new CableSupportSystemStepper(building);
        Building result = stepper.addCablesFrame().addFlyingCables().addNodes().getResultBuilding();
        Assertions.assertEquals(0, result.getCablesCount());
    }
}