import Siumulation.SimulationRunner;
import Util.Command;

import java.util.List;

import static Util.SimulationInstructionsLoader.loadInstructions;

public class Main {
    public static void main(String[] args) {
        List<Command> commandList = loadInstructions(" ");

        for (Command command : commandList) {
            System.out.println(command);
        }

        SimulationRunner simulationRunner = new SimulationRunner(commandList);

        simulationRunner.run();


    }
}
