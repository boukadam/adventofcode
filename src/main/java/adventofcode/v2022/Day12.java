package adventofcode.v2022;

import adventofcode.Utils;

import java.util.*;

import static java.lang.Integer.MAX_VALUE;

public class Day12 {

    public static void main(String[] args) {
        Utils.exec(Day12::part1, Day12::part2);
    }

    public static void part1() {
        System.out.println("See python solution in resources/v2022/d12/day12.py");
    }


    public static void part2() {
        System.out.println("See python solution in resources/v2022/d12/day12.py");
    }

    static int[][] readInput() {
        return Utils.readInput("/v2022/d12/input.txt").stream()
            .map(line -> line.chars()
                .map(c -> {
                    if (c == 'S') {
                        return 96;
                    } else if (c == 'E') {
                        return 123;
                    }
                    return c;
                })
                .toArray()
            )
            .toArray(int[][]::new);
    }

    static int process(int[] entrance) {
        int[][] map = readInput();
        int[][] directions = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        int rows = map.length;
        int cols = map[0].length;

        Position startingPosition = new Position(entrance[0], entrance[1], 0);

        Set<Position> visited = new HashSet<>();

        Queue<Position> queue = new LinkedList<>();
        queue.add(startingPosition);

        while (!queue.isEmpty()) {
            Position position = queue.remove();
            if (visited.contains(position)) {
                continue;
            }
            if (position.x() == 20 && position.y() == 0) {
                return position.distance();
            }
            visited.add(startingPosition);
            int currentDistance = position.distance();

            for (int[] direction : directions) {
                int newX = position.x() + direction[0];
                int newY = position.y() + direction[1];
                Position newPosition = new Position(newX, newY, currentDistance + 1);

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols) {
                    int newHeight = map[newX][newY];
                    int oldHeight = map[position.x()][position.y()];
                    if (newHeight <= oldHeight - 1) {
                        queue.add(newPosition);
                    }
                }
            }
        }
        visited.clear();
        return MAX_VALUE;
    }

    private record Position(int x, int y, int distance) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }


}