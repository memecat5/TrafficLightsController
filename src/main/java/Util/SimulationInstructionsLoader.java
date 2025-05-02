package Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SimulationInstructionsLoader {
    public static List<Command> loadInstructions(String path) {
        // temporary
        InputStream instructionsJSON = SimulationInstructionsLoader.class.getClassLoader().getResourceAsStream("instructions.json");

        if (instructionsJSON == null) {
            throw new RuntimeException("Could not find instructions.json");
        }
        //COMMENTS!!11
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

            JsonNode node = mapper.readTree(instructionsJSON);
            JsonNode commands = node.get("commands");

            // Returns list of commands from JSON
            return mapper.readerFor(new TypeReference<List<Command>>() {})
                    .readValue(commands);


        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
