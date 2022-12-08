package adventofcode.v2022;

import adventofcode.Utils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day3 {

    private static final String VALUES = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) {
        Utils.exec(Day3::part1, Day3::part2);
    }

    public static void part1() {
        Integer sum = readInputs()
            .stream()
            .map(line -> Pair.of(line.substring(0, line.length() / 2), line.substring(line.length() / 2)))
            .map(pair -> {
                Set<String> s1 = new HashSet<>(List.of(pair.getLeft().split("")));
                s1.retainAll(List.of(pair.getRight().split("")));
                return s1;
            })
            .map(set -> set.iterator().next().charAt(0))
            .map(item -> VALUES.indexOf(item) + 1)
            .reduce(0, Integer::sum);
        System.out.println("Sum: " + sum);
    }

    public static void part2() {
        List<String> strings = readInputs();
        Integer sum = ListUtils.partition(strings, 3)
            .stream()
            .map(group -> {
                Set<String> s1 = new HashSet<>(List.of(group.get(0).split("")));
                s1.retainAll(List.of(group.get(1).split("")));
                s1.retainAll(List.of(group.get(2).split("")));
                return s1;
            })
            .map(set -> set.iterator().next().charAt(0))
            .map(item -> VALUES.indexOf(item) + 1)
            .reduce(0, Integer::sum);
        System.out.println("Sum: " + sum);
    }

    static List<String> readInputs() {
        return Utils.readInput("/v2022/d3/input.txt").stream()
            .toList();
    }

}
