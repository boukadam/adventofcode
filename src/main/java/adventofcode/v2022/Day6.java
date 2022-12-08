package adventofcode.v2022;

import adventofcode.Utils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Day6 {

    public static void main(String[] args) {
        Utils.exec(Day6::part1, Day6::part2);
    }

    public static void part1() {
        process(4);
    }

    public static void part2() {
        process(14);
    }

    static void process(int size) {
        String buffer = readBuffer();
        for (int i = 0; i < buffer.length(); i++) {
            if (i < size) {
                continue;
            }
            String window = buffer.substring(i - size, i);
            Set<String> chars = Arrays.stream(window.split(""))
                .collect(Collectors.toSet());
            if (chars.size() == size) {
                System.out.println("i -> " + i);
                break;
            }
        }
    }

    static String readBuffer() {
        return Utils.readInput("/v2022/d6/input.txt").get(0);
    }

}
