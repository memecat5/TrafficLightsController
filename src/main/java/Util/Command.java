package Util;

import Model.WorldDirection;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Class representing input commands
 */
public class Command {
    public CommandType type;

    // vehicleId is stored only as int,
    // strings starting with "vehicle" are pointless and waste memory
    @JsonDeserialize(using = VehicleIdDeserializer.class)
    public int vehicleId;

    public WorldDirection startRoad;
    public WorldDirection endRoad;

    //temp
    @Override
    public String toString() {
        return "Command [commandType=" + type + ", vehicleId=" + vehicleId + ", startRoad="
                + startRoad + ", endRoad=" + endRoad + "]";
    }
}