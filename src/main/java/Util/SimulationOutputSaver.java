package Util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SimulationOutputSaver {

    private final File savedOutput;

    public SimulationOutputSaver(String path) {
        try {
            if( path == null || !path.substring(path.length() - 5).equals(".json")){
                throw new IOException("Incorrect file extension " + path);
            }
            savedOutput = new File(path);
            savedOutput.createNewFile();

        } catch (IOException e) {
            throw new RuntimeException("Error creating output file", e);
        }
    }

    public void saveSimulationOutput(List<List<Integer>> output) {
        StepStatuses stepStatuses = new StepStatuses();


        for(List<Integer> vehiclesLeft : output) {
            LeftVehicles temp = new LeftVehicles();
            for(Integer vehicleID : vehiclesLeft) {
                temp.addId(vehicleID);
            }
            stepStatuses.add(temp);
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(savedOutput, stepStatuses);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to output file", e);
        }
    }


    // Wrappers for lists, only to properly save to JSON
    private class LeftVehicles{
        public List<String> leftVehicles = new LinkedList<>();

        public void addId(Integer vehicleID) {
            leftVehicles.add("vehicle" + vehicleID.toString());
        }
    }

    private class StepStatuses{
        public List<LeftVehicles> stepStatuses = new LinkedList<>();
        public void add(LeftVehicles leftVehicles) {
            stepStatuses.add(leftVehicles);
        }
    }
}

