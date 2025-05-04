package Model;

import java.util.Objects;

/**
 * Class represents configurations of the lights.
 * Possible modes are:
 * <p>1. Green light for right lane in 2 opposite directions (for going forward and right).</p>
 * <p>2. Green light for left lane in 2 opposite directions (for going left)
 * and green arrows for other 2 directions.</p>
 * <p>3. Green light for both lanes from one direction.</p>
 * Each mode can be applied to all directions.
 */
public class LightsConfiguration {
    public enum LightsMode {
        twoLeftLanes, twoRightLanes, leftAndRightLane
    }

    private final LightsMode mode;

    // Which direction has green light
    // We only need one the rest can be deducted from this and lights' mode
    private final WorldDirection whereGreen;

    public LightsConfiguration(LightsMode mode, WorldDirection whereGreen) {
        this.mode = mode;
        this.whereGreen = whereGreen;
    }

    public LightsConfiguration(LightsConfiguration configuration){
        this.mode = configuration.getMode();
        this.whereGreen = configuration.getWhereGreen();
    }

    public LightsMode getMode() {
        return mode;
    }

    public WorldDirection getWhereGreen() {
        return whereGreen;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LightsConfiguration that)) return false;
        // == is the same as equals with enums
        return mode == that.mode && whereGreen == that.whereGreen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, whereGreen);
    }
}
