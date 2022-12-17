package adventofcode.v2022;

import adventofcode.Utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class Day14 {

    public static void main(String[] args) {
        Utils.exec(Day14::part1, Day14::part2);
    }

    public static void part1() {
        boolean[][] grid = rocks();
        process(grid, 1);
    }

    public static void part2() {
        boolean[][] grid = rocks();
        boolean[][] largeGrid = new boolean[grid.length + 2][99999];

        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, largeGrid[i], 0, grid[i].length);
        }
        for (int i = 0; i < grid[0].length; i++) {
            largeGrid[grid.length][i] = false;
        }
        for (int i = 0; i < 99999; i++) {
            largeGrid[grid.length + 1][i] = true;
        }
        process(largeGrid, 0);
    }

    private static void process(boolean[][] grid, int x) {
        int sandSource = 500;
        int sandCount = 0;
        while (!grid[x][sandSource]) {
            try {
                Coordinate sandPosition = new Coordinate(sandSource, 0);
                Coordinate nextSandPosition = moveSand(sandPosition, grid);
                while (!sandPosition.equals(nextSandPosition)) {
                    sandPosition = nextSandPosition;
                    nextSandPosition = moveSand(sandPosition, grid);
                }
                grid[sandPosition.y()][sandPosition.x()] = true;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
            sandCount++;
        }
        System.out.println("Sand count : " + sandCount);
    }

    static Coordinate moveSand(Coordinate sandPosition, boolean[][] grid) {
        if (!grid[sandPosition.y() + 1][sandPosition.x()]) {
            return new Coordinate(sandPosition.x(), sandPosition.y() + 1);
        }
        if (!grid[sandPosition.y() + 1][sandPosition.x() - 1]) {
            return new Coordinate(sandPosition.x() - 1, sandPosition.y() + 1);
        }
        if (!grid[sandPosition.y() + 1][sandPosition.x() + 1]) {
            return new Coordinate(sandPosition.x() + 1, sandPosition.y() + 1);
        }
        return sandPosition;
    }

    static void print(boolean[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j]) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println("");
        }
    }

    static boolean[][] rocks() {
        List<List<Coordinate>> lines = Utils.readInput("/v2022/d14/input.txt").stream()
            .map(line -> Arrays.stream(line.split(" -> "))
                .map(item -> {
                    String[] split = item.split(",");
                    return new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                })
                .toList()
            )
            .toList();

        int maxX = lines.stream()
            .flatMap(Collection::stream)
            .map(Coordinate::x)
            .max(Comparator.naturalOrder())
            .orElse(0);

        int maxY = lines.stream()
            .flatMap(Collection::stream)
            .map(Coordinate::y)
            .max(Comparator.naturalOrder())
            .orElse(0);

        boolean[][] rocks = new boolean[maxY + 1][maxX + 1];

        lines.forEach(list -> IntStream.range(1, list.size())
            .forEach(i -> {
                Coordinate start = list.get(i - 1);
                Coordinate end = list.get(i);
                for (int k = Math.min(start.y, end.y); k <= Math.max(start.y, end.y); k++) {
                    for (int j = Math.min(start.x, end.x); j <= Math.max(start.x, end.x); j++) {
                        rocks[k][j] = true;
                    }
                }
            })
        );
        return rocks;
    }

    record Coordinate(int x, int y) {

    }

}
