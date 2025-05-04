package Siumulation;

import Model.*;
import Util.Command;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is responsible for running the simulation.
 * It adds new cars to correct queues and moves them according
 * to current lights configuration.
 */
public class SimulationRunner implements Runnable{

    // Separate car queues for every direction
    private final CarQueue northernRoad = new CarQueue(WorldDirection.NORTH);
    private final CarQueue southernRoad = new CarQueue(WorldDirection.SOUTH);
    private final CarQueue westernRoad = new CarQueue(WorldDirection.WEST);
    private final CarQueue easternRoad = new CarQueue(WorldDirection.EAST);

    private final Controller controller = new Controller();

    private final List<Command> commands;

    private int stepCounter = 0;

    public SimulationRunner(List<Command> commands) {
        this.commands = commands;
    }


    public void run(){
        for(Command command : commands){
            if(command.type == Command.CommandType.addVehicle){
                addCar(new Car(command.vehicleId, command.startRoad, command.endRoad), command.startRoad);
            } else {
                System.out.println("Current step: " + stepCounter++);
                List<Integer> carsLeft = step();
                System.out.println("Cars that left this step: " + carsLeft);
            }
        }
    }


    /**
     * Method that performs one step of the simulation. It completes given
     * command and returns cars that left the intersection.
     * @return list of IDs of cars that left the intersection on this step.
     */
    public List<Integer> step(){
        LightsConfiguration currentConfiguration = controller.step();
        System.out.println("Current configuration: " + currentConfiguration);
        CarQueue road1 = directionToRoad(currentConfiguration.getWhereGreen());

        if(currentConfiguration.getMode() == LightsConfiguration.LightsMode.twoRightLanes){
            // Opposite to road with green light - also green light
            CarQueue road2 = directionToRoad(currentConfiguration.getWhereGreen().opposite());

            return oppositeRightLanes(road1, road2);

        } else if (currentConfiguration.getMode() == LightsConfiguration.LightsMode.twoLeftLanes) {
            // Opposite to road with green light - also green light
            CarQueue road2 = directionToRoad(currentConfiguration.getWhereGreen().opposite());

            // Roads with green arrows
            CarQueue road3 = directionToRoad(currentConfiguration.getWhereGreen().leftTurn());
            CarQueue road4 = directionToRoad(currentConfiguration.getWhereGreen().rightTurn());

            return oppositeLeftLanes(road1, road2, road3, road4);

        } else {
            // Road with green arrow, one on our left when we are on road1
            // So .rightTurn(), because it works from opposite direction
            CarQueue road2 = directionToRoad(currentConfiguration.getWhereGreen().rightTurn());

            return bothLanesOneDirection(road1, road2);
        }
    }


    /**
     * Register new car arriving at the intersection. Adds
     * it to the correct road and lane.
     * @param car new car
     * @param startRoad road on which that car appears
     */
    public void addCar(Car car, WorldDirection startRoad) {
        CarQueue road = switch (startRoad) {
            case NORTH -> northernRoad;
            case SOUTH -> southernRoad;
            case WEST -> westernRoad;
            case EAST -> easternRoad;
        };

        if(car.getTurn().equals(TurnDirection.LEFT)){
            road.addLeftLane(car);
        } else {
            road.addRightLane(car);
        }

        controller.updateQueueLengths(road.getRightLaneLength(), road.getLeftLaneLength(), startRoad);
    }

    /**
     * Handle green light for right lane (forward and right)
     * for 2 opposite directions.
     * @param road1 Road with green light
     * @param road2 Road opposite to road1 (also with green light)
     * @return List of IDs of cars that leave the intersection
     */
    private List<Integer> oppositeRightLanes(CarQueue road1, CarQueue road2) {
        List<Integer> carsLeft = new LinkedList<>();

        road1.leaveRightLane().ifPresent(c -> carsLeft.add(c.getId()) );
        road2.leaveRightLane().ifPresent(c -> carsLeft.add(c.getId()) );

        // Controller updates its data
        controller.updateQueueLengths(road1.getRightLaneLength(), road1.getLeftLaneLength(), road1.getDirection());
        controller.updateQueueLengths(road2.getRightLaneLength(), road2.getLeftLaneLength(), road2.getDirection());

        return carsLeft;
    }

    /**
     * Handle green light for left lane
     * for 2 opposite directions.
     * @param road1 Road with green light
     * @param road2 Road opposite to road1 (also with green light)
     * @param greenArrowRoad3
     * @param greenArrowRoad4
     * @return List of IDs of cars that leave the intersection
     */
    private List<Integer> oppositeLeftLanes(CarQueue road1, CarQueue road2,
                                            CarQueue greenArrowRoad3, CarQueue greenArrowRoad4) {
        List<Integer> carsLeft = new LinkedList<>();

        // Cars moving from left lanes
        road1.leaveLeftLane().ifPresent(c -> carsLeft.add(c.getId()));
        road2.leaveLeftLane().ifPresent(c -> carsLeft.add(c.getId()));

        greenArrowRoad3.leaveRightLaneGreenArrow().ifPresent(c -> carsLeft.add(c.getId()));
        greenArrowRoad4.leaveRightLaneGreenArrow().ifPresent(c -> carsLeft.add(c.getId()));


        // Controller updates its data
        controller.updateQueueLengths(road1.getRightLaneLength(), road1.getLeftLaneLength(), road1.getDirection());
        controller.updateQueueLengths(road2.getRightLaneLength(), road2.getLeftLaneLength(), road2.getDirection());
        controller.updateQueueLengths(greenArrowRoad3.getRightLaneLength(),
                greenArrowRoad3.getLeftLaneLength(), greenArrowRoad3.getDirection());
        controller.updateQueueLengths(greenArrowRoad4.getRightLaneLength(),
                greenArrowRoad4.getLeftLaneLength(), greenArrowRoad4.getDirection());


        return carsLeft;
    }

    /**
     * Handle green light for both lanes in one direction.
     * @param road Road with green light
     * @param greenArrowRoad road to its left, with green arrow
     * @return List of IDs of cars that leave the intersection
     */
    private List<Integer> bothLanesOneDirection(CarQueue road, CarQueue greenArrowRoad) {
        List<Integer> carsLeft = new LinkedList<>();

        road.leaveLeftLane().ifPresent(c -> carsLeft.add(c.getId()));
        road.leaveRightLane().ifPresent(c -> carsLeft.add(c.getId()));

        // Green arrow for turning right from the road to our left (doesn't create collision)
        greenArrowRoad.leaveRightLaneGreenArrow().ifPresent(c -> carsLeft.add(c.getId()));

        // Controller updates its data
        controller.updateQueueLengths(road.getRightLaneLength(), road.getLeftLaneLength(), road.getDirection());

        return carsLeft;
    }

    /**
     * Map directions to roads.
     * @param direction direction to get a road
     * @return road that's coming from given direction
     */
    private CarQueue directionToRoad(WorldDirection direction) {
        return switch (direction) {
            case NORTH -> northernRoad;
            case SOUTH -> southernRoad;
            case WEST -> westernRoad;
            case EAST -> easternRoad;
        };
    }
}
