package adventofcode.v2022;

import adventofcode.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Day1 {

    public static void main(String[] args) {
        Utils.exec(Day1::part1, Day1::part2);
    }

    public static void part1() {
        Integer max = readInput()
            .stream()
            .max(Comparator.naturalOrder())
            .orElse(null);
        System.out.println("Max : " + max);
    }

    public static void part2() {
        Integer sum = readInput()
            .stream()
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .reduce(0, Integer::sum);
        System.out.println("Sum : " + sum);
    }

    static List<Integer> readInput() {
        List<Integer> initial = new ArrayList<>();
        initial.add(0);
        return Utils.readInput("/v2022/d1/input.txt").stream()
            .reduce(initial, (subtotal, element) -> {
                if (element.trim().isEmpty()) {
                    subtotal.add(0);
                } else {
                    subtotal.set(subtotal.size() -1, subtotal.get(subtotal.size() - 1) + Integer.parseInt(element));
                }
                return subtotal;

            }, (list1, list2) -> Collections.emptyList());
    }

}
