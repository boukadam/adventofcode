package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

private enum class Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST
}

fun main() {

    fun part1(input: List<String>): Int {
        val loop = computeLoop(input)
        return loop.size / 2
    }

    fun part2(input: List<String>): Int {
        val verticalPipes = setOf('|', '7', 'F')
        val loop = computeLoop(input).associate { e -> Pair(e.second, e.third) to e.first }
        var count = 0
        for ((y, line) in input.withIndex()) {
            var doCount = 0
            for (x in line.indices) {
                val coord = Pair(x, y)
                val inLoop = loop.containsKey(coord)
                val isVertical = inLoop && verticalPipes.contains(loop.getValue(coord))
                if (inLoop && isVertical) {
                    doCount = (doCount + 1) % 2
                } else if (doCount > 0 && !inLoop) {
                    count++
                }
            }
        }
        return count
    }

    val input = readInput("v2023/d10/input")

    part1(input).println()
    part2(input).println()
}

private fun computeLoop(input: List<String>): List<Triple<Char, Int, Int>> {
    val start = getStart(input)
    val neighbors = mutableMapOf<Direction, Pair<Int, Int>>()
    if (listOf('-', '7', 'J').contains(input[start.second][start.first + 1])) {
        neighbors[Direction.EAST] = Pair(start.first + 1, start.second)
    }
    if (listOf('|', 'J', 'L').contains(input[start.second + 1][start.first])) {
        neighbors[Direction.SOUTH] = Pair(start.first, start.second + 1)
    }
    if (start.first > 0 && listOf('-', 'F', 'L').contains(input[start.second][start.first - 1])) {
        neighbors[Direction.WEST] = Pair(start.first - 1, start.second)
    }
    if (start.second > 0 && listOf('|', 'F', '7').contains(input[start.second - 1][start.first])) {
        neighbors[Direction.NORTH] = Pair(start.first, start.second - 1)
    }
    val possibleStarts = mutableMapOf<Char, Pair<Int, Int>>()
    if (neighbors.containsKey(Direction.EAST)) {
        if (neighbors.containsKey(Direction.NORTH)) {
            possibleStarts['L'] = neighbors.getValue(Direction.NORTH)
        }
        if (neighbors.containsKey(Direction.SOUTH)) {
            possibleStarts['F'] = neighbors.getValue(Direction.SOUTH)
        }
        if (neighbors.containsKey(Direction.WEST)) {
            possibleStarts['-'] = neighbors.getValue(Direction.WEST)
        }
    }
    if (neighbors.containsKey(Direction.WEST)) {
        if (neighbors.containsKey(Direction.NORTH)) {
            possibleStarts['J'] = neighbors.getValue(Direction.NORTH)
        }
        if (neighbors.containsKey(Direction.SOUTH)) {
            possibleStarts['7'] = neighbors.getValue(Direction.SOUTH)
        }
        if (neighbors.containsKey(Direction.EAST)) {
            possibleStarts['-'] = neighbors.getValue(Direction.EAST)
        }
    }
    if (neighbors.containsKey(Direction.NORTH)) {
        if (neighbors.containsKey(Direction.SOUTH)) {
            possibleStarts['|'] = neighbors.getValue(Direction.SOUTH)
        }
        if (neighbors.containsKey(Direction.WEST)) {
            possibleStarts['J'] = neighbors.getValue(Direction.WEST)
        }
        if (neighbors.containsKey(Direction.EAST)) {
            possibleStarts['L'] = neighbors.getValue(Direction.EAST)
        }
    }
    if (neighbors.containsKey(Direction.SOUTH)) {
        if (neighbors.containsKey(Direction.NORTH)) {
            possibleStarts['|'] = neighbors.getValue(Direction.NORTH)
        }
        if (neighbors.containsKey(Direction.WEST)) {
            possibleStarts['7'] = neighbors.getValue(Direction.WEST)
        }
        if (neighbors.containsKey(Direction.EAST)) {
            possibleStarts['F'] = neighbors.getValue(Direction.EAST)
        }
    }

    val result = mutableListOf<List<Triple<Char, Int, Int>>>()
    for (neighbor in possibleStarts) {
        var previous = start
        var current = neighbor.value
        var index = 1
        val loop = mutableListOf(Triple(neighbor.key, start.first, start.second))
        while (current != start) {
            val p = when (previous) {
                start -> neighbor.key
                else -> input[previous.second][previous.first]
            }
            val c = input[current.second][current.first]
            loop.add(Triple(c, current.first, current.second))
            val direction = when (c) {
                'F' -> when (p) {
                    'J' -> when {
                        current.second == previous.second && current.first < previous.first -> Direction.SOUTH
                        current.first == previous.first && current.second < previous.second -> Direction.EAST
                        else -> break
                    }
                    '7', '-' -> when {
                        current.second == previous.second && current.first < previous.first -> Direction.SOUTH
                        else -> break
                    }
                    '|', 'L' -> when {
                        current.first == previous.first && current.second < previous.second -> Direction.EAST
                        else -> break
                    }
                    else -> break
                }

                'L' -> when (p) {
                    '7' -> when {
                        current.second == previous.second && current.first < previous.first -> Direction.NORTH
                        current.first == previous.first && current.second > previous.second -> Direction.EAST
                        else -> break
                    }
                    'J', '-' -> when {
                        current.second == previous.second && current.first < previous.first -> Direction.NORTH
                        else -> break
                    }
                    'F', '|' -> when {
                        current.first == previous.first && current.second > previous.second -> Direction.EAST
                        else -> break
                    }
                    else -> break
                }

                'J' -> when (p) {
                    'F' -> when {
                        current.second == previous.second && current.first > previous.first -> Direction.NORTH
                        current.first == previous.first && current.second > previous.second -> Direction.WEST
                        else -> break
                    }
                    '7', '|' -> when {
                        current.first == previous.first && current.second > previous.second -> Direction.WEST
                        else -> break
                    }
                    'L', '-' -> when {
                        current.second == previous.second && current.first > previous.first -> Direction.NORTH
                        else -> break
                    }
                    else -> break
                }

                '7' -> when (p) {
                    'L' -> when {
                        current.second == previous.second && current.first > previous.first -> Direction.SOUTH
                        current.first == previous.first && current.second < previous.second -> Direction.WEST
                        else -> break
                    }
                    'F', '-' -> when {
                        current.second == previous.second && current.first > previous.first -> Direction.SOUTH
                        else -> break
                    }
                    'J', '|' -> when {
                        current.first == previous.first && current.second < previous.second -> Direction.WEST
                        else -> break
                    }
                    else -> break
                }

                '|' -> when (p) {
                    '|' -> when {
                        current.first == previous.first && current.second < previous.second -> Direction.NORTH
                        current.first == previous.first && current.second > previous.second -> Direction.SOUTH
                        else -> break
                    }
                    'L', 'J' -> when {
                        current.first == previous.first && current.second < previous.second -> Direction.NORTH
                        else -> break
                    }
                    'F', '7' -> when {
                        current.first == previous.first && current.second > previous.second -> Direction.SOUTH
                        else -> break
                    }
                    else -> break
                }

                '-' -> when (p) {
                    '-' -> when {
                        current.second == previous.second && current.first > previous.first -> Direction.EAST
                        current.second == previous.second && current.first < previous.first -> Direction.WEST
                        else -> break
                    }
                    'L', 'F' -> when {
                        current.second == previous.second && current.first > previous.first -> Direction.EAST
                        else -> break
                    }
                    'J', '7' -> when {
                        current.second == previous.second && current.first < previous.first -> Direction.WEST
                        else -> break
                    }
                    else -> break
                }

                else -> break
            }
            previous = current
            current = when (direction) {
                Direction.NORTH -> Pair(previous.first, previous.second - 1)
                Direction.SOUTH -> Pair(previous.first, previous.second + 1)
                Direction.WEST -> Pair(previous.first - 1, previous.second)
                Direction.EAST -> Pair(previous.first + 1, previous.second)
            }
            index++
        }
        result.add(loop)
    }

    return result.maxBy { it.size }
}

private fun getStart(input: List<String>): Pair<Int, Int> {
    for ((y, line) in input.withIndex()) {
        if (line.contains("S")) {
            return Pair(line.indexOf("S"), y)
        }
    }
    throw RuntimeException()
}