package adventofcode.v2022;

import adventofcode.Utils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day7 {

    private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\$\\s(cd|ls)(?:\\s(.*))?$");
    private static final Pattern DIR_PRINT_PATTERN = Pattern.compile("^dir\\s(.+)$");
    private static final Pattern FILE_PRINT_PATTERN = Pattern.compile("^(\\d+)\\s(.+)$");


    public static void main(String[] args) {
        Utils.exec(args, Day7::part1, Day7::part2);
    }

    public static void part1() {
        Directory root = buildTree();
        List<Directory> directories = filter(root, null, 100_000);
        int sum = directories.stream()
            .mapToInt(Item::size)
            .sum();
        System.out.println("Sum : " + sum);
    }

    public static void part2() {
        Directory root = buildTree();
        int freeSpace = 70_000_000 - root.size();
        int spaceRequired = 30_000_000 - freeSpace;
        if (spaceRequired < 0) {
            System.out.println("There is enough free space");
            return;
        }

        List<Directory> directories = filter(root, spaceRequired, null);
        directories.stream()
            .min(Comparator.comparing(Directory::size))
            .ifPresent(directory -> System.out.println("Smallest directory to delete : " + directory));
    }

    static List<Directory> filter(Directory directory, Integer minSize, Integer maxSize) {
        List<Directory> directories = CollectionUtils.emptyIfNull(directory.children()).stream()
            .filter(Directory.class::isInstance)
            .map(Directory.class::cast)
            .flatMap(child -> filter(child, minSize, maxSize).stream())
            .filter(child -> minSize == null || child.size() >= minSize)
            .filter(child -> maxSize == null || child.size() <= maxSize)
            .collect(Collectors.toList());
        if ((minSize == null || directory.size() >= minSize) && (maxSize == null || directory.size() <= maxSize)) {
            directories.add(directory);
        }
        return directories;
    }

    static Directory buildTree() {
        Directory current = new Directory("/", null, new ArrayList<>());
        for (Line line : readLines()) {
            if (line instanceof Command command) {
                if (command.action() == Command.Action.LS) {
                    continue;
                }
                if (command.action() == Command.Action.CD) {
                    current = handleCdCommand(current, command);
                }
            } else if (line instanceof Print print) {
                handlePrintLine(current, print);
            }
        }
        return getRoot(current);
    }

    private static void handlePrintLine(Directory current, Print print) {
        Item existingChild = current.children().stream()
            .filter(child -> child.name().equals(print.name()))
            .findFirst()
            .orElse(null);
        if (existingChild == null) {
            if (print.type() == Print.Type.DIRECTORY) {
                current.addChild(new Directory(print.name(), current, new ArrayList<>()));
            } else if (print.type() == Print.Type.FILE) {
                current.addChild(new File(print.name(), print.size()));
            }
        }
    }

    private static Directory handleCdCommand(Directory current, Command command) {
        if (command.dir().equals("..")) {
            current = current.parent();
        } else {
            Directory subDirectory = current.children().stream()
                .filter(Directory.class::isInstance)
                .map(Directory.class::cast)
                .filter(child -> child.name().equals(command.dir()))
                .findFirst()
                .orElse(null);

            if (subDirectory == null && !command.dir().equals("/")) {
                subDirectory = new Directory(command.dir(), current, new ArrayList<>());
                current.addChild(subDirectory);
            }
            if (subDirectory != null) {
                current = subDirectory;
            }
        }
        return current;
    }

    static Directory getRoot(Directory directory) {
        if (directory.parent() == null) {
            return directory;
        }
        return getRoot(directory.parent());
    }

    static List<? extends Line> readLines() {
        return Utils.readInput("/v2022/d7/input.txt").stream()
            .map(line -> {
                Matcher commandMatcher = COMMAND_PATTERN.matcher(line);
                if (commandMatcher.matches()) {
                    return new Command(Command.Action.valueOf(commandMatcher.group(1).toUpperCase()), commandMatcher.group(2));
                }
                Matcher dirPrintMatcher = DIR_PRINT_PATTERN.matcher(line);
                if (dirPrintMatcher.matches()) {
                    return new Print(Print.Type.DIRECTORY, dirPrintMatcher.group(1), null);
                }
                Matcher filePrintMatcher = FILE_PRINT_PATTERN.matcher(line);
                if (filePrintMatcher.matches()) {
                    return new Print(Print.Type.FILE, filePrintMatcher.group(2), Integer.parseInt(filePrintMatcher.group(1)));
                }
                return null;
            })
            .filter(Objects::nonNull)
            .toList();
    }

    interface Line {

    }

    record Command(Action action, String dir) implements Line {

        enum Action {
            CD, LS
        }

    }

    record Print(Type type, String name, Integer size) implements Line {

        enum Type {
            FILE, DIRECTORY
        }

    }

    interface Item {

        String name();

        Integer size();

    }

    record File(String name, Integer size) implements Item {

    }

    record Directory(String name, Directory parent, List<Item> children) implements Item {

        void addChild(Item child) {
            children.add(child);
        }

        @Override
        public Integer size() {
            return children.stream().mapToInt(Item::size).sum();
        }

        @Override
        public String toString() {
            return "Directory{" +
                "name='" + name + '\'' +
                "size='" + size() + '\'' +
                '}';
        }
    }

}
