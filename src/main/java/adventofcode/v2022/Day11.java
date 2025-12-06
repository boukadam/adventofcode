package adventofcode.v2022;

import adventofcode.Utils;
import lombok.SneakyThrows;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 {

    private static final Pattern FUNC_PATTERN = Pattern.compile("^(old|\\d+)\\s([*+])\\s(old|\\d+)$");

    public static void main(String[] args) {
        Utils.exec(Day11::part1, Day11::part2);
    }

    public static void part1() {
        process(20, i -> (long) Math.floor((double) i / 3));
    }

    /**
     * The key insight is this:
     * For any set of integers n, p and d: if d mod p = 0, then
     *   (n mod p) mod d = n mod d
     * In this case: d is the divisor (different for each monkey), n is the input number (worry level),
     * and n mod p is the reduced number. What the above observation shows is that if you take p to be
     * a common multiple for every possible d, then you may safely replace n by n mod p without messing
     * up any future divisibility test.
     */
    public static void part2() {

        List<Long> dividers = readInput().values().stream().map(Monkey::testDivisionBy).toList();
        long lcm = lcm(dividers);
        process(10000, i -> i % lcm);
    }

    static void evalMonkey(int number, Map<Integer, Monkey> monkeys, LongUnaryOperator relief) {
        Monkey currentMonkey = monkeys.get(number);
        while (!currentMonkey.items().isEmpty()) {
            Long item = currentMonkey.items().poll();
            Long worryLevel = currentMonkey.operation().apply(item);
            worryLevel = relief.applyAsLong(worryLevel);
            if (worryLevel % currentMonkey.testDivisionBy() == 0) {
                monkeys.get(currentMonkey.nextMonkeyIfTestTrue()).items().add(worryLevel);
            } else {
                monkeys.get(currentMonkey.nextMonkeyIfTestFalse()).items().add(worryLevel);
            }
            currentMonkey.itemsInspected().incrementAndGet();
        }
    }

    static void process(int rounds, LongUnaryOperator relief) {
        Map<Integer, Monkey> monkeys = readInput();
        IntStream.range(0, rounds).forEach(i -> {
            System.out.println("Round #" + (i + 1));
            monkeys.forEach((k, v) -> evalMonkey(k, monkeys, relief));
            monkeys.forEach((k, v) -> System.out.println("Monkey " + k + " inspected items " + v.itemsInspected().get()));
        });
        Long result = monkeys.values().stream()
            .map(Monkey::itemsInspected)
            .map(AtomicInteger::get)
            .sorted(Comparator.reverseOrder())
            .limit(2)
            .map(Long::valueOf)
            .reduce(1L, (x, y) -> x * y);
        System.out.println("Result -> " + result);
    }

    static Map<Integer, Monkey> readInput() {
        List<String> lines = Utils.readInput("/v2022/d11/input.txt");
        return ListUtils.partition(lines, 7).stream()
            .map(group -> {
                Integer number = getMonkeyNumber(group.get(0));
                Queue<Long> items = getItems(group.get(1));
                UnaryOperator<Long> operation = getOperation(group.get(2));
                Long testDivisionBy = getTestDivisionBy(group.get(3));
                Integer nextMonkeyIfTestTrue = getNextMonkeyIfTestTrue(group.get(4));
                Integer nextMonkeyIfTestFalse = getNextMonkeyIfTestFalse(group.get(5));
                return new Monkey(number, items, operation, testDivisionBy, nextMonkeyIfTestTrue, nextMonkeyIfTestFalse, new AtomicInteger(0));
            })
            .collect(Collectors.toMap(Monkey::number, Function.identity()));
    }

    static int getMonkeyNumber(String line) {
        return Integer.parseInt(line.replace("Monkey ", "").replace(":", ""));
    }

    @SuppressWarnings("java:S6204")
    static Queue<Long> getItems(String line) {
        String listStr = StringUtils.trim(line).replace("Starting items: ", "");
        return new LinkedList<>(Arrays.stream(listStr.split(", ")).map(Long::parseLong).toList());
    }

    static UnaryOperator<Long> getOperation(String line) {
        String functionStr = StringUtils.trim(line).replace("Operation: new = ", "");
        Matcher matcher = FUNC_PATTERN.matcher(functionStr);
        if (matcher.matches()) {
            return new UnaryOperator<>() {

                @Override
                public Long apply(Long integer) {
                    Long operand1 = getOperand(matcher.group(1), integer);
                    Long operand2 = getOperand(matcher.group(3), integer);
                    if (matcher.group(2).equals("+")) {
                        return operand1 + operand2;
                    } else {
                        return operand1 * operand2;
                    }
                }

                private Long getOperand(String str, Long integer) {
                    if ("old".equals(str)) {
                        return integer;
                    } else {
                        return Long.parseLong(str);
                    }
                }
            };
        }
        return null;
    }

    static Long getTestDivisionBy(String line) {
        return Long.parseLong(StringUtils.trim(line).replace("Test: divisible by ", ""));
    }

    static Integer getNextMonkeyIfTestTrue(String line) {
        return Integer.parseInt(StringUtils.trim(line).replace("If true: throw to monkey ", ""));
    }

    static Integer getNextMonkeyIfTestFalse(String line) {
        return Integer.parseInt(StringUtils.trim(line).replace("If false: throw to monkey ", ""));
    }

    record Monkey(int number, Queue<Long> items, Function<Long, Long> operation, Long testDivisionBy,
                  int nextMonkeyIfTestTrue, int nextMonkeyIfTestFalse, AtomicInteger itemsInspected) {

    }

    static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    static long lcm(List<Long> input) {
        long result = input.get(0);
        for (int i = 1; i < input.size(); i++) {
            result = lcm(result, input.get(i));
        }
        return result;
    }

}
