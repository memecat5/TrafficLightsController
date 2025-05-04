package Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private final int STEPS_PER_CYCLE = 40;

    // Adding minimum steps is a real life modification - configurations which last only
    // for one car to pass would be inefficient
    private final int MINIMUM_STEPS = 3;


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
        return currentConfiguration;
    }


    private void recalculateProportionsInSteps(){
        int allCars = sumQueueLengths();

        if(allCars == 0){
            for(LightsConfiguration lightsConfiguration : cycle){
                durationInSteps.put(lightsConfiguration, STEPS_PER_CYCLE / cycle.size() + MINIMUM_STEPS);
            }
        } else {
            // how to make them sum to 60?
            durationInSteps.put(cycle.get(0),
                    (lengthNorthRight + lengthSouthRight) / allCars * STEPS_PER_CYCLE + MINIMUM_STEPS
            );

            durationInSteps.put(cycle.get(1),
                    (lengthWestRight + lengthEastRight) / allCars * STEPS_PER_CYCLE + MINIMUM_STEPS
            );

            durationInSteps.put(cycle.get(2),
                    (lengthNorthLeft + lengthSouthLeft) / allCars * STEPS_PER_CYCLE + MINIMUM_STEPS
            );

            durationInSteps.put(cycle.get(3),
                    (lengthWestLeft + lengthEastLeft) / allCars * STEPS_PER_CYCLE + MINIMUM_STEPS
            );
        }
    }


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
