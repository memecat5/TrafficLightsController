package Model;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * Represents queue of cars waiting on one road of the intersection.
 *
 */
public class CarQueue {
    // For cars going right and forward
    private final Queue<Car> rightLane =  new LinkedList<>();
    // For cars going left
    private final Queue<Car> leftLane =  new LinkedList<>();

    // Direction from which the road is coming
    private final WorldDirection direction;

    CarQueue(WorldDirection direction) {
        this.direction = direction;
    }

    public int getLeftLaneLength() {
        return leftLane.size();
    }

    public int getRightLaneLength() {
        return rightLane.size();
    }

    /**
     * Adds car to the queue, checks it's heading
     * to put it on the correct lane.
     * @param car car to be added
     */
    public void addCar(Car car) {
        // Check if car is going left
        if(car.getTurn() == TurnDirection.LEFT) {
            leftLane.add(car);
        } else {
            rightLane.add(car);
        }
    }

    /**
     * Handles cars leaving intersection with green light
     * for turning left (only left lane).
     * @return Optional of car leaving left lane or empty
     * if no car was waiting.
     */
    public Optional<Car> leaveLeftLane() {
        return Optional.ofNullable(leftLane.poll());
    }

    /**
     * Handles cars leaving intersection with green light
     * for right lane.
     * @return Optional of car leaving right lane or empty
     * if no car was waiting.
     */
    public Optional<Car> leaveRightLane() {
        return Optional.ofNullable(rightLane.poll());
    }

    /**
     * Handles car leaving on green arrow to the right.
     * Checks if the first car in the right lane queue is
     * headed right If so, it leaves, else nothing happens.
     * @return Optional of car going to the right or empty
     */
    public Optional<Car> leaveRightLaneGreenArrow() {
        if (!rightLane.isEmpty() && rightLane.peek().getTurn().equals(TurnDirection.RIGHT)) {
            return Optional.ofNullable(rightLane.poll());
        } else {
            return Optional.empty();
        }
    }
}
