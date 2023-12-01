package adventofcode.v2022;

import adventofcode.Utils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day16 {

    private static final Pattern VALVE_PATTERN = Pattern.compile("Valve (.+) has flow rate=(\\d+); (?:tunnels|tunnel) (?:lead|leads) to (?:valves|valve) (.+)");

    public static void main(String[] args) {
        Utils.exec(Day16::part1, Day16::part2);
    }

    public static void part1() {
        Map<String, Valve> valves = valves();
        Valve valve = valves.get("AA");
        Map<String, Integer> pressures = new HashMap<>();
        int minute = 0;
        while (minute < 30) {
            Optional<Valve> next = valve.move();
            if (next.isPresent()) {
                valve = next.get();
                pressures.putIfAbsent(valve.label(), 30 - minute);
            }
            minute += 2;
        }
        Integer pressureReleased = pressures.entrySet().stream()
            .map(entry -> valves.get(entry.getKey()).flowRate() * entry.getValue())
            .reduce(0, Integer::sum);
        System.out.println("Pressure released : " + pressureReleased);
    }

    public static void part2() {

    }

    record Valve(String label, int flowRate, List<Valve> linkedValves) {

        @Override
        public String toString() {
            return "Valve{" +
                "label='" + label + '\'' +
                ", flowRate=" + flowRate +
                '}';
        }

        Optional<Valve> move() {
            return linkedValves.stream()
                .max(Comparator.comparing(Valve::flowRate));
        }
    }

    static Map<String, Valve> valves() {
        List<Pair<Valve, String[]>> pairs = Utils.readInput("/v2022/d16/example.txt").stream()
            .map(line -> {
                Matcher matcher = VALVE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    return Pair.of(new Valve(matcher.group(1), Integer.parseInt(matcher.group(2)), new ArrayList<>()), matcher.group(3).split(", "));
                }
                return null;
            })
            .filter(Objects::nonNull)
            .toList();
        Map<String, Valve> valves = pairs.stream().map(Pair::getLeft).collect(Collectors.toMap(Valve::label, Function.identity()));
        pairs.forEach(pair -> {
            Valve valve = pair.getKey();
            Arrays.stream(pair.getRight()).forEach(linkedValve -> valve.linkedValves().add(valves.get(linkedValve)));
        });
        return valves;
    }

}
