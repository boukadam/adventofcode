 package adventofcode.v2025;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import adventofcode.Utils;

public class Day4 {

    public static void main(String[] args) {
        Utils.exec(Day4::part1, Day4::part2);
    }

    public static void part1() {
        int count = 0;
        Integer[][] matrix = readInput();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (isRollPaper(matrix, i, j) && canBeAccessed(matrix, i, j)) {
                    count++;
                }
            }
        }
        System.out.println("total : " + count);
    }

    public static void part2() {
        int count = 0;
        int stepCount = 0;
        Integer[][] matrix = readInput();
        Integer[][] next = new Integer[matrix.length][matrix[0].length];
        do {
            stepCount = 0;
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (isRollPaper(matrix, i, j) && canBeAccessed(matrix, i, j)) {
                        stepCount++;
                        next[i][j] = 0;
                    } else {
                        next[i][j] = matrix[i][j];
                    }
                }
            }
            matrix = next;
            count += stepCount;
        } while (stepCount > 0);
        System.out.println("total : " + count);
    }

    private static boolean isRollPaper(Integer[][] matrix, int row, int col) {
        return matrix[row][col] == 1;
    }

    private static boolean canBeAccessed(Integer[][] matrix, int row, int col) {
        int count = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int x = row + i;
                int y = col + j;
                if ((i == 0 && j == 0) || x < 0 || y < 0 || x >= matrix.length || y >= matrix[0].length) {
                    continue;
                }
                count += matrix[x][y];
            }
        }
        return count < 4;
    }

    static Integer[][] readInput() {
        List<String> strings = Utils.readInput("/v2025/d4/input.txt");
        Integer[][] matrix = new Integer[strings.size()][strings.get(0).length()];
        for (int i = 0; i < strings.size(); i++) {
            for (int j = 0; j < strings.get(i).length(); j++) {
                matrix[i][j] = strings.get(i).charAt(j) == '@' ? 1 : 0;
            }
        }
        return matrix;
    }

}
