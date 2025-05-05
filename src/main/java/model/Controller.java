package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class Controller {

    // Length of the queue for each lane
    // This is the only information controller
    // can receive from its sensors
    private int lengthNorthRight = 0;
    private int lengthNorthLeft = 0;
    private int lengthSouthRight = 0;
    private int lengthSouthLeft = 0;
    private int lengthWestRight = 0;
    private int lengthWestLeft = 0;
    private int lengthEastRight = 0;
    private int lengthEastLeft = 0;

    // How many steps is one cycle of light modes
    // Not exactly as they can be increased with
    // minimum step logic or due to rounding
    private final int STEPS_PER_CYCLE = 8;

    // Adding minimum steps is a real life modification - configurations which last only
    // for one car to pass would be inefficient
    private final int MINIMUM_STEPS = 1;


    private int currentStep = 0;

    // How many steps to do before changing configuration
    private int currentConfigurationSteps;



    // Under normal circumstances only twoRightLanes and twoLeftLanes modes are used.
    // However, if there are no cars on the opposite direction, there is no point
    // to put there green light and then the third mode will be used to increase efficiency.
    // Cycle:
    // 2 right lanes, north and south
    // 2 right lanes, west and east
    // 2 left lanes, north and south
    // 2 left lanes, west and east
    private final List<LightsConfiguration> cycle = List.of(
            new LightsConfiguration(LightsConfiguration.LightsMode.twoRightLanes, WorldDirection.NORTH),
            new LightsConfiguration(LightsConfiguration.LightsMode.twoRightLanes, WorldDirection.WEST),
            new LightsConfiguration(LightsConfiguration.LightsMode.twoLeftLanes, WorldDirection.NORTH),
            new LightsConfiguration(LightsConfiguration.LightsMode.twoLeftLanes, WorldDirection.WEST)
            );

    private final Map<LightsConfiguration, Integer> durationInSteps = new HashMap<>();

    // Current lights configuration
    private LightsConfiguration currentConfiguration = cycle.getFirst();
    private int currentConfigurationIndex = 0;

    public Controller() {
        // need some initial proportions
        recalculateProportionsInSteps();
        currentConfigurationSteps = durationInSteps.get(currentConfiguration);
    }

    /**
     * Updates lengths of chosen queues in controller.
     * In system closer to real life this logic should be accessed by another
     * class, which would handle reading real life sensors. Here I don't
     * have such sensors, so this method is called by SimulationRunner
     * whenever there is a change in given queue.
     * @param right length of the queue on the right lane
     * @param left same on the left lane
     * @param direction specifies the road which lengths it's updating
     */
    public void updateQueueLengths(int right, int left, WorldDirection direction) {
        switch (direction) {
            case NORTH -> {lengthNorthRight = right;lengthNorthLeft = left;}
            case SOUTH -> {lengthSouthRight = right;lengthSouthLeft = left;}
            case WEST -> {lengthWestRight = right;lengthWestLeft = left;}
            case EAST -> {lengthEastRight = right;lengthEastLeft = left;}
        }
    }

    /**
     * Reaction to one step in simulation.
     * Register passed time and optionally switch
     * current lights configuration or recalculate
     * tim proportions between cycles.
     * @return lights configuration
     */
    public LightsConfiguration step(){
        // Configuration finished, switch to a new one
        if(currentStep == currentConfigurationSteps && currentStep != 0){
            switchConfiguration();

            // Recalculate proportions on the first configuration of the cycle,
            // before the first step
            if(currentConfiguration.equals(cycle.getFirst())){
                // Calculate proportions between configurations
                recalculateProportionsInSteps();
            }

            currentConfigurationSteps = durationInSteps.get(currentConfiguration);
            currentStep = 0;

        } else{
            currentStep++;
        }

        // If there are no cars on one of the roads with green lights from current cycle
        // this will switch lights to the third mode for the remaining steps of this configuration
        if(currentConfiguration.getMode() != LightsConfiguration.LightsMode.leftAndRightLane) {
            avoidEmptyGreenLight().ifPresent(config -> currentConfiguration = config);
        }

        return currentConfiguration;
    }

    // I'm not proud of this monstrosity, but it would require to rethink
    // all controller's data and there is no time for such refactoring
    /**
     * Checks if one of the lanes which currently has green light is empty and
     * if lights will benefit from third mode.
     * @return Third light mode, turns off green light for the empty direction
     * or empty optional if no direction is empty
     */
    private Optional<LightsConfiguration> avoidEmptyGreenLight(){
        LightsConfiguration.LightsMode thirdMode = LightsConfiguration.LightsMode.leftAndRightLane;
        // 2 cases for modes
        if(currentConfiguration.getMode() == LightsConfiguration.LightsMode.twoRightLanes){
            // 2 cases for directions
            if (currentConfiguration.getWhereGreen() == WorldDirection.NORTH) {
                // Check if either lane is empty and if switching modes would help
                if (lengthSouthRight == 0 && lengthNorthLeft > 0) {
                    return Optional.of(new LightsConfiguration(thirdMode, WorldDirection.NORTH));
                } else if (lengthNorthRight == 0 && lengthSouthLeft > 0) {
                    return Optional.of(new LightsConfiguration(thirdMode, WorldDirection.SOUTH));
                }
            } else {
                // Check if either lane is empty and if switching modes would help
                if (lengthWestRight == 0 &&  lengthEastLeft > 0) {
                    return Optional.of(new LightsConfiguration(
                            thirdMode, WorldDirection.EAST));
                } else if (lengthEastRight == 0 &&  lengthWestLeft > 0) {
                    return Optional.of(new LightsConfiguration(
                            thirdMode, WorldDirection.WEST));
                }
            }
        } else {    // 2 left lanes
            // 2 cases for directions
            if (currentConfiguration.getWhereGreen() == WorldDirection.NORTH) {
                // Check if either lane is empty and if switching modes would help
                if (lengthSouthLeft == 0 && lengthNorthRight > 0) {
                    return Optional.of(new LightsConfiguration(thirdMode, WorldDirection.NORTH));
                } else if (lengthNorthLeft == 0 && lengthSouthRight > 0) {
                    return Optional.of(new LightsConfiguration(thirdMode, WorldDirection.SOUTH));
                }
            } else {
                // Check if either lane is empty and if switching modes would help
                if (lengthWestLeft == 0 &&  lengthEastRight > 0) {
                    return Optional.of(new LightsConfiguration(
                            thirdMode, WorldDirection.EAST));
                } else if (lengthEastLeft == 0 &&  lengthWestRight > 0) {
                    return Optional.of(new LightsConfiguration(
                            thirdMode, WorldDirection.WEST));
                }
            }
        }
        return Optional.empty();
    }


    /**
     * Adjust time proportions between lights configuration
     * in the cycle according to queue lengths on every road.
     */
    private void recalculateProportionsInSteps(){
        int allCars = sumQueueLengths();

        if(allCars == 0){
            for(LightsConfiguration lightsConfiguration : cycle){
                durationInSteps.put(lightsConfiguration, STEPS_PER_CYCLE / cycle.size());
            }
        } else {
            durationInSteps.put(cycle.get(0),
                     Math.max((int) Math.round((lengthNorthRight + lengthSouthRight) / (double)allCars
                             * STEPS_PER_CYCLE), MINIMUM_STEPS)
            );

            durationInSteps.put(cycle.get(1),
                    Math.max((int)Math.round((lengthWestRight + lengthEastRight) / (double)allCars
                            * STEPS_PER_CYCLE), MINIMUM_STEPS)
            );

            durationInSteps.put(cycle.get(2),
                    Math.max((int)Math.round((lengthNorthLeft + lengthSouthLeft) / (double)allCars
                            * STEPS_PER_CYCLE), MINIMUM_STEPS)
            );

            durationInSteps.put(cycle.get(3),
                    Math.max((int)Math.round((lengthWestLeft + lengthEastLeft) / (double)allCars
                            * STEPS_PER_CYCLE), MINIMUM_STEPS)
            );
        }
    }

    /**
     * Move to next lights configuration from the cycle.
     * Note: currentConfiguration may not always correspond to
     * currentConfigurationIndex if lights went into third mode, but
     * they still will be switched to the correct configuration from the
     * cycle because currentConfigurationIndex stays on the
     * original configuration from the cycle, even in the third mode.
     */
    private void switchConfiguration(){
        currentConfigurationIndex = (currentConfigurationIndex + 1) % cycle.size();
        currentConfiguration = cycle.get(currentConfigurationIndex);
    }


    private int sumQueueLengths(){
        int sum = 0;
        sum += lengthNorthRight;
        sum += lengthNorthLeft;
        sum += lengthSouthRight;
        sum += lengthSouthLeft;
        sum += lengthWestRight;
        sum += lengthWestLeft;
        sum += lengthEastRight;
        sum += lengthEastLeft;

        return sum;
    }

}
