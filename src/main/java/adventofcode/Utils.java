package adventofcode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.InferenceRulesEngine;

public class Utils {

    private static RulesEngine ENGINE = new InferenceRulesEngine(new RulesEngineParameters(false, false, false, Integer.MAX_VALUE));

    private Utils() {}

    public static List<String> readInput(String filename) {
        try {
            return Files.readAllLines(Path.of(Objects.requireNonNull(Utils.class.getResource(filename)).toURI()));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error while reading " + filename);
        }
        return List.of();
    }

    public static void exec(String[] args, Runnable part1, Runnable part2) {
        if (args == null || args.length != 1 || !List.of("part1", "part2").contains(args[0])) {
            System.err.println("You should pass 'part1' or 'part2' as program argument");
            return;
        }
        if ("part1".equals(args[0])) {
            part1.run();
        } else if ("part2".equals(args[0])) {
            part2.run();
        }
    }

    public static void fire(Rules rules, Facts facts) {
        ENGINE.fire(rules, facts);
    }

}
