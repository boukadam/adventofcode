package adventofcode.v2022;

import adventofcode.Utils;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Day2 {

    public static void main(String[] args) {
        Utils.exec(Day2::part1, Day2::part2);
    }

    public static void part1() {
        Integer total = readInput()
            .stream()
            .map(pair -> Pair.of(puzzleFromLetter(pair.getLeft()), puzzleFromLetter(pair.getRight())))
            .map(pair -> score(pair.getLeft(), pair.getRight()).getScore() + pair.getRight().getScore())
            .reduce(0, Integer::sum);
        System.out.println("Total : " + total);

    }

    public static void part2() {
        Integer total = readInput()
            .stream()
            .map(pair -> Pair.of(puzzleFromLetter(pair.getLeft()), resultFromLetter(pair.getRight())))
            .map(pair -> Pair.of(pair.getLeft(), against(pair.getLeft(), pair.getRight())))
            .map(pair -> score(pair.getLeft(), pair.getRight()).getScore() + pair.getRight().getScore())
            .reduce(0, Integer::sum);
        System.out.println("Total : " + total);
    }

    static List<Pair<String, String>> readInput() {
        return Utils.readInput("/v2022/d2/input.txt").stream()
            .map(line -> line.split(" "))
            .map(elements -> Pair.of(elements[0], elements[1]))
            .toList();
    }

    static Puzzle puzzleFromLetter(String letter) {
        return switch (letter) {
            case "A", "X" -> Puzzle.ROCK;
            case "B", "Y" -> Puzzle.PAPER;
            case "C", "Z" -> Puzzle.SCISSORS;
            default -> null;
        };
    }

    static Result resultFromLetter(String letter) {
        return switch (letter) {
            case "X" -> Result.LOST;
            case "Y" -> Result.DRAW;
            case "Z" -> Result.WIN;
            default -> null;
        };
    }

    static Result score(Puzzle choice1, Puzzle choice2) {
        if (choice1 == choice2) {
            return Result.DRAW;
        }
        if (winAgainst(choice2) == choice1) {
            return Result.WIN;
        }
        return Result.LOST;
    }

    static Puzzle against(Puzzle choice1, Result result) {
        if (result == Result.DRAW) {
            return choice1;
        }
        if (result == Result.WIN) {
            return loseAgainst(choice1);
        }
        return winAgainst(choice1);
    }

    static Puzzle winAgainst(Puzzle choice) {
        return switch (choice) {
            case ROCK -> Puzzle.SCISSORS;
            case PAPER -> Puzzle.ROCK;
            case SCISSORS -> Puzzle.PAPER;
        };
    }

    static Puzzle loseAgainst(Puzzle choice) {
        return switch (choice) {
            case ROCK -> Puzzle.PAPER;
            case PAPER -> Puzzle.SCISSORS;
            case SCISSORS -> Puzzle.ROCK;
        };
    }

    @Getter
    enum Puzzle {
        ROCK(1), PAPER(2), SCISSORS(3);

        private final Integer score;

        Puzzle(Integer score) {
            this.score = score;
        }

    }

    @Getter
    enum Result {

        LOST(0), DRAW(3), WIN(6);

        private final Integer score;

        Result(Integer score) {
            this.score = score;
        }

    }

}
