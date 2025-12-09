package adventofcode.v2025;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import adventofcode.Utils;

public class Day9 {

    static void main(String[] args) {
        Utils.exec(Day9::part1, Day9::part2);
    }

    public static void part1() {
        List<Coordinate> coordinates = readInput();
        Map<Long, Set<Pair>> squares = new HashMap<>();
        for (Coordinate coordinate : coordinates) {
            for (Coordinate otherCoordinate : coordinates) {
                Long square = coordinate.square(otherCoordinate);
                Set<Pair> pairs = squares.compute(square, (k, v) -> v == null ? new HashSet<>() : v);
                pairs.add(new Pair(coordinate, otherCoordinate));
            }
        }
        System.out.println("total : " + squares.keySet().stream().max(Comparator.naturalOrder()));
    }

    public static void part2() {
        List<Coordinate> coordinates = readInput();
        Map<Long, Set<Pair>> squares = new HashMap<>();
        for (Coordinate coordinate : coordinates) {
            for (Coordinate otherCoordinate : coordinates) {
                Square square = Square.of(coordinate, otherCoordinate);
                if (square.corners().stream().allMatch(c -> isInsideOrOnBorder(c, coordinates))) {
                    Set<Pair> pairs = squares.compute(square.area(), (k, v) -> v == null ? new HashSet<>() : v);
                    pairs.add(new Pair(coordinate, otherCoordinate));
                }
            }
        }
        System.out.println("total : " + squares.keySet().stream().max(Comparator.naturalOrder()));
    }

    private static boolean isInsideOrOnBorder(Coordinate c, List<Coordinate> poly) {
        long crossings = 0;

        for (int i = 0; i < poly.size(); i++) {
            Coordinate a = poly.get(i);
            Coordinate b = poly.get((i + 1) % poly.size());

            if (a.y.equals(b.y) && c.y.equals(a.y) && c.x >= Math.min(a.x, b.x) && c.x <= Math.max(a.x, b.x)) {
                return true;
            }

            if (a.x.equals(b.x) && c.x.equals(a.x) && c.y >= Math.min(a.y, b.y) && c.y <= Math.max(a.y, b.y)) {
                return true;
            }

            if (a.x.equals(b.x)) {
                long yMin = Math.min(a.y, b.y);
                long yMax = Math.max(a.y, b.y);

                if (c.y >= yMin && c.y < yMax && c.x < a.x) {
                    crossings++;
                }
            }
        }

        return (crossings % 2) == 1;
    }


    static List<Coordinate> readInput() {
        return Utils.readInput("/v2025/d9/example.txt")
            .stream()
            .map(line -> line.split(","))
            .map(parts -> new Coordinate(Long.valueOf(parts[0]), Long.valueOf(parts[1])))
            .toList();
    }

    private record Coordinate(Long x, Long y) {

        public Long square(Coordinate other) {
            return (Math.abs(x - other.x) + 1) * (Math.abs(y - other.y) + 1);
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

            return (a.equals(other.a) && b.equals(other.b))
                || (a.equals(other.b) && b.equals(other.a));
        }

        @Override
        public int hashCode() {
            return a.hashCode() + b.hashCode();
        }

    }

    private record Square(Coordinate a, Coordinate b, Coordinate c, Coordinate d) {

        public Long area() {
            return (Math.abs(a.x - d.x) + 1) * (Math.abs(a.y - d.y) + 1);
        }

        public List<Coordinate> corners() {
            return List.of(a, b, c, d);
        }

        public static Square of(Coordinate a, Coordinate d) {
            Coordinate b = new Coordinate(d.x, a.y);
            Coordinate c = new Coordinate(a.x, d.y);
            return new Square(a, b, c, d);
        }

    }

}
