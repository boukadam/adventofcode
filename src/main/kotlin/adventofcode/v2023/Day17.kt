package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import java.util.*
import kotlin.math.min

private data class State(val location: Pair<Int, Int>, val direction: Char, val steps: Int) {
    fun next(nextCoord: Pair<Int, Int>, nextDirection: Char): State =
        State(nextCoord(location, nextCoord), nextDirection, if (direction == nextDirection) steps + 1 else 1)
}

private data class Work(val state: State, val heatLoss: Int) : Comparable<Work> {
    override fun compareTo(other: Work): Int =
        heatLoss - other.heatLoss
}

private val directions = mapOf(
    '^' to "<>^",
    'v' to "<>v",
    '<' to "<v^",
    '>' to ">v^"
)

private val moves = mapOf(
    '^' to Pair(0, -1),
    'v' to Pair(0, 1),
    '<' to Pair(-1, 0),
    '>' to Pair(1, 0)
)

fun main() {

    fun part1(input: List<String>): Int {
        return compute(parse(input), 1) { state, nextDirection -> state.steps < 3 || state.direction != nextDirection }
    }

    fun part2(input: List<String>): Int {
        return compute(parse(input), 4) { state, nextDirection ->
            if (state.steps > 9) state.direction != nextDirection
            else if (state.steps < 4) state.direction == nextDirection
            else true
        }
    }

    val input = readInput("v2023/d17/input")

    part1(input).println()
    part2(input).println()
}

private fun compute(grid: List<List<Int>>, minSteps: Int, isValidNextMove: (State, Char) -> Boolean): Int {
    val finish = Pair(grid.first().lastIndex, grid.lastIndex)
    val seen = mutableSetOf<State>()
    val queue = PriorityQueue<Work>()

    State(Pair(0, 0), '>', 0).apply {
        queue += Work(this, 0)
        seen += this
    }

    while (queue.isNotEmpty()) {
        val (current, heatLoss) = queue.poll()
        if (current.location == finish && current.steps >= minSteps) {
            return heatLoss
        }

        directions
            .getValue(current.direction)
            .filter { direction ->
                val next = nextCoord(moves.getValue(direction), current.location)
                next.second in grid.indices && next.first in grid.first().indices
            }
            .filter { direction -> isValidNextMove(current, direction) }
            .map { direction -> current.next(moves.getValue(direction), direction) }
            .filterNot { state -> state in seen }
            .forEach { state ->
                queue += Work(state, heatLoss + grid[state.location.second][state.location.first])
                seen += state
            }
    }
    throw IllegalStateException("No route")
}

private fun nextCoord(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(p1.first + p2.first, p1.second + p2.second)
}

private fun parse(input: List<String>): List<List<Int>> {
    return input.map { row -> row.map { it.digitToInt() } }
}