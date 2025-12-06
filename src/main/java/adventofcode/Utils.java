package adventofcode;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.InferenceRulesEngine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {

    private static final RulesEngine ENGINE = new InferenceRulesEngine(new RulesEngineParameters(false, false, false, Integer.MAX_VALUE));

    private Utils() {
    }

    public static List<String> readInput(String filename) {
        try {
            return Files.readAllLines(Path.of(Objects.requireNonNull(Utils.class.getResource(filename)).toURI()));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error while reading " + filename);
        }
        return List.of();
    }

    public static List<List<String>> readInputSplitBy(String filename, String separatorLine) {
        List<String> lines = readInput(filename);
        List<List<String>> result = new ArrayList<>();
        List<String> subList = new ArrayList<>();
        for (String line : lines) {
            if (line.equals(separatorLine)) {
                result.add(subList);
                subList = new ArrayList<>();
            } else {
                subList.add(line);
            }
        }
        if (CollectionUtils.isNotEmpty(subList)) {
            result.add(subList);
        }
        return result;
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

    public static void exec(Runnable... parts) {
        if (ArrayUtils.isEmpty(parts)) {
            System.out.println("Nothing to do");
            return;
        }

        for (int i = 0; i < parts.length; i++) {
            long start = System.currentTimeMillis();
            System.out.println("## Running part " + (i + 1));
            parts[i].run();
            System.out.println("Part " + (i + 1) + " finished on " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    public static void fire(Rules rules, Facts facts) {
        ENGINE.fire(rules, facts);
    }

}
