import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jeasy.rules.api.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;

import lombok.RequiredArgsConstructor;


public class Day5 {

    private static final Pattern pattern = Pattern.compile("^(\\d{1,3}),(\\d{1,3}) -> (\\d{1,3}),(\\d{1,3})$");

    public static void main(String[] args) {
        Utils.exec(args, Day5::part1, Day5::part2);
    }

    public static void part1() {
        process(false);
    }

    public static void part2() {
        process(true);
    }

    public static void process(boolean considerDiagonalLines) {
        List<Line> lines = readInput();
        Map<Coordinate, Integer> points = new HashMap<>();

        Rules rules = new Rules();
        rules.register(new LineSelectorRule(considerDiagonalLines));
        rules.register(new PointGeneratorRule(considerDiagonalLines));
        rules.register(new DiagramUpdateRule(points));
        rules.register(new OverlapFinderRule(points));

        Facts facts = new Facts();
        IntStream.range(0, lines.size())
            .forEach(i -> {
                facts.add(new Fact<>("line", lines.get(i)));
                Utils.fire(rules, facts);
            });
        System.out.println("Number of points where at least two lines overlap : " + facts.get("overlap-points"));
    }

    static List<Line> readInput() {
        return Utils.readInput("/d5/input.txt").stream()
            .map(pattern::matcher)
            .filter(Matcher::matches)
            .map(matcher -> new Line(new Coordinate(parseInt(matcher.group(1)), parseInt(matcher.group(2))),
                new Coordinate(parseInt(matcher.group(3)), parseInt(matcher.group(4)))))
            .collect(Collectors.toList());
    }

    public static record Line(Coordinate point1, Coordinate point2) {

        public boolean isHorizontal() {
            return point1.y == point2.y;
        }

        public boolean isVertical() {
            return point1.x == point2.x;
        }

        public boolean isDiagonal() {
            return abs(point1.x - point2.x) == abs(point1.y - point2.y);
        }
    }

    public static record Coordinate(int x, int y) {

    }

    @RequiredArgsConstructor
    public static class LineSelectorRule implements Rule {

        private final boolean considerDiagonalLines;

        @Override
        public boolean evaluate(Facts facts) {
            return facts.get("line") != null && filter(facts.get("line"));
        }

        private boolean filter(Line line) {
            return line.isVertical() || line.isHorizontal() || considerDiagonalLines && line.isDiagonal();
        }

        @Override
        public void execute(Facts facts) {
            Line line = facts.get("line");
            facts.add(new Fact<>("selected-line", line));
            facts.remove("line");
        }

        @Override
        public int compareTo(Rule o) {
            if (o instanceof LineSelectorRule) {
                return 0;
            }
            return 1;
        }
    }

    @RequiredArgsConstructor
    public static class PointGeneratorRule implements Rule {

        private final boolean considerDiagonalLines;

        @Override
        public boolean evaluate(Facts facts) {
            return facts.get("selected-line") != null;
        }

        @Override
        public void execute(Facts facts) {
            Line line = facts.get("selected-line");
            if (line.isVertical()) {
                int x = line.point1.x;
                int y1 = min(line.point1.y, line.point2.y);
                int y2 = max(line.point1.y, line.point2.y);
                IntStream.rangeClosed(y1, y2).forEach(y -> facts.add(new Fact<>("point-" + x + "-" + y, new Coordinate(x, y))));
            } else if (line.isHorizontal()) {
                int y = line.point1.y;
                int x1 = min(line.point1.x, line.point2.x);
                int x2 = max(line.point1.x, line.point2.x);
                IntStream.rangeClosed(x1, x2).forEach(x -> facts.add(new Fact<>("point-" + x + "-" + y, new Coordinate(x, y))));
            } else if (considerDiagonalLines && line.isDiagonal()) {
                final int xPace;
                final int yPace;
                int distance = abs(line.point1.x - line.point2.x);
                if (line.point1.x > line.point2.x) {
                    if (line.point1.y > line.point2.y) {
                        xPace = -1;
                        yPace = -1;
                    } else {
                        xPace = -1;
                        yPace = 1;
                    }
                } else {
                    if (line.point1.y > line.point2.y) {
                        xPace = 1;
                        yPace = -1;
                    } else {
                        xPace = 1;
                        yPace = 1;
                    }
                }
                IntStream.rangeClosed(0, distance).forEach(i -> {
                    int x = line.point1.x + (i * xPace);
                    int y = line.point1.y + (i * yPace);
                    facts.add(new Fact<>("point-" + x + "-" + y, new Coordinate(x, y)));
                });
            }
            facts.remove("selected-line");
        }

        @Override
        public int compareTo(Rule o) {
            if (o instanceof PointGeneratorRule) {
                return 0;
            }
            return 1;
        }
    }

    @RequiredArgsConstructor
    public static class DiagramUpdateRule implements Rule {

        private final Map<Coordinate, Integer> points;

        @Override
        public boolean evaluate(Facts facts) {
            return facts.asMap().keySet().stream().anyMatch(s -> s.startsWith("point-"));
        }

        @Override
        public void execute(Facts facts) {
            facts.asMap().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("point-"))
                .forEach(entry -> {
                    facts.remove(entry.getKey());
                    Coordinate point = (Coordinate) entry.getValue();
                    points.compute(point, (k, v) -> v == null ? 1 : v + 1);
                });
            facts.add(new Fact<>("diagram-updated", true));
        }

        @Override
        public int compareTo(Rule o) {
            if (o instanceof DiagramUpdateRule) {
                return 0;
            }
            return 1;
        }
    }

    @RequiredArgsConstructor
    public static class OverlapFinderRule implements Rule {

        private final Map<Coordinate, Integer> points;

        @Override
        public boolean evaluate(Facts facts) {
            return facts.get("diagram-updated") != null;
        }

        @Override
        public void execute(Facts facts) {
            facts.remove("diagram-updated");
            long count = points.values().stream()
                .filter(v -> v > 1)
                .count();
            facts.add(new Fact<>("overlap-points", count));
        }

        @Override
        public int compareTo(Rule o) {
            if (o instanceof OverlapFinderRule) {
                return 0;
            }
            return 1;
        }
    }

}
