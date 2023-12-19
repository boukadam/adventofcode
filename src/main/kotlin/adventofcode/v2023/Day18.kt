package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

fun main() {

    fun part1(input: List<String>): Int {
        val grid = buildGridWithBorders(input)
        val list = convert(grid)
        val filled = fill(list)

        var count = 0
        for (line in filled) {
            count += line.count { c -> c == '#' }
        }
        return count
    }

    fun part2(input: List<String>): Long {
        // works also for part1
        val directions = mapOf(Pair('R', Pair(0, 1)), Pair('D', Pair(1, 0)), Pair('L', Pair(0, -1)), Pair('U', Pair(-1, 0)))
        var x = 0L
        var y = 0L
        var perimeter = 0L
        var area = 0L
        for (step in getSteps(input)) {
            val (direction, count) = step
            var (dy, dx) = directions.getValue(direction)
            dy *= count
            dx *= count
            y += dy
            x += dx
            perimeter += count
            area += x * dy
        }

        return area+perimeter/2+1
    }

    val input = readInput("v2023/d18/input")

    part1(input).println()
    part2(input).println()
}

private fun fill(input: List<String>): List<String> {
    val verticalPipes = setOf('|', '7', 'F')
    var count = 0
    val newList = mutableListOf<String>()
    for (line in input) {
        var doCount = 0
        val lineBuilder = StringBuilder()
        for (x in line.indices) {
            val isVertical = verticalPipes.contains(line[x])
            if (isVertical) {
                lineBuilder.append('#')
                doCount = (doCount + 1) % 2
            } else if (doCount > 0) {
                lineBuilder.append('#')
                count++
            } else {
                lineBuilder.append(line[x])
            }
        }
        newList.add(lineBuilder.toString().replace('-', '#')
            .replace('J', '#')
            .replace('L', '#')
        )
    }
    return newList;
}

private fun convert(grid: MutableMap<Pair<Int, Int>, Char>): List<String> {
    val minRows = grid.keys.minOf { p -> p.second }
    val maxRows = grid.keys.maxOf { p -> p.second }
    val minColumns = grid.keys.minOf { p -> p.first }
    val maxColumns = grid.keys.maxOf { p -> p.first }
    val list = mutableListOf<String>()
    for (r in minRows..maxRows) {
        val builder = StringBuilder()
        for (c in minColumns..maxColumns) {
            val key = Pair(c, r)
            if (grid.containsKey(key)) {
                when {
                    grid.containsKey(Pair(key.first + 1, key.second)) && grid.containsKey(Pair(key.first, key.second + 1)) -> {
                        builder.append('F');
                    }
                    grid.containsKey(Pair(key.first + 1, key.second)) && grid.containsKey(Pair(key.first, key.second - 1)) -> {
                        builder.append('L')
                    }
                    grid.containsKey(Pair(key.first, key.second + 1)) && grid.containsKey(Pair(key.first - 1, key.second)) -> {
                        builder.append('7')
                    }
                    grid.containsKey(Pair(key.first - 1, key.second)) && grid.containsKey(Pair(key.first, key.second - 1)) -> {
                        builder.append('J')
                    }
                    grid.containsKey(Pair(key.first - 1, key.second)) && grid.containsKey(Pair(key.first + 1, key.second)) -> {
                        builder.append('-')
                    }
                    grid.containsKey(Pair(key.first, key.second - 1)) && grid.containsKey(Pair(key.first, key.second + 1)) -> {
                        builder.append('|')
                    }
                }
            } else {
                builder.append('.')
            }
        }
        list.add(builder.toString())
    }
    return list
}

private fun buildGridWithBorders(input: List<String>): MutableMap<Pair<Int, Int>, Char> {
    val grid = mutableMapOf(Pair(Pair(0, 0), '-'))
    var currentPosition = Pair(0, 0)
    val regex = Regex("^([RLUD])\\s(\\d+)\\s(.*)\$")
    for (line in input) {
        val destructured = regex.find(line)?.destructured
        val direction = destructured?.component1()!!
        val count = destructured.component2().toInt()
        currentPosition = getNextPosition(direction, count, currentPosition, grid)
    }
    return grid
}

private fun getNextPosition(direction: String, count: Int, currentPosition: Pair<Int, Int>, grid: MutableMap<Pair<Int, Int>, Char>): Pair<Int, Int> {
    return when (direction) {
        "R" -> {
            "#".repeat(count).forEachIndexed { i, c -> grid[Pair(currentPosition.first + i + 1, currentPosition.second)] = c }
            Pair(currentPosition.first + count, currentPosition.second)
        }

        "L" -> {
            "#".repeat(count).forEachIndexed { i, c -> grid[Pair(currentPosition.first - i - 1, currentPosition.second)] = c }
            Pair(currentPosition.first - count, currentPosition.second)
        }

        "U" -> {
            "#".repeat(count).forEachIndexed { i, c -> grid[Pair(currentPosition.first, currentPosition.second - i - 1)] = c }
            Pair(currentPosition.first, currentPosition.second - count)
        }

        "D" -> {
            "#".repeat(count).forEachIndexed { i, c -> grid[Pair(currentPosition.first, currentPosition.second + i + 1)] = c }
            Pair(currentPosition.first, currentPosition.second + count)
        }

        else -> error("")
    }
}

private fun getSteps(input: List<String>): List<Pair<Char, Int>> {
    val list = mutableListOf<Pair<Char, Int>>()
    val regex = Regex("^.*\\(#(.{5})(.)\\)\$")
    for (line in input) {
        val destructured = regex.find(line)?.destructured
        val count = Integer.valueOf(destructured?.component1(), 16)
        val direction = when (destructured?.component2()) {
            "0" -> 'R'
            "1" -> 'D'
            "2" -> 'L'
            "3" -> 'U'
            else -> error("")
        }
        list.add(Pair(direction, count))
    }
    return list
}