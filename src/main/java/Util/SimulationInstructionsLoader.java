package Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SimulationInstructionsLoader {
    public static List<Command> loadInstructions(String path) {
        File instructionsJSON = new File(path);
        if (!instructionsJSON.exists()) {
            throw new RuntimeException("Could not find instructions file");
        }

        try {
            // Using ObjectMapper from Jackson library to read and write to JSONs
            ObjectMapper mapper = new ObjectMapper();
            // My enums are in caps and in input file are not, so mapper has
            // to accept case-insensitive enums
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

            JsonNode node = mapper.readTree(instructionsJSON);
            JsonNode commands = node.get("commands");

            // Returns list of commands from JSON
            return mapper.readerFor(new TypeReference<List<Command>>() {})
                    .readValue(commands);


        } catch (IOException | IllegalArgumentException e) {
            // Can't salvage anything when there is an exception here
            throw new RuntimeException(e);
        }
    }
}
