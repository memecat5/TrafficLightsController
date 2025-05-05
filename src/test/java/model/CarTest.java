package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CarTest {

    @Test
    public void testForwardTurn() {
        Car car = new Car(1, WorldDirection.NORTH, WorldDirection.SOUTH); // Opposite
        assertEquals(1, car.getId());
        assertEquals(TurnDirection.FORWARD, car.getTurn());
    }

    @Test
    public void testRightTurn() {
        Car car = new Car(2, WorldDirection.NORTH, WorldDirection.WEST); // Right turn from NORTH
        assertEquals(2, car.getId());
        assertEquals(TurnDirection.RIGHT, car.getTurn());
    }

    @Test
    public void testLeftTurn() {
        Car car = new Car(3, WorldDirection.NORTH, WorldDirection.EAST); // Left turn from NORTH
        assertEquals(3, car.getId());
        assertEquals(TurnDirection.LEFT, car.getTurn());
    }

    @Test
    public void testAllDirectionsCombination() {
        // NORTH to SOUTH = FORWARD
        assertEquals(TurnDirection.FORWARD, new Car(4, WorldDirection.NORTH, WorldDirection.SOUTH).getTurn());

        // NORTH to WEST = RIGHT
        assertEquals(TurnDirection.RIGHT, new Car(5, WorldDirection.NORTH, WorldDirection.WEST).getTurn());

        // NORTH to EAST = LEFT
        assertEquals(TurnDirection.LEFT, new Car(6, WorldDirection.NORTH, WorldDirection.EAST).getTurn());
    }
}