package adventofcode.v2025;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import adventofcode.Utils;

public class Day3 {

    public static void main(String[] args) {
        Utils.exec(Day3::part1, Day3::part2);
    }

    public static void part1() {
        System.out.println("total : " + exec(2));
    }

    public static void part2() {
        System.out.println("total : " + exec(12));
    }

    private static long exec(int size) {
        long total = 0;
        for (List<Integer> bank : readInput()) {
            int lastIndex = -1;
            StringBuilder chain = new StringBuilder();
            for (int i = 0; i < size; i++) {
                Pair<Integer, Integer> max = getMaxFromIndex(bank, lastIndex, size - i);
                chain.append(max.getRight());
                lastIndex = max.getLeft();
            }
            total += Long.parseLong(chain.toString());
        }
        return total;
    }

    private static Pair<Integer, Integer> getMaxFromIndex(List<Integer> bank, Integer index, int size) {
        Integer max = null;
        Integer maxIndex = null;
        for (int i = index + 1; i <= bank.size() - size; i++) {
            if (max == null || bank.get(i) > max) {
                max = bank.get(i);
                maxIndex = i;
            }
        }
        return Pair.of(maxIndex, max);
    }

    static List<List<Integer>> readInput() {
        return Utils.readInput("/v2025/d3/input.txt")
            .stream()
            .map(line -> line.chars().map(c -> c - '0').boxed().toList())
            .toList();
    }

}
