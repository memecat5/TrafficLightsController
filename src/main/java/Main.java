import Siumulation.SimulationRunner;
import Util.Command;
import Util.SimulationOutputSaver;

import java.util.List;

import static Util.SimulationInstructionsLoader.loadInstructions;

public class Main {
    public static void main(String[] args) {
        // Check correct number of arguments
        if (args.length != 2) {
            throw new IllegalArgumentException("Wrong number of arguments");
        }

        // Read arguments from input file
        List<Command> commandList = loadInstructions(args[0]);

        // Initialize simulation runner
        SimulationRunner simulationRunner = new SimulationRunner(commandList, false);

        // Run simulation (it implements Runnable interface so that it's easy
        // to possibly later modify it to run multiple simulation concurrently)
        simulationRunner.run();

        // Collect output from simulation
        List<List<Integer>> output = simulationRunner.getStepStatuses();

        // Save output to the specified file (create new if necessary)
        SimulationOutputSaver saver = new SimulationOutputSaver(args[1]);
        saver.saveSimulationOutput(output);

    }
}
