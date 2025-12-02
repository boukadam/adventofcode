package adventofcode.v2025;

import adventofcode.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class Day1 {

    public static void main(String[] args) {
        Utils.exec(Day1::part1, Day1::part2);
    }

    public static void part1() {
        List<Integer> rotations = readInput();
        int index = 50;
        int count0 = 0;
        for (Integer rotation : rotations) {
            index = rotate(index, rotation);
            if (index == 0) {
                count0++;
            }
        }
        System.out.println("count 0 : " + count0);
    }

    private static int rotate(int current, int rotation) {
        return ( (current + rotation) % 100 + 100 ) % 100;
    }

    public static void part2() {
        List<Integer> rotations = readInput();
        int index = 50;
        int count0 = 0;
        for (Integer rotation : rotations) {
            count0 += countCross0(index, rotation);
            index = rotate(index, rotation);
        }
        System.out.println("count 0 : " + count0);
    }

    private static int countCross0(int current, int rotation) {
        int step = rotation > 0 ? 1 : -1;
        int n = Math.abs(rotation);
        int pos = current;
        int count = 0;

        for (int i = 0; i < n; i++) {
            pos = (pos + step + 100) % 100;
            if (pos == 0) {
                count++;
            }
        }

        return count;
    }

    static List<Integer> readInput() {
        return Utils.readInput("/v2025/d1/input.txt").stream()
            .map(line -> Pair.of(line.charAt(0), Integer.parseInt(line.substring(1))))
            .map(pair -> pair.getRight() * (pair.getLeft() == 'L' ? - 1 : 1))
            .toList();
    }

}
