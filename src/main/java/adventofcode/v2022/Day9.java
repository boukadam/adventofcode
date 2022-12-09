package adventofcode.v2022;

import adventofcode.Utils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day9 {

    private static final Pattern MOTION_PATTERN = Pattern.compile("^([UDRL])\s(\\d+)$");


    public static void main(String[] args) {
        Utils.exec(Day9::part1, Day9::part2);
    }

    public static void part1() {
        process(2);
    }

    public static void part2() {
        process(10);
    }

    static void process(int nbKnots) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < nbKnots; i++) {
            coordinates.add(new Coordinate(0, 0));
        }
        Set<Coordinate> tailPositions = new HashSet<>();
        tailPositions.add(new Coordinate(0, 0));
        for (Motion motion : readInput()) {
            tailPositions.addAll(move(coordinates, motion));
        }
        System.out.println("Nb positions visited by tail : " + tailPositions.size());
    }

    static Set<Coordinate> move(List<Coordinate> coordinates, Motion motion) {
        Set<Coordinate> tailPositions = new HashSet<>();
        for (int unit = 1; unit <= motion.unit(); unit++) {
            Coordinate nextHead = switch (motion.direction()) {
                case UP -> new Coordinate(coordinates.get(0).x(), coordinates.get(0).y() + 1);
                case DOWN -> new Coordinate(coordinates.get(0).x(), coordinates.get(0).y() - 1);
                case RIGHT -> new Coordinate(coordinates.get(0).x() + 1, coordinates.get(0).y());
                case LEFT -> new Coordinate(coordinates.get(0).x() - 1, coordinates.get(0).y());
            };
            coordinates.set(0, nextHead);
            for (int i = 1; i < coordinates.size(); i++) {
                if (coordinates.get(i).isCloseEnough(coordinates.get(i - 1))) {
                    break;
                }
                Coordinate current = coordinates.get(i);
                Coordinate toFollow = coordinates.get(i - 1);
                coordinates.set(i, moveKnot(current, toFollow));
                if (i == coordinates.size() - 1) {
                    tailPositions.add(coordinates.get(i));
                }
            }
        }
        return tailPositions;
    }

    private static Coordinate moveKnot(Coordinate current, Coordinate toFollow) {
        if (current.x() == toFollow.x()) {
            if (toFollow.y() < current.y()) {
                return new Coordinate(toFollow.x(), toFollow.y() + 1);
            } else {
                return new Coordinate(toFollow.x(), toFollow.y() - 1);
            }
        } else if (current.y() == toFollow.y()) {
            if (toFollow.x() < current.x()) {
                return new Coordinate(toFollow.x() + 1, toFollow.y());
            } else {
                return new Coordinate(toFollow.x() - 1, toFollow.y());
            }
        } else if (current.x() > toFollow.x()) {
            if (current.y() > toFollow.y()) {
                return new Coordinate(current.x() - 1, current.y() - 1);
            } else {
                return new Coordinate(current.x() - 1, current.y() + 1);
            }
        } else {
            if (current.y() > toFollow.y()) {
                return new Coordinate(current.x() + 1, current.y() - 1);
            } else {
                return new Coordinate(current.x() + 1, current.y() + 1);
            }
        }
    }

    static List<Motion> readInput() {
        return Utils.readInput("/v2022/d9/input.txt").stream()
            .map(line -> {
                Matcher matcher = MOTION_PATTERN.matcher(line);
                if (matcher.matches()) {
                    return new Motion(Motion.Direction.fromCode(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                }
                return null;
            })
            .filter(Objects::nonNull)
            .toList();

    }

    record Coordinate(int x, int y) {

        boolean isCloseEnough(Coordinate coordinate) {
            return coordinate.x() >= (x - 1) && coordinate.x() <= (x + 1)
                && coordinate.y() >= (y - 1) && coordinate.y() <= (y + 1);
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + ']';
        }
    }

    record Motion(Direction direction, Integer unit) {

        enum Direction {
            UP("U"), DOWN("D"), RIGHT("R"), LEFT("L");

            @Getter
            private final String code;

            Direction(String code) {
                this.code = code;
            }

            public static Direction fromCode(String code) {
                if (StringUtils.isBlank(code)) {
                    return null;
                }
                return Arrays.stream(values())
                    .filter(e -> e.getCode().equals(code))
                    .findFirst()
                    .orElse(null);
            }
        }

    }

}
