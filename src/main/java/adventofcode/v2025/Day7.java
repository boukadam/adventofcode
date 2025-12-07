package adventofcode.v2025;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import adventofcode.Utils;

public class Day7 {

    static void main(String[] args) {
        Utils.exec(Day7::part1, Day7::part2);
    }

    public static void part1() {
        int count = 0;
        List<String> result = draw();
        result.forEach(System.out::println);
        for (int i = 1; i < result.size(); i++) {
            for (int j = 0; j < result.get(i).length(); j++) {
                if (result.get(i).charAt(j) == '^' && result.get(i - 1).charAt(j) == '|') {
                    count++;
                }
            }
        }
        System.out.println("total : " + count);
    }

    private static List<Integer> getPositions(String line) {
        return IntStream.range(0, line.length())
            .filter(i -> line.charAt(i) == '|')
            .boxed()
            .toList();
    }

    private static List<String> draw() {
        Iterator<String> iterator = readInput().iterator();
        String firstLine = iterator.next();
        List<Integer> lastPositions = getPositions(firstLine);
        List<String> result = new ArrayList<>();
        result.add(firstLine);
        while (iterator.hasNext()) {
            String line = iterator.next();
            Map<Integer, Character> newLine = new LinkedHashMap<>();
            for (int i = 0; i < line.length(); i++) {
                if (newLine.containsKey(i) && newLine.get(i) == '|') {
                    continue;
                }
                if (lastPositions.contains(i)) {
                    if (line.charAt(i) == '^') {
                        newLine.put(i - 1, '|');
                        newLine.put(i, line.charAt(i));
                        newLine.put(i + 1, '|');
                    } else {
                        newLine.put(i, '|');
                    }
                } else {
                    newLine.put(i, line.charAt(i));
                }
            }
            String lineProcessed = newLine.values().stream().map(c -> c + "").reduce((x, y) -> x + y).orElse("");
            result.add(lineProcessed);
            lastPositions = getPositions(lineProcessed);
        }
        return result;
    }

    public static void part2() {
        List<Map<Integer, Long>> result = drawAndCount();
        result.forEach(System.out::println);
        result.getLast().values().stream().reduce(Long::sum)
            .ifPresent(sum ->  System.out.println("total : " + sum));
    }

    private static List<Map<Integer, Long>> drawAndCount() {
        List<String> input = readInput();
        String firstLine = input.getFirst().replace('|', '1');
        List<Map<Integer, Long>> result = new ArrayList<>();
        result.add(Map.of(firstLine.indexOf('1'), 1L));
        for (int i = 1; i < input.size(); i++) {
            String line = input.get(i);
            Map<Integer, Long> newLine = new LinkedHashMap<>();
            Set<Integer> lastPositions = result.get(i - 1).keySet();
            for (int j = 0; j < line.length(); j++) {
                if (lastPositions.contains(j)) {
                    Long value = result.get(i - 1).get(j);
                    if (line.charAt(j) == '^') {
                        newLine.compute(j - 1, (pos, c) -> c == null ? value : c + value);
                        newLine.compute(j + 1, (pos, c) -> c == null ? value : c + value);
                    } else {
                        newLine.compute(j, (pos, c) -> c == null ? value : c + value);
                    }
                }
            }
            result.add(newLine);
        }
        return result;
    }

    static List<String> readInput() {
        return Utils.readInput("/v2025/d7/input.txt")
            .stream()
            .map(l -> l.replace('S', '|'))
            .toList();
    }

}
