package adventofcode.v2022;

import adventofcode.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day5 {

    private static final Pattern OPERATION_PATTERN = Pattern.compile("^move (\\d+) from (\\d) to (\\d)$");

    public static void main(String[] args) {
        Utils.exec(args, Day5::part1, Day5::part2);
    }

    public static void part1() {
        Map<Integer, Deque<String>> state = readState();
        readOperations().forEach(op -> IntStream.range(0, op.quantity()).forEach(i -> state.get(op.to()).push(state.get(op.from()).remove())));
        String result = state.values().stream()
            .map(Deque::poll)
            .collect(Collectors.joining());
        System.out.println(result);
    }

    public static void part2() {
        Map<Integer, Deque<String>> state = readState();
        readOperations().forEach(op -> {
            Deque<String> tmp = new ArrayDeque<>();
            IntStream.range(0, op.quantity()).forEach(i -> tmp.push(state.get(op.from()).remove()));
            IntStream.range(0, tmp.size()).forEach(i -> state.get(op.to()).push(tmp.remove()));
        });

        String result = state.values().stream()
            .map(Deque::poll)
            .collect(Collectors.joining());
        System.out.println(result);
    }

    static Map<Integer, Deque<String>> readState() {
        Map<Integer, Deque<String>> state = IntStream.range(0, 9)
            .boxed()
            .collect(Collectors.toMap(Function.identity(), i -> new ArrayDeque<>()));

        List<String> lines = Utils.readInput("/v2022/d5/input.txt").stream()
            .limit(8)
            .collect(Collectors.collectingAndThen(Collectors.toList(), l -> {
                Collections.reverse(l);
                return l;
            }));

        lines.stream()
            .map(line -> line.split("(?<=\\G.{4})"))
            .map(Arrays::asList)
            .forEach(row -> state.forEach((key, value) -> {
                if (row.size() > key && StringUtils.isNotBlank(row.get(key))) {
                    value.push(row.get(key).replace("[", "").replace("]", "").replace(" ", ""));
                }
            }));

        return state;

    }

    static Stream<Operation> readOperations() {
        return Utils.readInput("/v2022/d5/input.txt").stream()
            .skip(10)
            .map(Day5::readOperation);
    }

    static Operation readOperation(String line) {
        Matcher matcher = OPERATION_PATTERN.matcher(line);
        if (matcher.matches()) {
            return new Operation(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)) - 1, Integer.parseInt(matcher.group(3)) - 1);
        }
        return null;
    }

    record Operation(int quantity, int from, int to) {
    }

}
