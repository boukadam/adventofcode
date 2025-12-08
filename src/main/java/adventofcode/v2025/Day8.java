package adventofcode.v2025;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import adventofcode.Utils;

public class Day8 {

    static void main(String[] args) {
        Utils.exec(Day8::part1, Day8::part2);
    }

    public static void part1() {

        Map<Double, Pair> distances = computeDistances();
        List<Double> orderedDistances = getOrderedDistances(distances);

        List<Circuit> circuits = new ArrayList<>();
        computeCircuits(orderedDistances, distances, circuits, 1000);

        circuits.stream()
            .map(Circuit::size)
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .reduce((a, b) -> a * b)
            .ifPresent(result -> System.out.println("Result: " + result));

    }

    public static void part2() {
        Map<Double, Pair> distances = computeDistances();
        List<Double> orderedDistances = getOrderedDistances(distances);

        List<Circuit> circuits = new ArrayList<>();
        Pair last = computeCircuits(orderedDistances, distances, circuits, -1);
        System.out.println("Result: " + last.a.x * last.b.x);
    }

    private static Pair computeCircuits(List<Double> orderedDistances, Map<Double, Pair> distances, List<Circuit> circuits, int limit) {
        Set<Pair> connections = new HashSet<>();
        Iterator<Double> iterator = orderedDistances.iterator();
        int circuitIndex = 1;
        Pair lastPair = null;
        int boxesSize = readInput().size();
        while (limit == -1 ? lastPair == null : connections.size() < limit) {
            Pair pair = distances.get(iterator.next());
            connections.add(pair);
            if (circuits.stream().anyMatch(c -> c.contains(pair))) {
                System.err.println(pair + " already connected");
                continue;
            }
            List<Circuit> matchingCircuits = circuits.stream().filter(c -> c.canConnect(pair)).toList();
            if (matchingCircuits.isEmpty()) {
                Set<Coordinate> list = new HashSet<>();
                list.add(pair.a);
                list.add(pair.b);
                Circuit c = new Circuit(circuitIndex++ + "", list);
                circuits.add(c);
                System.err.println(pair + " create new circuit " + c.id);
            } else {
                Circuit circuit;
                if (matchingCircuits.size() == 1) {
                    circuit = matchingCircuits.getFirst();
                    circuit.add(pair);
                } else {
                    circuits.removeAll(matchingCircuits);
                    Set<Coordinate> collectedCoordinates = matchingCircuits.stream().flatMap(c -> c.coordinates.stream()).collect(Collectors.toSet());
                    circuit = new Circuit(circuitIndex++ + "", collectedCoordinates);
                    circuits.add(circuit);
                    System.err.println("Replacing circuits " + matchingCircuits.stream().map(c -> c.id).collect(Collectors.joining(", ")) + " by " + circuit.id);
                }
                circuit.add(pair);
                if (circuits.size() == 1 && circuits.getFirst().coordinates.size() == boxesSize) {
                    lastPair = pair;
                }
                System.err.println(pair + " added to " + circuit.id);
            }
        }
        return lastPair;
    }

    private static List<Double> getOrderedDistances(Map<Double, Pair> distances) {
        return distances.keySet().stream()
            .filter(key -> key > 0)
            .sorted()
            .toList();
    }

    private static Map<Double, Pair> computeDistances() {
        List<Coordinate> coordinates = readInput();
        Map<Double, Pair> distances = new HashMap<>();
        for (Coordinate coordinate : coordinates) {
            for (Coordinate coordinate2 : coordinates) {
                distances.put(coordinate.distance(coordinate2), new Pair(coordinate, coordinate2));
            }
        }
        return distances;
    }

    static List<Coordinate> readInput() {
        return Utils.readInput("/v2025/d8/input.txt").stream()
            .map(line -> line.split(","))
            .map(split -> new Coordinate(Long.valueOf(split[0]), Long.valueOf(split[1]), Long.valueOf(split[2])))
            .toList();
    }

    private record Coordinate(Long x, Long y, Long z) {

        public Double distance(Coordinate other) {
            return Math.sqrt(Math.powExact(x - other.x, 2) + Math.powExact(y - other.y, 2) + Math.powExact(z - other.z, 2));
        }
    }

    private record Pair(Coordinate a, Coordinate b) {

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pair other)) {
                return false;
            }

            // (a,b) == (a,b) ou (a,b) == (b,a)
            return (a.equals(other.a) && b.equals(other.b))
                || (a.equals(other.b) && b.equals(other.a));
        }

        @Override
        public int hashCode() {
            return a.hashCode() + b.hashCode();
        }

    }

    private record Circuit(String id, Set<Coordinate> coordinates) {

        public boolean canConnect(Pair pair) {
            return coordinates.contains(pair.a) || coordinates.contains(pair.b);
        }

        public boolean contains(Pair pair) {
            return coordinates.contains(pair.a) && coordinates.contains(pair.b);
        }

        public void add(Pair pair) {
            coordinates.add(pair.a);
            coordinates.add(pair.b);
        }

        public int size() {
            return coordinates.size();
        }

    }

}
