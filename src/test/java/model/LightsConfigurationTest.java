package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LightsConfigurationTest {

    @Test
    public void testConstructorAndGetters() {
        LightsConfiguration config = new LightsConfiguration(
                LightsConfiguration.LightsMode.twoLeftLanes,
                WorldDirection.NORTH
        );

        assertEquals(LightsConfiguration.LightsMode.twoLeftLanes, config.getMode());
        assertEquals(WorldDirection.NORTH, config.getWhereGreen());
    }

    @Test
    public void testCopyConstructor() {
        LightsConfiguration original = new LightsConfiguration(
                LightsConfiguration.LightsMode.leftAndRightLane,
                WorldDirection.SOUTH
        );
        LightsConfiguration copy = new LightsConfiguration(original);

        assertEquals(original.getMode(), copy.getMode());
        assertEquals(original.getWhereGreen(), copy.getWhereGreen());
        assertEquals(original, copy);
        assertNotSame(original, copy);  // Ensure it's a different instance
    }

    @Test
    public void testEqualsAndHashCode() {
        LightsConfiguration config1 = new LightsConfiguration(
                LightsConfiguration.LightsMode.twoRightLanes,
                WorldDirection.EAST
        );
        LightsConfiguration config2 = new LightsConfiguration(
                LightsConfiguration.LightsMode.twoRightLanes,
                WorldDirection.EAST
        );
        LightsConfiguration config3 = new LightsConfiguration(
                LightsConfiguration.LightsMode.twoLeftLanes,
                WorldDirection.EAST
        );

        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());

        assertNotEquals(config1, config3);
        assertNotEquals(config1.hashCode(), config3.hashCode());
    }

    @Test
    public void testEqualsWithDifferentObjectTypes() {
        LightsConfiguration config = new LightsConfiguration(
                LightsConfiguration.LightsMode.leftAndRightLane,
                WorldDirection.WEST
        );

        assertNotEquals(config, "some string");
        assertNotEquals(config, null);
    }

    @Test
    public void testToStringFormat() {
        LightsConfiguration config = new LightsConfiguration(
                LightsConfiguration.LightsMode.twoLeftLanes,
                WorldDirection.NORTH
        );

        String result = config.toString();
        assertTrue(result.contains("Mode: twoLeftLanes"));
        assertTrue(result.contains("WhereGreen: NORTH"));
    }
}
