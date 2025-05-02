package Util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Parser to keep vehicleIdx as int x,
 * not String, to save space
 */
public class VehicleIdDeserializer extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext dc) throws IOException {
        String text = p.getText();
        if (text.startsWith("vehicle")) {
            return Integer.parseInt(text.substring(7));
        }
        throw new IOException("Invalid vehicleId format: " + text);
    }
}