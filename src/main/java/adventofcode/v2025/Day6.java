package adventofcode.v2025;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import adventofcode.Utils;
import kotlin.Pair;

public class Day6 {

    static void main(String[] args) {
        Utils.exec(Day6::part1, Day6::part2);
    }

    public static void part1() {
        BigInteger count = readInput().stream()
            .map(operation -> compute(operation.getFirst(), operation.getSecond()))
            .reduce(BigInteger::add)
            .orElse(BigInteger.ZERO);
        System.out.println("total : " + count);
    }

    private static BigInteger compute(String operation, List<Integer> values) {
        return switch (operation) {
            case "+" -> values.stream().map(BigInteger::valueOf).reduce(BigInteger::add).orElse(BigInteger.ZERO);
            case "*" -> values.stream().map(BigInteger::valueOf).reduce(BigInteger::multiply).orElse(BigInteger.ZERO);
            default -> BigInteger.ZERO;
        };
    }

    public static void part2() {
        BigInteger count = readPart2().stream()
            .map(operation -> compute(operation.getFirst(), operation.getSecond()))
            .reduce(BigInteger::add)
            .orElse(BigInteger.ZERO);
        System.out.println("total : " + count);
    }

    static List<Pair<String, List<Integer>>> readInput() {
        List<String> strings = Utils.readInput("/v2025/d6/input.txt");
        List<String[]> parts = strings.stream()
            .map(line -> line.trim().split("\\s+"))
            .toList();
        List<Pair<String, List<Integer>>> result = new ArrayList<>();
        for (int i = 0; i < parts.getFirst().length; i++) {
            List<Integer> values = new ArrayList<>();
            for (int j = 0; j < strings.size() - 1; j++) {
                values.add(Integer.parseInt(parts.get(j)[i]));
            }
            result.add(new Pair<>(parts.get(strings.size() - 1)[i], values));
        }
        return result;
    }

    static List<Pair<String, List<Integer>>> readPart2() {
        List<String> lines = Utils.readInput("/v2025/d6/input.txt");
        List<Integer> operationsStarts = new ArrayList<>();
        String operationsLine = lines.getLast();
        for (int i = 0; i < operationsLine.length(); i++) {
            if (operationsLine.charAt(i) == '+' || operationsLine.charAt(i) == '*') {
                operationsStarts.add(i);
            }
        }

        List<Pair<String, List<Integer>>> result = new ArrayList<>();
        for (int i = 0; i < operationsStarts.size(); i++) {
            String operation = String.valueOf(operationsLine.charAt(operationsStarts.get(i)));
            int operationStart = operationsStarts.get(i);
            int operationLength = i == operationsStarts.size() - 1 ?
                lines.stream().map(String::length).max(Comparator.naturalOrder()).get() - operationsStarts.get(i)
                : operationsStarts.get(i + 1) - operationsStarts.get(i) - 1;
            List<Integer> values = new ArrayList<>();
            for (int j = operationLength - 1; j >= 0; j--) {
                StringBuilder value = new StringBuilder();
                for (String line : lines) {
                    if (line.length() > operationStart + j && Character.isDigit(line.charAt(operationStart + j))) {
                        value.append(line.charAt(operationStart + j));
                    }
                }
                values.add(Integer.parseInt(value.toString()));
            }
            result.add(new Pair<>(operation, values));
        }

        return result;
    }

}
