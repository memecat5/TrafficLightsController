package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarQueueTest {

    private CarQueue carQueue;

    @BeforeEach
    public void setUp() {
        carQueue = new CarQueue(WorldDirection.NORTH);
    }

    @Test
    public void testInitialLaneLengths() {
        assertEquals(0, carQueue.getLeftLaneLength());
        assertEquals(0, carQueue.getRightLaneLength());
    }

    @Test
    public void testAddToLeftLane() {
        Car mockCar = mock(Car.class);
        carQueue.addLeftLane(mockCar);
        assertEquals(1, carQueue.getLeftLaneLength());
    }

    @Test
    public void testAddToRightLane() {
        Car mockCar = mock(Car.class);
        carQueue.addRightLane(mockCar);
        assertEquals(1, carQueue.getRightLaneLength());
    }

    @Test
    public void testLeaveLeftLaneEmpty() {
        assertTrue(carQueue.leaveLeftLane().isEmpty());
    }

    @Test
    public void testLeaveLeftLane() {
        Car mockCar = mock(Car.class);
        carQueue.addLeftLane(mockCar);
        Optional<Car> result = carQueue.leaveLeftLane();
        assertTrue(result.isPresent());
        assertEquals(mockCar, result.get());
        assertEquals(0, carQueue.getLeftLaneLength());
    }

    @Test
    public void testLeaveRightLaneEmpty() {
        assertTrue(carQueue.leaveRightLane().isEmpty());
    }

    @Test
    public void testLeaveRightLane() {
        Car mockCar = mock(Car.class);
        carQueue.addRightLane(mockCar);
        Optional<Car> result = carQueue.leaveRightLane();
        assertTrue(result.isPresent());
        assertEquals(mockCar, result.get());
        assertEquals(0, carQueue.getRightLaneLength());
    }

    @Test
    public void testLeaveRightLaneGreenArrow_Empty() {
        assertTrue(carQueue.leaveRightLaneGreenArrow().isEmpty());
    }

    @Test
    public void testLeaveRightLaneGreenArrow_NotTurningRight() {
        Car mockCar = mock(Car.class);
        when(mockCar.getTurn()).thenReturn(TurnDirection.FORWARD);
        carQueue.addRightLane(mockCar);
        Optional<Car> result = carQueue.leaveRightLaneGreenArrow();
        assertTrue(result.isEmpty());
        // Car wasn't removed because it's not turning right
        assertEquals(1, carQueue.getRightLaneLength());
    }

    @Test
    public void testLeaveRightLaneGreenArrow_TurningRight() {
        Car mockCar = mock(Car.class);
        when(mockCar.getTurn()).thenReturn(TurnDirection.RIGHT);
        carQueue.addRightLane(mockCar);
        Optional<Car> result = carQueue.leaveRightLaneGreenArrow();
        assertTrue(result.isPresent());
        assertEquals(mockCar, result.get());
        assertEquals(0, carQueue.getRightLaneLength());
    }

    @Test
    public void testGetDirection() {
        assertEquals(WorldDirection.NORTH, carQueue.getDirection());
    }
}
