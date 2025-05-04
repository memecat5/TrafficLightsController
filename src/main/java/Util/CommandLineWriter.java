package Util;

import Model.LightsConfiguration;

public class CommandLineWriter {
    private final String template =
            """
                             a b              \s
                            ┌┴─┴─┼───┐        \s
                            │↓| ↓│ ↑ │        \s
                     ┌──────┘ |  │   └─────── \s
                     │←                     ←g\s
                     │───────         ------- \s
                    e│→                     ←h\s
                     │-------         ─────── \s
                    f│→                     → \s
                     └─────┐   │  | ┌──────── \s
                           │ ↓ │ ↑|↑│         \s
                           └───┼─┬─┬┘         \s
                                 c d          \s""";

    public void draw(int step, LightsConfiguration lightsConfiguration, int northRight, int northLeft, int southRight, int southLeft,
                      int westRight, int westLeft, int eastRight, int eastLeft){
        String output;
        output = template.replace("a", Integer.toString(northRight));
        output = output.replace("b", Integer.toString(northLeft));
        output = output.replace("c", Integer.toString(southLeft));
        output = output.replace("d", Integer.toString(southRight));
        output = output.replace("e", Integer.toString(westLeft));
        output = output.replace("f", Integer.toString(westRight));
        output = output.replace("g", Integer.toString(eastRight));
        output = output.replace("h", Integer.toString(eastLeft));

        System.out.println("Step: " + step);
        System.out.println("Lights configuration: " + lightsConfiguration);
        System.out.println(output + "\n\n");
    }
}
