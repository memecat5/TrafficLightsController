import Siumulation.SimulationRunner;
import Util.Command;
import Util.SimulationOutputSaver;

import java.util.List;

import static Util.SimulationInstructionsLoader.loadInstructions;

public class Main {
    public static void main(String[] args) {
        List<Command> commandList = loadInstructions(" ");

        SimulationRunner simulationRunner = new SimulationRunner(commandList, true);

        simulationRunner.run();

        List<List<Integer>> output = simulationRunner.getStepStatuses();

        SimulationOutputSaver saver = new SimulationOutputSaver("output.json");

        saver.saveSimulationOutput(output);


    }
}
