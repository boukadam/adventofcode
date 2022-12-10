package adventofcode.v2022;

import adventofcode.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day10 {

    private static final Pattern ADDX_PATTERN = Pattern.compile("^(addx\\s(-?\\d+))$");


    public static void main(String[] args) {
        Utils.exec(Day10::part1, Day10::part2);
    }

    public static void part1() {
        List<Integer> input = readInput();
        List<Integer> steps = List.of(20, 60, 100, 140, 180, 220);
        List<Integer> results = new ArrayList<>();
        int x = 1;
        for (int i = 1; i <= input.size(); i++) {
            if (steps.contains(i)) {
                results.add(x * i);
            }
            x += input.get(i - 1);
        }
        System.out.println("Sum : " + results.stream().mapToInt(i -> i).sum());
    }

    public static void part2() {
        List<Integer> input = readInput();
        int x = 1;
        for (int i = 0; i < input.size(); i++) {
            int currentPixel = i % 40;
            if (currentPixel >= (x - 1) && currentPixel <= (x + 1)) {
                System.out.print("#");
            } else {
                System.out.print(".");
            }
            if ((i + 1) % 40 == 0) {
                System.out.println("");
            }
            x += input.get(i);
        }
    }

    static List<Integer> readInput() {
        return Utils.readInput("/v2022/d10/input.txt").stream()
            .flatMap(line -> {
                if ("noop".equals(line)) {
                    return Stream.of(0);
                }
                Matcher matcher = ADDX_PATTERN.matcher(line);
                if (matcher.matches()) {
                    return Stream.of(0, Integer.parseInt(matcher.group(2)));
                }
                return Stream.of();
            })
            .toList();

    }

}
