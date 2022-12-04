package adventofcode.v2021;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import adventofcode.Utils;
import org.jeasy.rules.api.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Day4 {

    public static void main(String[] args) {
        Utils.exec(args, Day4::part1, Day4::part2);
    }

    public static void part1() {
        process(1);
    }

    public static void part2() {
        process(100);
    }

    public static void process(int winnerPosition) {
        List<String> input = Utils.readInput("/v2021/d4/input.txt");
        List<Integer> draw = draw(input);
        List<Board> boards = boards(input);

        Facts facts = new Facts();
        List<Board> bingo = new ArrayList<>();
        facts.add(new Fact<>("bingo", bingo));

        Rules rules = new Rules();
        boards.forEach(board -> {
            rules.register(new BoardUpdateRule(board));
            rules.register(new BoardEvaluationRule(board));
        });

        int i;
        for (i = 0; i < draw.size() && bingo.size() < winnerPosition; ++i) {
            System.out.println("Draw " + i + " : " + draw.get(i) + " ...");
            facts.add(new Fact<>("draw", draw.get(i)));
            Utils.fire(rules, facts);
        }

        Board winner = bingo.get(winnerPosition - 1);
        System.out.println();
        System.out.println("Winner input board : ");
        System.out.println(winner.str());
        System.out.println();
        System.out.println("Winner computed board : ");
        winner.print();
        System.out.println();
        System.out.println("Winner score : " + winner.score() * draw.get(i - 1));
    }

    public static List<Integer> draw(List<String> input) {
        return Arrays
            .stream(input.get(0).split(","))
            .map(Integer::valueOf)
            .toList();
    }

    public static List<Board> boards(List<String> input) {
        return input.stream()
            .skip(1)
            .reduce((acc, v) -> acc + v + "\n")
            .map(blob -> blob.split("\n\n"))
            .stream()
            .flatMap(Arrays::stream)
            .map(bloc -> {
                List<List<String>> rows = Arrays.stream(bloc.split("\n"))
                    .map(row -> Arrays.stream(row.split(" ")).filter(value -> !value.isEmpty()).toList())
                    .toList();

                Set<Point> points = IntStream.range(0, rows.size())
                    .mapToObj(rowNumber -> {
                        List<String> row = rows.get(rowNumber);
                        return IntStream.range(0, row.size())
                            .mapToObj(colNumber -> {
                                Integer value = Integer.valueOf(row.get(colNumber));
                                return new Point(value, rowNumber, colNumber);
                            })
                            .toList();
                    })
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
                return new Board(bloc, points);
            })
            .toList();
    }

    @Data
    @RequiredArgsConstructor
    static final class Point {

        private final Integer number;
        private final Integer row;
        private final Integer col;
        private boolean marked = false;

    }

    record Board(String str, Set<Point> points) {

        private static final int BOARD_SIZE = 5;

        public boolean validate() {
            Point[][] matrix = new Point[BOARD_SIZE][BOARD_SIZE];
            points.forEach(p -> matrix[p.getRow()][p.getCol()] = p);

            for (int i = 0; i < BOARD_SIZE; i++) {
                int nbMarkedRow = 0;
                int nbMarkedCol = 0;
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (matrix[i][j].isMarked()) {
                        nbMarkedRow++;
                    }
                    if (matrix[j][i].isMarked()) {
                        nbMarkedCol++;
                    }
                }
                if (nbMarkedRow == BOARD_SIZE || nbMarkedCol == BOARD_SIZE) {
                    return true;
                }
            }

            return false;
        }

        public boolean hasNumber(Integer number) {
            return points.stream()
                .filter(p -> !p.isMarked())
                .anyMatch(p -> p.getNumber().equals(number));

        }

        public void addDraw(Integer draw) {
            points.stream()
                .filter(p -> !p.isMarked())
                .filter(p -> p.getNumber().equals(draw))
                .findFirst()
                .ifPresent(p -> p.setMarked(true));
        }

        public Integer score() {
            return points.stream()
                .filter(p -> !p.isMarked())
                .mapToInt(Point::getNumber)
                .sum();
        }

        public void print() {
            points.stream()
                .collect(Collectors.groupingBy(Point::getRow, Collectors.groupingBy(Point::getCol)))
                .values()
                .forEach(row -> {
                    row.values()
                        .forEach(p -> {
                            Point point = p.get(0);
                            String number = point.getNumber() < 10 ? " " + point.getNumber() : "" + point.getNumber();
                            System.out.print(number + (point.isMarked() ? "(*) " : "( ) "));
                        });
                    System.out.println();
                });
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class BoardUpdateRule implements Rule {

        private final Board board;

        @Override
        public boolean evaluate(Facts facts) {
            Integer draw = facts.get("draw");
            List<Board> bingo = facts.get("bingo");
            return !bingo.contains(board) && draw != null && board.hasNumber(draw);
        }

        @Override
        public void execute(Facts facts) {
            Integer draw = facts.get("draw");
            board.addDraw(draw);
        }

        @Override
        public int compareTo(Rule o) {
            if (o instanceof BoardUpdateRule other) {
                if (board.equals(other.getBoard())) {
                    return 0;
                } else {
                    return -1;
                }
            }
            return 1;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class BoardEvaluationRule implements Rule {

        private final Board board;

        @Override
        public boolean evaluate(Facts facts) {
            List<Board> bingo = facts.get("bingo");
            return !bingo.contains(board) && board.validate();
        }

        @Override
        public void execute(Facts facts) {
            List<Board> bingo = facts.get("bingo");
            bingo.add(board);
        }

        @Override
        public int compareTo(Rule o) {
            if (o instanceof BoardEvaluationRule other) {
                if (board.equals(other.getBoard())) {
                    return 0;
                } else {
                    return -1;
                }
            }
            return 1;
        }
    }

}
