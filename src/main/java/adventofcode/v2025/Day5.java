package adventofcode.v2025;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

import org.apache.commons.lang3.Range;

import adventofcode.Utils;

public class Day5 {

    static void main(String[] args) {
        Utils.exec(Day5::part1, Day5::part2);
    }

    public static void part1() {
        AtomicLong count = new AtomicLong();
        List<Range<Long>> ranges = readFreshness();
        for (Long ingredient : readAvailableIngredients()) {
            ranges.stream()
                .filter(range -> range.contains(ingredient))
                .findAny()
                .ifPresent(ignored -> count.getAndIncrement());
        }
        System.out.println("total : " + count);
    }

    public static void part2() {
        List<Range<Long>> ranges = new ArrayList<>();
        Iterator<Range<Long>> iterator = readFreshness().iterator();
        Range<Long> next = iterator.next();
        while (iterator.hasNext()) {
            Range<Long> range = iterator.next();
            if (range.isOverlappedBy(next)) {
                next = Range.of(Math.min(range.getMinimum(), next.getMinimum()), Math.max(range.getMaximum(), next.getMaximum()));
            } else {
                ranges.add(next);
                next = range;
            }
        }
        ranges.add(next);
        System.out.println("total : " + ranges.stream().map(range -> LongStream.rangeClosed(range.getMinimum(), range.getMaximum()).count()).mapToLong(Long::longValue).sum());
    }

    static List<Long> readAvailableIngredients() {
        List<List<String>> input = Utils.readInputSplitBy("/v2025/d5/input.txt", "");
        return input.getLast().stream()
            .map(Long::valueOf)
            .toList();
    }

    static List<Range<Long>> readFreshness() {
        List<List<String>> input = Utils.readInputSplitBy("/v2025/d5/input.txt", "");
        return input.getFirst().stream()
            .map(e -> e.split("-"))
            .map(parts -> Range.of(Long.parseLong(parts[0]), Long.parseLong(parts[1])))
            .sorted(Comparator.comparing(Range::getMinimum))
            .toList();
    }

}
