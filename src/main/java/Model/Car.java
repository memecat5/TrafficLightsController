package Model;

public class Car {
    private final int id;

    private int waitingTime = 0;

    private final TurnDirection turn;

    public Car(int id, WorldDirection startRoad, WorldDirection endRoad) {
        this.id = id;

        // Determine which side is the car turning
        if(startRoad.opposite().equals(endRoad)) {
            this.turn = TurnDirection.FORWARD;
        } else if (startRoad.opposite().rightTurn().equals(endRoad)) {
            this.turn = TurnDirection.RIGHT;
        } else {
            this.turn = TurnDirection.LEFT;
        }
    }

    public int getId() {
        return id;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public TurnDirection getTurn() {
        return turn;
    }

    public void increaseWaitingTime() {
        waitingTime++;
    }
}
