package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

class ControllerTest {

    private Controller controller;

    @BeforeEach
    public void setUp() {
        controller = new Controller();
    }

    @ParameterizedTest
    @MethodSource("provideQueueLengthUpdates")
    public void testUpdateQueueLengths(WorldDirection direction,
                                       int rightLength, int leftLength,
                                       String rightField, String leftField) throws Exception {
        controller.updateQueueLengths(rightLength, leftLength, direction);

        assertPrivateField(controller, rightField, rightLength);
        assertPrivateField(controller, leftField, leftLength);
    }

    private static Stream<Object[]> provideQueueLengthUpdates() {
        return Stream.of(
                new Object[]{WorldDirection.NORTH, 5, 3, "lengthNorthRight", "lengthNorthLeft"},
                new Object[]{WorldDirection.SOUTH, 4, 2, "lengthSouthRight", "lengthSouthLeft"},
                new Object[]{WorldDirection.EAST, 7, 1, "lengthEastRight", "lengthEastLeft"},
                new Object[]{WorldDirection.WEST, 6, 9, "lengthWestRight", "lengthWestLeft"}
        );
    }

    private void assertPrivateField(Object obj, String fieldName, int expectedValue) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        int actualValue = field.getInt(obj);
        assertEquals(expectedValue, actualValue, "Mismatch for field: " + fieldName);
    }

    @Test
    void switchConfigurationTest() throws Exception {
        Field currentConfig = controller.getClass().getDeclaredField("currentConfiguration");
        currentConfig.setAccessible(true);
        Field cycleConfig = controller.getClass().getDeclaredField("cycle");
        cycleConfig.setAccessible(true);

        List<LightsConfiguration> cycle = (List<LightsConfiguration>) cycleConfig.get(controller);

        Method switchConfiguration = controller.getClass().getDeclaredMethod("switchConfiguration");
        switchConfiguration.setAccessible(true);

        for (int i = 0; i < cycle.size(); i++) {
            assertEquals(cycle.get(i), currentConfig.get(controller));
            switchConfiguration.invoke(controller);
        }
        assertEquals(cycle.getFirst(), currentConfig.get(controller));
    }

    @Test
    void avoidEmptyGreenLightTest() throws Exception {
        // access to tested method
        Method avoidEmptyGreen = controller.getClass().getDeclaredMethod("avoidEmptyGreenLight");
        avoidEmptyGreen.setAccessible(true);

        // access to controller's data
        Field currentConfig = controller.getClass().getDeclaredField("currentConfiguration");
        currentConfig.setAccessible(true);
        Field northRight = controller.getClass().getDeclaredField("lengthNorthRight");
        northRight.setAccessible(true);
        Field southRight = controller.getClass().getDeclaredField("lengthSouthRight");
        southRight.setAccessible(true);
        Field eastRight = controller.getClass().getDeclaredField("lengthEastRight");
        eastRight.setAccessible(true);
        Field westRight = controller.getClass().getDeclaredField("lengthWestRight");
        westRight.setAccessible(true);
        Field northLeft = controller.getClass().getDeclaredField("lengthNorthLeft");
        northLeft.setAccessible(true);
        Field southLeft = controller.getClass().getDeclaredField("lengthSouthLeft");
        southLeft.setAccessible(true);
        Field eastLeft = controller.getClass().getDeclaredField("lengthEastLeft");
        eastLeft.setAccessible(true);
        Field westLeft = controller.getClass().getDeclaredField("lengthWestLeft");
        westLeft.setAccessible(true);

        //expected lights configurations
        LightsConfiguration thirdModeNorth =
                new LightsConfiguration(LightsConfiguration.LightsMode.leftAndRightLane, WorldDirection.NORTH);
        LightsConfiguration thirdModeSouth =
                new LightsConfiguration(LightsConfiguration.LightsMode.leftAndRightLane, WorldDirection.SOUTH);
        LightsConfiguration thirdModeWest =
                new LightsConfiguration(LightsConfiguration.LightsMode.leftAndRightLane, WorldDirection.WEST);
        LightsConfiguration thirdModeEast =
                new LightsConfiguration(LightsConfiguration.LightsMode.leftAndRightLane, WorldDirection.EAST);


        //North, right
        currentConfig.set(controller, new LightsConfiguration(
                LightsConfiguration.LightsMode.twoRightLanes, WorldDirection.NORTH));

        northRight.set(controller, 5);
        northLeft.set(controller, 3);
        southRight.set(controller, 0);
        assertEquals(Optional.of(thirdModeNorth), avoidEmptyGreen.invoke(controller));

        northLeft.set(controller, 0);
        assertFalse(((Optional<?>) avoidEmptyGreen.invoke(controller)).isPresent());


        //North, left
        currentConfig.set(controller, new LightsConfiguration(
                LightsConfiguration.LightsMode.twoLeftLanes, WorldDirection.NORTH));

        northRight.set(controller, 5);
        northLeft.set(controller, 5);
        southLeft.set(controller, 0);
        assertEquals(Optional.of(thirdModeNorth), avoidEmptyGreen.invoke(controller));

        northRight.set(controller, 0);
        assertFalse(((Optional<?>) avoidEmptyGreen.invoke(controller)).isPresent());


        //South, right
        currentConfig.set(controller, new LightsConfiguration(
                LightsConfiguration.LightsMode.twoRightLanes, WorldDirection.NORTH
        ));

        southRight.set(controller, 5);
        southLeft.set(controller, 5);
        northRight.set(controller, 0);
        assertEquals(Optional.of(thirdModeSouth), avoidEmptyGreen.invoke(controller));

        southLeft.set(controller, 0);
        assertFalse(((Optional<?>) avoidEmptyGreen.invoke(controller)).isPresent());


        // ----- South, left -----
        currentConfig.set(controller, new LightsConfiguration(
                LightsConfiguration.LightsMode.twoLeftLanes, WorldDirection.NORTH));

        southLeft.set(controller, 5);
        southRight.set(controller, 5);
        northLeft.set(controller, 0);
        assertEquals(Optional.of(thirdModeSouth), avoidEmptyGreen.invoke(controller));

        southRight.set(controller, 0);
        assertFalse(((Optional<?>) avoidEmptyGreen.invoke(controller)).isPresent());


        // ----- West, right -----
        currentConfig.set(controller, new LightsConfiguration(
                LightsConfiguration.LightsMode.twoRightLanes, WorldDirection.WEST));

        westRight.set(controller, 4);
        westLeft.set(controller, 5);
        eastRight.set(controller, 0);
        assertEquals(Optional.of(thirdModeWest), avoidEmptyGreen.invoke(controller));

        westLeft.set(controller, 0);
        assertFalse(((Optional<?>) avoidEmptyGreen.invoke(controller)).isPresent());

        // ----- West, left -----
        currentConfig.set(controller, new LightsConfiguration(LightsConfiguration.LightsMode.twoLeftLanes, WorldDirection.WEST));

        westLeft.set(controller, 6);
        westRight.set(controller, 4);
        eastLeft.set(controller, 0);
        assertEquals(Optional.of(thirdModeWest), avoidEmptyGreen.invoke(controller));

        westRight.set(controller, 0);
        assertFalse(((Optional<?>) avoidEmptyGreen.invoke(controller)).isPresent());

        // ----- East, right -----
        currentConfig.set(controller, new LightsConfiguration(LightsConfiguration.LightsMode.twoRightLanes, WorldDirection.WEST));

        eastRight.set(controller, 5);
        eastLeft.set(controller, 5);
        westRight.set(controller, 0);
        assertEquals(Optional.of(thirdModeEast), avoidEmptyGreen.invoke(controller));

        eastLeft.set(controller, 0);
        assertFalse(((Optional<?>) avoidEmptyGreen.invoke(controller)).isPresent());

        // ----- East, left -----
        currentConfig.set(controller, new LightsConfiguration(LightsConfiguration.LightsMode.twoLeftLanes, WorldDirection.WEST));

        eastLeft.set(controller, 4);
        eastRight.set(controller, 5);
        westLeft.set(controller, 0);
        assertEquals(Optional.of(thirdModeEast), avoidEmptyGreen.invoke(controller));

        eastRight.set(controller, 0);
        assertFalse(((Optional<?>) avoidEmptyGreen.invoke(controller)).isPresent());

    }

    @Test
    void recalculateProportionsTest() throws Exception {
        // access to tested method
        Method recalculateProportions = controller.getClass().getDeclaredMethod("recalculateProportionsInSteps");
        recalculateProportions.setAccessible(true);


        // access to controller's data
        Field durations = controller.getClass().getDeclaredField("durationInSteps");
        durations.setAccessible(true);
        Field northRight = controller.getClass().getDeclaredField("lengthNorthRight");
        northRight.setAccessible(true);
        Field southRight = controller.getClass().getDeclaredField("lengthSouthRight");
        southRight.setAccessible(true);
        Field eastRight = controller.getClass().getDeclaredField("lengthEastRight");
        eastRight.setAccessible(true);
        Field westRight = controller.getClass().getDeclaredField("lengthWestRight");
        westRight.setAccessible(true);
        Field northLeft = controller.getClass().getDeclaredField("lengthNorthLeft");
        northLeft.setAccessible(true);
        Field southLeft = controller.getClass().getDeclaredField("lengthSouthLeft");
        southLeft.setAccessible(true);
        Field eastLeft = controller.getClass().getDeclaredField("lengthEastLeft");
        eastLeft.setAccessible(true);
        Field westLeft = controller.getClass().getDeclaredField("lengthWestLeft");
        westLeft.setAccessible(true);
        Field steps = controller.getClass().getDeclaredField("STEPS_PER_CYCLE");
        steps.setAccessible(true);
        Field minSteps = controller.getClass().getDeclaredField("MINIMUM_STEPS");
        minSteps.setAccessible(true);
        Field cycleField = controller.getClass().getDeclaredField("cycle");
        cycleField.setAccessible(true);
        List<LightsConfiguration> cycle = (List<LightsConfiguration>) cycleField.get(controller);

        // All equal to zero
        recalculateProportions.invoke(controller);

        assertEquals((int)steps.get(controller) / cycle.size(),
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(0)));
        assertEquals((int)steps.get(controller) / cycle.size(),
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(1)));
        assertEquals((int)steps.get(controller) / cycle.size(),
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(2)));
        assertEquals((int)steps.get(controller) / cycle.size(),
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(3)));

        // All equal
        int val = 8;
        northRight.set(controller, val);
        southRight.set(controller, val);
        eastRight.set(controller, val);
        westRight.set(controller, val);
        northLeft.set(controller, val);
        southLeft.set(controller, val);
        eastLeft.set(controller, val);
        westLeft.set(controller, val);

        recalculateProportions.invoke(controller);
        int allCars = 8*8;
        assertEquals(2*val * (int)steps.get(controller) / allCars,
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(0)));
        assertEquals(2*val * (int)steps.get(controller) / allCars,
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(1)));
        assertEquals(2*val * (int)steps.get(controller) / allCars,
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(2)));
        assertEquals(2*val * (int)steps.get(controller) / allCars,
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(3)));


        // Some proportions
        northRight.set(controller, 1);
        southRight.set(controller, 1);//2
        eastRight.set(controller, 2);
        westRight.set(controller, 2);//4
        northLeft.set(controller, 5);
        southLeft.set(controller, 5);//10
        eastLeft.set(controller, 1);
        westLeft.set(controller, 1);//2
        allCars = 18;
        recalculateProportions.invoke(controller);

        assertEquals(expectedValue(1, 1, allCars, (int)steps.get(controller), (int)minSteps.get(controller)),
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(0)));
        assertEquals(expectedValue(2, 2, allCars, (int)steps.get(controller), (int)minSteps.get(controller)),
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(1)));
        assertEquals(expectedValue(5, 5, allCars, (int)steps.get(controller), (int)minSteps.get(controller)),
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(2)));
        assertEquals(expectedValue(1, 1, allCars, (int)steps.get(controller), (int)minSteps.get(controller)),
                ((Map<LightsConfiguration, Integer>)durations.get(controller)).get(cycle.get(3)));

    }
    private int expectedValue(int one, int two, int allCars, int steps, int minSteps) {
        return (int) Math.max(Math.round((one + two) * steps / (double)allCars), minSteps);
    }

    @Test
    void stepTest() throws Exception {

        // access controller's data
        Field currentStep = controller.getClass().getDeclaredField("currentStep");
        currentStep.setAccessible(true);
        Field currentConfigSteps = controller.getClass().getDeclaredField("currentConfigurationSteps");
        currentConfigSteps.setAccessible(true);
        Field currentConfig = controller.getClass().getDeclaredField("currentConfiguration");
        currentConfig.setAccessible(true);
        Field currentConfigId = controller.getClass().getDeclaredField("currentConfigurationIndex");
        currentConfigId.setAccessible(true);

        //3 normal steps, without changing mode
        currentConfigSteps.set(controller, 3);
        currentStep.set(controller, 0);
        int currentStepBefore = (int)currentStep.get(controller);

        for (int i = 0; i < 3; i++) {
            controller.step();
            assertEquals(currentStepBefore, (int) currentStep.get(controller) - 1);
            currentStepBefore = (int)currentStep.get(controller);
        }

        // changing configuration
        int currentConfigIdBefore = (int)currentConfigId.get(controller);
        controller.step();
        assertEquals(currentConfigIdBefore, (int) currentConfigId.get(controller) - 1);


    }

}