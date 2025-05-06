import simulation.SimulationRunner;
import util.Command;
import util.SimulationOutputSaver;

import java.util.List;

import static util.SimulationInstructionsLoader.loadInstructions;

public class Main {
    public static void main(String[] args) {
        // Check correct number of arguments
        if (args.length != 2) {
            System.out.println("Wrong number of arguments");
            System.exit(1);
        }
        // Read arguments from input file
        List<Command> commandList = loadInstructions(args[0]);

        // Initialize simulation runner
        // I've added drawing intersection to the console but there is some problem with encoding
        // on my machine though it should be working
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
