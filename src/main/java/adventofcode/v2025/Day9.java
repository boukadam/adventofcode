package adventofcode.v2025;

import java.util.ArrayList;
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
        List<Square> segs = new ArrayList<>();
        Coordinate prev = coordinates.get(coordinates.size() - 1);

        for (Coordinate cur : coordinates) {
            segs.add(Square.of(prev, cur));
            prev = cur;
        }

        List<Square> squares = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            for (Coordinate otherCoordinate : coordinates) {
                Square square = Square.of(coordinate, otherCoordinate);
                squares.add(square);
            }
        }

        squares.stream()
            .sorted(Comparator.comparing(Square::area).reversed())
            .filter(r -> segs.stream().allMatch(s -> !aabbCollision(r, s)))
            .mapToLong(Square::area)
            .findFirst()
            .ifPresent(System.out::println);

    }

    private static boolean aabbCollision(Square s1, Square s2) {
        long s1MinX = Math.min(Math.min(s1.a().x(), s1.b().x()), Math.min(s1.c().x(), s1.d().x()));
        long s1MaxX = Math.max(Math.max(s1.a().x(), s1.b().x()), Math.max(s1.c().x(), s1.d().x()));
        long s1MinY = Math.min(Math.min(s1.a().y(), s1.b().y()), Math.min(s1.c().y(), s1.d().y()));
        long s1MaxY = Math.max(Math.max(s1.a().y(), s1.b().y()), Math.max(s1.c().y(), s1.d().y()));

        long s2MinX = Math.min(Math.min(s2.a().x(), s2.b().x()), Math.min(s2.c().x(), s2.d().x()));
        long s2MaxX = Math.max(Math.max(s2.a().x(), s2.b().x()), Math.max(s2.c().x(), s2.d().x()));
        long s2MinY = Math.min(Math.min(s2.a().y(), s2.b().y()), Math.min(s2.c().y(), s2.d().y()));
        long s2MaxY = Math.max(Math.max(s2.a().y(), s2.b().y()), Math.max(s2.c().y(), s2.d().y()));

        boolean separated =
            s1MaxX <= s2MinX ||
                s1MinX >= s2MaxX ||
                s1MaxY <= s2MinY ||
                s1MinY >= s2MaxY;

        return !separated;
    }

    static List<Coordinate> readInput() {
        return Utils.readInput("/v2025/d9/input.txt")
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
