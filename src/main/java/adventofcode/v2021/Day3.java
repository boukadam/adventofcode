package adventofcode.v2021;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import adventofcode.Utils;
import org.jeasy.rules.api.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;

public class Day3 {

    public static void main(String[] args) {
        Utils.exec(args, Day3::part1, Day3::part2);
    }

    public static void part1() {
        Facts facts = new Facts();
        facts.add(new Fact<>("report", getReport()));

        Rules rules = new Rules();
        IntStream.range(0, 12).forEach(i -> rules.register(new MostCommonBitRule(i, "report", "common-bit-")));
        rules.register(new GammaRateRule());
        rules.register(new EpsilonRateRule());

        Utils.fire(rules, facts);

        String gammaRate = facts.get("gamma-rate");
        String epsilonRate = facts.get("epsilon-rate");

        BigInteger gammaRateDecimal = new BigInteger(gammaRate, 2);
        BigInteger epsilonRateDecimal = new BigInteger(epsilonRate, 2);

        System.out.println("gamma   rate : " + gammaRate + " (decimal : " + gammaRateDecimal + ")");
        System.out.println("epsilon rate : " + epsilonRate + " (decimal : " + epsilonRateDecimal + ")");

        System.out.println("Multiplication : " + gammaRateDecimal.multiply(epsilonRateDecimal));
    }

    public static void part2() {
        Facts facts = new Facts();
        List<String> report = getReport();
        ArrayList<String> oxygenWorkReport = new ArrayList<>(report);
        ArrayList<String> co2WorkReport = new ArrayList<>(report);
        facts.add(new Fact<>("oxygen-work-report", oxygenWorkReport));
        facts.add(new Fact<>("co2-work-report", co2WorkReport));

        Rules rules = new Rules();

        int currentPosition = 0;
        while (currentPosition < 12) {
            rules.clear();

            rules.register(new MostCommonBitRule(currentPosition, "oxygen-work-report", "oxygen-common-bit-"));
            rules.register(new MostCommonBitRule(currentPosition, "co2-work-report", "co2-common-bit-"));
            rules.register(new OxygenGeneratorRatingRule(currentPosition));
            rules.register(new CO2ScrubberRatingRule(currentPosition));

            Utils.fire(rules, facts);

            currentPosition++;
        }

        String oxygenGeneratorRating = oxygenWorkReport.get(0);
        String co2ScrubberRating = co2WorkReport.get(0);
        BigInteger oxygenGeneratorRatingDecimal = new BigInteger(oxygenGeneratorRating, 2);
        BigInteger co2ScrubberRatingDecimal = new BigInteger(co2ScrubberRating, 2);

        System.out.println("Oxygen Generator Rating : " + oxygenGeneratorRating + " (decimal : " + oxygenGeneratorRatingDecimal + ")");
        System.out.println("CO2 Scrubber Rating : " + co2ScrubberRating + " (decimal : " + co2ScrubberRatingDecimal + ")");

        System.out.println("Multiplication : " + oxygenGeneratorRatingDecimal.multiply(co2ScrubberRatingDecimal));

    }

    private static List<String> getReport() {
        return Utils.readInput("/v2021/d3/input.txt");
    }

    public static class MostCommonBitRule implements Rule {

        private final Integer position;
        private final String reportName;
        private final String factNamePrefix;
        private int current = 0;
        private int sum = 0;

        public MostCommonBitRule(Integer position, String reportName, String factNamePrefix) {
            this.position = position;
            this.reportName = reportName;
            this.factNamePrefix = factNamePrefix;
        }

        public Integer getPosition() {
            return position;
        }

        public String getFactNamePrefix() {
            return factNamePrefix;
        }

        public String getReportName() {
            return reportName;
        }

        @Override
        public boolean evaluate(Facts facts) {
            List<String> report = facts.get(getReportName());
            return current < report.size();
        }

        @Override
        public void execute(Facts facts) {
            List<String> report = facts.get(getReportName());
            String line = report.get(current);
            int value = Integer.parseInt(line.charAt(position) + "");
            this.sum += value;
            this.current++;
            if (current == report.size()) {
                facts.add(new Fact<>(factNamePrefix + position, this.sum >= report.size() / 2. ? "1" : "0"));
            }
        }

        @Override
        public int compareTo(Rule o) {
            if (o instanceof MostCommonBitRule other) {
                Integer positionComparison = Math.abs(getPosition().compareTo(other.getPosition()));
                Integer factNamePrefixComparison = Math.abs(getFactNamePrefix().compareTo(other.getFactNamePrefix()));
                return positionComparison + factNamePrefixComparison;
            }
            return -1;
        }
    }

    public static class GammaRateRule implements Rule {

        @Override
        public boolean evaluate(Facts facts) {
            return facts.get("gamma-rate") == null && facts.asMap().keySet().containsAll(IntStream.range(0, 12)
                .mapToObj(i -> "common-bit-" + i)
                .collect(Collectors.toSet()));
        }

        @Override
        public void execute(Facts facts) throws Exception {
            IntStream.range(0, 12)
                .mapToObj(i -> "common-bit-" + i)
                .map(facts::get)
                .map(String.class::cast)
                .reduce((acc, v) -> acc + v)
                .ifPresent(gammaRate -> facts.add(new Fact<>("gamma-rate", gammaRate)));
        }

        @Override
        public int compareTo(Rule o) {
            return o instanceof GammaRateRule ? 0 : -1;
        }
    }

    public static class EpsilonRateRule implements Rule {

        @Override
        public boolean evaluate(Facts facts) {
            return facts.get("gamma-rate") != null && facts.get("epsilon-rate") == null;
        }

        @Override
        public void execute(Facts facts) throws Exception {
            BigInteger max = new BigInteger("111111111111", 2);
            BigInteger gammaRate = new BigInteger(facts.get("gamma-rate"), 2);
            String epsilonRate = String.format("%1$12s", max.xor(gammaRate).toString(2)).replace(' ', '0');
            facts.add(new Fact<>("epsilon-rate", epsilonRate));
        }

        @Override
        public int compareTo(Rule o) {
            return o instanceof EpsilonRateRule ? 0 : -1;
        }
    }

    public static class OxygenGeneratorRatingRule extends AirControlRule {

        public OxygenGeneratorRatingRule(Integer position) {
            super(position);
        }

        @Override
        boolean selectMostCommon() {
            return true;
        }

        @Override
        String getPrefix() {
            return "oxygen";
        }

        @Override
        public int compareTo(Rule o) {
            if (o instanceof OxygenGeneratorRatingRule other) {
                return getPosition().compareTo(other.getPosition());
            }
            return -1;
        }
    }

    public static class CO2ScrubberRatingRule extends AirControlRule {

        public CO2ScrubberRatingRule(Integer position) {
            super(position);
        }

        @Override
        boolean selectMostCommon() {
            return false;
        }

        @Override
        String getPrefix() {
            return "co2";
        }

        @Override
        public int compareTo(Rule o) {
            if (o instanceof CO2ScrubberRatingRule other) {
                return getPosition().compareTo(other.getPosition());
            }
            return -1;
        }
    }

    public abstract static class AirControlRule implements Rule {

        private final Integer position;
        private boolean fired = false;

        abstract boolean selectMostCommon();

        abstract String getPrefix();

        public AirControlRule(Integer position) {
            this.position = position;
        }

        public Integer getPosition() {
            return position;
        }

        @Override
        public boolean evaluate(Facts facts) {
            List<String> report = facts.get(getPrefix() + "-work-report");
            return facts.get(getPrefix() + "-common-bit-" + position) != null && report.size() > 1 && !fired;
        }

        @Override
        public void execute(Facts facts) {
            String commonBit = facts.get(getPrefix() + "-common-bit-" + position);
            List<String> report = facts.get(getPrefix() + "-work-report");
            if (selectMostCommon()) {
                report.removeIf(i -> !String.valueOf(i.charAt(position)).equals(commonBit));
            } else {
                report.removeIf(i -> String.valueOf(i.charAt(position)).equals(commonBit));
            }
            this.fired = true;
        }

    }

}
