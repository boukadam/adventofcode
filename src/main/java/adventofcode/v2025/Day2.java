package adventofcode.v2025;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.LongStream;

import org.apache.commons.lang3.tuple.Pair;

import adventofcode.Utils;

public class Day2 {

    public static void main(String[] args) {
        Utils.exec(Day2::part1, Day2::part2);
    }

    public static void part1() {
        List<Long> invalids = new ArrayList<>();
        for (Pair<Long, Long> pair : readInput()) {
            List<Long> list = LongStream.rangeClosed(pair.getLeft(), pair.getRight())
                .mapToObj(String::valueOf)
                .filter(l -> l.length() % 2 == 0)
                .map(l -> splitInEqualParts(l, 2))
                .filter(parts -> Long.valueOf(parts.get(0)).equals(Long.valueOf(parts.get(1))))
                .map(parts -> Long.valueOf(parts.get(0) + parts.get(1)))
                .toList();
            invalids.addAll(list);
        }
        System.out.println("count : " + invalids.stream().reduce(Long::sum));
    }

    public static List<String> splitInEqualParts(String s, int n) {
        int len = s.length();
        int partSize = len / n;
        List<String> parts = new ArrayList<>(n);
        for (int i = 0; i < len; i += partSize) {
            parts.add(s.substring(i, i + partSize));
        }
        return parts;
    }

    public static void part2() {
        List<Long> invalids = new ArrayList<>();
        for (Pair<Long, Long> pair : readInput()) {
            List<Long> list = LongStream.rangeClosed(pair.getLeft(), pair.getRight())
                .mapToObj(String::valueOf)
                .filter(Day2::isPart2Invalid)
                .map(Long::valueOf)
                .toList();
            invalids.addAll(list);
        }
        System.out.println("count : " + invalids.stream().reduce(Long::sum));
    }

    private static boolean isPart2Invalid(String id) {
        for (int i = 2; i <= id.length(); i++) {
            if (id.length() % i != 0) {
                continue;
            }
            List<String> parts = splitInEqualParts(id, i);
            if (parts.stream().allMatch(e -> e.equals(parts.get(0)))) {
                return true;
            }
        }
        return false;
    }

    static List<Pair<Long, Long>> readInput() {
        String line = Utils.readInput("/v2025/d2/input.txt").get(0);
        return Arrays.stream(line.split(","))
            .map(s -> s.split("-"))
            .map(r -> Pair.of(Long.parseLong(r[0]), Long.parseLong(r[1])))
            .toList();
    }

}
