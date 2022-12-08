package adventofcode.v2022;

import adventofcode.Utils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public class Day8 {

    public static void main(String[] args) {
        Utils.exec(Day8::part1, Day8::part2);
    }

    public static void part1() {
        Integer[][] grid = readInput();
        int count = 0;
        for (int i = 0; i < 99; i++) {
            for (int j = 0; j < 99; j++) {
                if (isVisible(i, j, grid)) {
                    count++;
                }
            }
        }
        System.out.println("Visible trees : " + count);
    }

    public static void part2() {
        Integer[][] grid = readInput();
        int max = 0;
        for (int i = 0; i < 99; i++) {
            for (int j = 0; j < 99; j++) {
                Integer scenicScore = computeScenicScore(i, j, grid);
                if (scenicScore > max) {
                    max = scenicScore;
                }
            }
        }
        System.out.println("Max scenic score : " + max);
    }

    static Integer computeScenicScore(int x, int y, Integer[][] grid) {
        int left = lookLeft(x, y, grid).getRight();
        int right = lookRight(x, y, grid).getRight();
        int top = lookTop(x, y, grid).getRight();
        int bottom = lookBottom(x, y, grid).getRight();
        return left * right * top * bottom;
    }

    static boolean isVisible(int x, int y, Integer[][] grid) {
        if (x == 0 || x == grid[0].length - 1) {
            return true;
        }
        if (y == 0 || y == grid.length -1) {
            return true;
        }
        if (Boolean.TRUE.equals(lookLeft(x, y, grid).getLeft())) {
            return true;
        }
        if (Boolean.TRUE.equals(lookRight(x, y, grid).getLeft())) {
            return true;
        }
        if (Boolean.TRUE.equals(lookTop(x, y, grid).getLeft())) {
            return true;
        }
        return lookBottom(x, y, grid).getLeft();
    }

    static Pair<Boolean, Integer> lookLeft(int x, int y, Integer[][] grid) {
        for (int i = x - 1; i >= 0; i--) {
            if (grid[i][y] >= grid[x][y]) {
                return Pair.of(false, x - i);
            }
        }
        return Pair.of(true, x);
    }

    static Pair<Boolean, Integer> lookRight(int x, int y, Integer[][] grid) {
        for (int i = x + 1; i < grid[0].length; i++) {
            if (grid[i][y] >= grid[x][y]) {
                return Pair.of(false, i - x);
            }
        }
        return Pair.of(true, grid[0].length - x - 1);
    }

    static Pair<Boolean, Integer> lookTop(int x, int y, Integer[][] grid) {
        for (int i = y - 1; i >= 0; i--) {
            if (grid[x][i] >= grid[x][y]) {
                return Pair.of(false, y - i);
            }
        }
        return Pair.of(true, y);
    }

    static Pair<Boolean, Integer> lookBottom(int x, int y, Integer[][] grid) {
        for (int i = y + 1; i < grid.length; i++) {
            if (grid[x][i] >= grid[x][y]) {
                return Pair.of(false, i - y);
            }
        }
        return Pair.of(true, grid.length - y - 1);
    }

    static Integer[][] readInput() {
        return Utils.readInput("/v2022/d8/input.txt").stream()
            .map(line -> Arrays.stream(line.split("")).map(Integer::parseInt).toArray(Integer[]::new))
            .toArray(Integer[][]::new);
    }

}
