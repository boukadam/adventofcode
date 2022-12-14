package adventofcode.v2022;

import adventofcode.Utils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day13 {

    private static final Pattern PACKET_CONTENT = Pattern.compile("^\\[(.*)]$");

    public static void main(String[] args) {
        Utils.exec(Day13::part1, Day13::part2);
    }

    public static void part1() {
        List<Pair<Element, Element>> packets = readInput();
        Map<Integer, Boolean> compareResult = compare(packets);
        int result = compareResult.entrySet().stream()
            .filter(Map.Entry::getValue)
            .mapToInt(entry -> entry.getKey() + 1)
            .sum();
        System.out.println("Result : " + result);
    }


    public static void part2() {
        List<Element> packets = readInput().stream()
            .flatMap(pair -> Stream.of(pair.getLeft(), pair.getRight()))
            .collect(Collectors.toList());
        Complex add1 = new Complex(List.of(new Complex(List.of(new Value(2)))));
        packets.add(add1);
        Complex add2 = new Complex(List.of(new Complex(List.of(new Value(6)))));
        packets.add(add2);
        packets.sort((o1, o2) -> switch (o1.compareTo(o2)) {
            case EQUAL -> 0;
            case GREATER -> 1;
            case SMALLER -> -1;
        });
        int indexAdd1 = packets.indexOf(add1) + 1;
        int indexAdd2 = packets.indexOf(add2) + 1;
        System.out.println("Result : " + (indexAdd1 * indexAdd2));
    }

    static Map<Integer, Boolean> compare(List<Pair<Element, Element>> packets) {
        return IntStream.range(0, packets.size())
            .mapToObj(i -> Pair.of(i, packets.get(i).getLeft().compareTo(packets.get(i).getRight())))
            .collect(Collectors.toMap(Pair::getLeft, pair -> pair.getRight() == Element.ComparisonState.SMALLER));
    }

    static List<Pair<Element, Element>> readInput() {
        List<String> lines = Utils.readInput("/v2022/d13/input.txt");
        return ListUtils.partition(lines, 3).stream()
            .map(packets -> {
                String left = packets.get(0);
                String right = packets.get(1);
                return Pair.of(left, right);
            })
            .map(Day13::parse)
            .toList();
    }

    static Pair<Element, Element> parse(Pair<String, String> strPair) {
        return Pair.of(parse(strPair.getLeft()), parse(strPair.getRight()));
    }

    static Element parse(String element) {
        try {
            return new Value(Integer.parseInt(element));
        } catch (NumberFormatException e) {
            List<Element> elements = tokenize(element).stream()
                .map(Day13::parse)
                .toList();
            return new Complex(elements);
        }
    }

    static List<String> tokenize(String element) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        int bracketCount = 0;
        Matcher matcher = PACKET_CONTENT.matcher(element);
        if (matcher.matches()) {
            for (char c : matcher.group(1).toCharArray()) {
                if (c == ',' && bracketCount == 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                    continue;
                }
                if (c == '[') {
                    bracketCount++;
                }
                if (c == ']') {
                    bracketCount--;
                }
                currentToken.append(c);
            }
            if (StringUtils.isNotBlank(currentToken)) {
                tokens.add(currentToken.toString());
            }
        }
        return tokens;
    }

    interface Element {

        ComparisonState compareTo(Element element);

        enum ComparisonState {
            SMALLER, EQUAL, GREATER
        }

    }

    record Value(int value) implements Element {

        @Override
        public ComparisonState compareTo(Element element) {
            if (element instanceof Value otherValue) {
                int left = value;
                int right = otherValue.value();
                if (left > right) {
                    return ComparisonState.GREATER;
                } else if (left < right) {
                    return ComparisonState.SMALLER;
                } else {
                    return ComparisonState.EQUAL;
                }
            }
            return new Complex(List.of(this)).compareTo(element);
        }
    }

    record Complex(List<Element> elements) implements Element {

        @Override
        public ComparisonState compareTo(Element element) {
            if (element instanceof Value otherValue) {
                return compareTo(new Complex(List.of(otherValue)));
            }
            if (element instanceof Complex otherComplex) {
                for (int i = 0; i < this.elements.size(); i++) {
                    if (otherComplex.elements().size() == i) {
                        return ComparisonState.GREATER;
                    }
                    Element left = this.elements.get(i);
                    Element right = otherComplex.elements().get(i);
                    ComparisonState comparisonState = left.compareTo(right);
                    if (comparisonState == ComparisonState.EQUAL) {
                        continue;
                    }
                    return comparisonState;
                }
                if (elements.size() < otherComplex.elements.size()) {
                    return ComparisonState.SMALLER;
                } else if (elements.size() == otherComplex.elements.size()) {
                    return ComparisonState.EQUAL;
                }
            }
            return ComparisonState.GREATER;
        }
    }

}
