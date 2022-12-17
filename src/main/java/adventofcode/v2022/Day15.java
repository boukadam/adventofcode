package adventofcode.v2022;

import adventofcode.Utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day15 {

    private static final Pattern SENSOR_PATTERN = Pattern.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)");

    public static void main(String[] args) {
        Utils.exec(Day15::part1, Day15::part2);
    }

    public static void part1() {
        List<Sensor> sensors = sensors();
        Set<Coordinate> beacons = sensors.stream()
            .map(Sensor::beacon)
            .collect(Collectors.toSet());

        long sum = diamonds(sensors).stream()
            .filter(diamond -> diamond.containsY(2_000_000))
            .flatMap(diamond -> diamond.points(2_000_000).stream())
            .filter(coordinate -> !beacons.contains(coordinate))
            .distinct()
            .count();

        System.out.println("Sum : " + sum);
    }

    public static void part2() {
        List<Sensor> sensors = sensors();
        List<Diamond> diamonds = diamonds(sensors);
        //print(diamonds); // works only for example dataset
        Coordinate distressBeacon = findDistressBeacon(diamonds);
        System.out.println("Distress beacon position " + distressBeacon);
        System.out.println("Distress beacon tuning frequency " + ((distressBeacon.x * 4_000_000) + distressBeacon.y));
    }

    static void print(List<Diamond> diamonds) {
        char[][] grid = new char[40][40];
        System.out.print("  ");
        for (int i = 0; i < grid.length; i++) {
            Arrays.fill(grid[i], '.');
            if (i == 10) {
                System.out.print('0');
            } else {
                System.out.print(' ');
            }
        }
        System.out.println("");
        for (Diamond diamond : diamonds) {
            grid[(int) (diamond.center.y + 10)][(int) (diamond.center.x + 10)] = 'S';
            for (Coordinate border : diamond.borderPoints(0)) {
                grid[(int) (border.y + 10)][(int) (border.x + 10)] = '#';
            }
        }
        for (int i = 0; i < grid.length; i++) {
            if (i == 10) {
                System.out.print("0 ");
            } else {
                System.out.print("  ");
            }
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println("");
        }
    }

    private static Coordinate findDistressBeacon(List<Diamond> diamonds) {
        for (Diamond diamond : diamonds) {
            int i = 0;
            for (long x = diamond.left.x - 1; x <= diamond.center.x; x++) {
                long y = diamond.left.y - i;
                Coordinate coordinate = getCoordinate(diamonds, x, y);
                if (coordinate != null) return coordinate;
                i++;
            }
            i = 0;
            for (long x = diamond.left.x - 1; x <= diamond.center.x; x++) {
                long y = diamond.left.y + i;
                Coordinate coordinate = getCoordinate(diamonds, x, y);
                if (coordinate != null) return coordinate;
                i++;
            }
            i = 0;
            for (long x = diamond.right.x + 1; x >= diamond.center.x; x--) {
                long y = diamond.left.y - i;
                Coordinate coordinate = getCoordinate(diamonds, x, y);
                if (coordinate != null) return coordinate;
                i++;
            }
            i = 0;
            for (long x = diamond.right.x + 1; x >= diamond.center.x; x--) {
                long y = diamond.left.y + i;
                Coordinate coordinate = getCoordinate(diamonds, x, y);
                if (coordinate != null) return coordinate;
                i++;
            }
        }
        return null;
    }

    private static Coordinate getCoordinate(List<Diamond> diamonds, long x, long y) {
        if (x >= 0 && y >= 0 && x <= 4_000_000 && y <= 4_000_000) {
            Coordinate coordinate = new Coordinate(x, y);
            if (diamonds.stream().noneMatch(d -> d.contains(coordinate))) {
                return coordinate;
            }
        }
        return null;
    }

    static List<Diamond> diamonds(List<Sensor> sensors) {
        return sensors.stream()
            .map(sensor -> {
                long diffX = Math.abs(sensor.position.x - sensor.beacon.x);
                long diffY = Math.abs(sensor.position.y - sensor.beacon.y);
                long diff = diffX + diffY;

                Coordinate top = new Coordinate(sensor.position.x, sensor.position.y - diff);
                Coordinate down = new Coordinate(sensor.position.x, sensor.position.y + diff);
                Coordinate left = new Coordinate(sensor.position.x - diff, sensor.position.y);
                Coordinate right = new Coordinate(sensor.position.x + diff, sensor.position.y);

                return new Diamond(sensor.position, top, down, left, right);
            })
            .toList();
    }

    static List<Sensor> sensors() {
        return Utils.readInput("/v2022/d15/input.txt").stream()
            .map(line -> {
                Matcher matcher = SENSOR_PATTERN.matcher(line);
                if (matcher.matches()) {
                    long sensorX = Long.parseLong(matcher.group(1));
                    long sensorY = Long.parseLong(matcher.group(2));
                    long beaconX = Long.parseLong(matcher.group(3));
                    long beaconY = Long.parseLong(matcher.group(4));

                    Coordinate sensorPosition = new Coordinate(sensorX, sensorY);
                    Coordinate beaconPosition = new Coordinate(beaconX, beaconY);

                    return new Sensor(sensorPosition, beaconPosition);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .toList();
    }

    record Coordinate(long x, long y) {

    }

    record Sensor(Coordinate position, Coordinate beacon) {

    }

    record Diamond(Coordinate center, Coordinate top, Coordinate down, Coordinate left, Coordinate right) {

        boolean containsY(long givenY) {
            return top.y <= givenY && down.y >= givenY;
        }

        boolean contains(Coordinate coordinate) {
            if (coordinate.x >= left.x && coordinate.x <= right.x && coordinate.y >= top.y && coordinate.y <= down.y) {
                long diff = Math.abs(center.x - coordinate.x) + Math.abs(center.y - coordinate.y);
                long thisDiff = Math.abs(center.x - left.x);
                return diff <= thisDiff;
            }
            return false;
        }

        Set<Coordinate> points(long givenY) {
            if (!containsY(givenY)) {
                return Set.of();
            }
            if (givenY == center.y) {
                Set<Coordinate> coordinates = new HashSet<>();
                for (long x = left.x; x <= right.x; x++) {
                    coordinates.add(new Coordinate(x, givenY));
                }
                return coordinates;
            } else if (givenY > center.y) {
                long diffY = Math.abs(givenY - center.y);
                Set<Coordinate> coordinates = new HashSet<>();
                for (long x = left.x + diffY; x <= right.x - diffY; x++) {
                    coordinates.add(new Coordinate(x, givenY));
                }
                return coordinates;
            } else {
                long diffY = Math.abs(center.y - givenY);
                Set<Coordinate> coordinates = new HashSet<>();
                for (long x = left.x + diffY; x <= right.x - diffY; x++) {
                    coordinates.add(new Coordinate(x, givenY));
                }
                return coordinates;
            }
        }

        Set<Coordinate> borderPoints(long offset) {
            Set<Coordinate> coordinates = new HashSet<>();
            int i = 0;
            for (long x = left.x - offset; x <= center.x; x++) {
                coordinates.add(new Coordinate(x, left.y - i - offset));
                i++;
            }
            i = 0;
            for (long x = left.x - offset; x <= center.x; x++) {
                coordinates.add(new Coordinate(x, left.y + i - offset));
                i++;
            }
            i = 0;
            for (long x = right.x + offset; x >= center.x; x--) {
                coordinates.add(new Coordinate(x, left.y - i + offset));
                i++;
            }
            i = 0;
            for (long x = right.x + offset; x >= center.x; x--) {
                coordinates.add(new Coordinate(x, left.y + i + offset));
                i++;
            }
            return coordinates;
        }

    }

}
