package adventofcode.v2021;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import adventofcode.Utils;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;

public class Day1 {

    public static void main(String[] args) {
        Utils.exec(args, Day1::part1, Day1::part2);
    }

    public static void part1() {
        process(readInput());
    }

    public static void part2() {
        List<Integer> measures = readInput();
        List<Integer> threeMeasurements = new ArrayList<>();
        for (int i = 0; i < measures.size() - 2; i++) {
            threeMeasurements.add(measures.get(i) + measures.get(i + 1) + measures.get(i + 2));
        }
        process(threeMeasurements);
    }

    static List<Integer> readInput() {
        return Utils.readInput("/v2021/d1/input.txt").stream().map(Integer::valueOf).collect(Collectors.toList());
    }

    static void process(List<Integer> measures) {
        Rules rules = new Rules();
        rules.register(new IncreasedRule());
        rules.register(new DecreasedRule());

        Facts facts = new Facts();
        facts.add(new Fact<>("count", new AtomicInteger(0)));
        facts.add(new Fact<>("measures", measures));
        while (measures.size() > 1) {
            Utils.fire(rules, facts);
        }

        System.out.println("Nb increased : " + facts.get("count"));
    }

    @Rule
    public static class IncreasedRule extends AbstractRule {

        @Condition
        public boolean hasIncreased(@org.jeasy.rules.annotation.Fact("measures") List<Integer> measures) {
            if (measures.size() <= 1) {
                return false;
            }
            Integer head = measures.get(0);
            Integer next = measures.get(1);
            return head < next;
        }

        @Action
        public void count(@org.jeasy.rules.annotation.Fact("count") AtomicInteger count,
            @org.jeasy.rules.annotation.Fact("measures") List<Integer> measures) {
            count.incrementAndGet();
            popAndLog(measures);
        }
    }

    @Rule
    public static class DecreasedRule extends AbstractRule {

        @Condition
        public boolean hasDecreased(@org.jeasy.rules.annotation.Fact("measures") List<Integer> measures) {
            if (measures.size() <= 1) {
                return false;
            }
            Integer head = measures.get(0);
            Integer next = measures.get(1);
            return head >= next;
        }

        @Action
        public void action(@org.jeasy.rules.annotation.Fact("measures") List<Integer> measures) {
            popAndLog(measures);
        }
    }

    abstract static class AbstractRule {
        void popAndLog(List<Integer> measures) {
            Integer head = measures.get(0);
            Integer next = measures.get(1);
            System.out.println(head + " -> " + next + " (" + whatHappens(head, next) + ")");
            measures.remove(0);
        }

        String whatHappens(Integer head, Integer next) {
            if (Objects.equals(head, next)) {
                return "no change";
            } else if (head < next) {
                return "increased";
            } else {
                return "decreased";
            }
        }
    }
}
