package adventofcode.v2022;

import adventofcode.Utils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Day4 {

    public static void main(String[] args) {
        Utils.exec(Day4::part1, Day4::part2);
    }

    public static void part1() {
        long count = readInputs().stream()
            .filter(pair -> fullyContainsTheOther(pair.getLeft(), pair.getRight()))
            .count();
        System.out.println("Fully contains the other : " + count);
    }

    public static void part2() {
        long count = readInputs().stream()
            .filter(pair -> partiallyContainsTheOther(pair.getLeft(), pair.getRight()))
            .count();
        System.out.println("Fully contains the other : " + count);
    }

    static List<Pair<Interval, Interval>> readInputs() {
        return Utils.readInput("/v2022/d4/input.txt").stream()
            .map(line -> {
                String[] strings = line.split(",");
                String[] first = strings[0].split("-");
                String[] second = strings[1].split("-");
                return Pair.of(new Interval(Integer.parseInt(first[0]), Integer.parseInt(first[1])), new Interval(Integer.parseInt(second[0]), Integer.parseInt(second[1])));
            })
            .toList();
    }

    static boolean fullyContainsTheOther(Interval first, Interval second) {
        return first.includes(second) || second.includes(first);
    }

    static boolean partiallyContainsTheOther(Interval first, Interval second) {
        return first.intersect(second) || second.intersect(first);
    }

    record Interval(int min, int max) {

        boolean includes(Interval other) {
            return other.min() >= min && other.max <= max;
        }

        boolean intersect(Interval other) {
            return includes(other) || (other.min() > min && other.min() <= max) || (other.max >= min && other.max < max);
        }

    }

}
