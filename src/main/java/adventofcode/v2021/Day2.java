package adventofcode.v2021;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import adventofcode.Utils;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;

public class Day2 {

    private static final Pattern pattern = Pattern.compile("^(forward|up|down)\\s(\\d)$");

    public static void main(String[] args) {
        Utils.exec(args, Day2::part1, Day2::part2);
    }

    public static void part1() {
        Position position = new Position(new AtomicInteger(0), new AtomicInteger(0), null);

        Facts facts = facts(position, compileInstructions());
        Rules rules = rules();

        Utils.fire(rules, facts);

        System.out.println("Position : " + position);
        System.out.println("Multiplication : " + position.multiply());
    }

    public static void part2() {
        Position position = new Position(new AtomicInteger(0), new AtomicInteger(0), new AtomicInteger(0));

        Instructions instructions = new Instructions();
        Facts facts = facts(position, instructions);
        Rules rules = rules();

        readInput().forEach(instruction -> {
            instructions.clear();
            instructions.add(instruction);
            Utils.fire(rules, facts);
        });

        System.out.println("Position : " + position);
        System.out.println("Multiplication : " + position.multiply());
    }

    static List<Instruction> readInput() {
        return Utils.readInput("/v2021/d2/input.txt").stream()
            .map(pattern::matcher)
            .filter(Matcher::matches)
            .map(matcher -> new Instruction(Command.valueOf(matcher.group(1).toUpperCase()), Integer.valueOf(matcher.group(2))))
            .collect(Collectors.toList());
    }

    static Instructions compileInstructions() {
        Instructions instructions = new Instructions();
        readInput()
            .forEach(instructions::add);
        return instructions;
    }

    static Facts facts(Position position, Instructions instructions) {
        Facts facts = new Facts();
        facts.add(new Fact<>("position", position));
        facts.add(new Fact<>("forward", instructions.forwardInstructions));
        facts.add(new Fact<>("up", instructions.upInstructions));
        facts.add(new Fact<>("down", instructions.downInstructions));
        return facts;
    }

    static Rules rules() {
        Rules rules = new Rules();
        rules.register(new ForwardRule());
        rules.register(new UpRule());
        rules.register(new DownRule());
        return rules;
    }

    @Rule
    public static class ForwardRule {

        @Condition
        public boolean shouldGoForward(@org.jeasy.rules.annotation.Fact("forward") List<Integer> forwardInstructions) {
            return !forwardInstructions.isEmpty();
        }

        @Action
        public void goForward(@org.jeasy.rules.annotation.Fact("forward") List<Integer> forwardInstructions,
            @org.jeasy.rules.annotation.Fact("position") Position position) {
            position.forward(forwardInstructions.remove(0));
        }

    }

    @Rule
    public static class UpRule {

        @Condition
        public boolean shouldGoUp(@org.jeasy.rules.annotation.Fact("up") List<Integer> upInstructions) {
            return !upInstructions.isEmpty();
        }

        @Action
        public void goUp(@org.jeasy.rules.annotation.Fact("up") List<Integer> upInstructions,
            @org.jeasy.rules.annotation.Fact("position") Position position) {
            position.up(upInstructions.remove(0));
        }
    }

    @Rule
    public static class DownRule {

        @Condition
        public boolean shouldGoDown(@org.jeasy.rules.annotation.Fact("down") List<Integer> downInstructions) {
            return !downInstructions.isEmpty();
        }

        @Action
        public void goDown(@org.jeasy.rules.annotation.Fact("down") List<Integer> downInstructions,
            @org.jeasy.rules.annotation.Fact("position") Position position) {
            position.down(downInstructions.remove(0));
        }
    }

    record Instruction(Command command, Integer units) {

    }

    static class Instructions {
        List<Integer> forwardInstructions = new ArrayList<>();
        List<Integer> upInstructions = new ArrayList<>();
        List<Integer> downInstructions = new ArrayList<>();

        void add(Instruction instruction) {
            switch (instruction.command()) {
                case UP -> upInstructions.add(instruction.units());
                case DOWN -> downInstructions.add(instruction.units());
                case FORWARD -> forwardInstructions.add(instruction.units());
            }
        }

        void clear() {
            forwardInstructions.clear();
            upInstructions.clear();
            downInstructions.clear();
        }
    }

    record Position(AtomicInteger x, AtomicInteger depth, AtomicInteger aim) {

        Integer multiply() {
            return x.get() * depth.get();
        }

        void up(Integer delta) {
            if (moveWithAim()) {
                aim.addAndGet(-1 * delta);
            } else {
                depth.addAndGet(-1 * delta);
            }
        }

        void down(Integer delta) {
            if (moveWithAim()) {
                aim.addAndGet(delta);
            } else {
                depth.addAndGet(delta);
            }
        }

        void forward(Integer delta) {
            x.addAndGet(delta);
            if (moveWithAim()) {
                depth.addAndGet(delta * aim.get());
            }
        }

        private boolean moveWithAim() {
            return aim != null;
        }
    }

    enum Command {
        FORWARD, UP, DOWN
    }

}
