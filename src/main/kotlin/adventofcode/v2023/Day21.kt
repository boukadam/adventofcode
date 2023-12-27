package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import kotlinx.coroutines.*
import java.math.BigInteger
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedDeque

private val directions = listOf(Pair(0, -1), Pair(0, 1), Pair(-1, 0), Pair(1, 0))
fun main() {

    fun part1(input: List<String>): Int {
        return doRepeat(input, 64) // or parse(input).values.count { v -> v % 2 == 0 && v <= 64}
    }

    fun part2(input: List<String>): Long {
        val map = parse(input)
        val evenCorners = map.values.count { v -> v % 2 == 0 && v > 65 }
        val oddCorners = map.values.count { v -> v % 2 == 1 && v > 65 }

        val n = ((26501365L - (input.size / 2)) / input.size)
        val even = n * n;
        val odd = (n + 1) * (n + 1);
        return odd * map.values.count { v -> v % 2 == 1 } + even * map.values.count { v -> v % 2 == 0 } - ((n + 1) * oddCorners) + (n * evenCorners)
    }

    val input = readInput("v2023/d21/input")

    part1(input).println()
    part2(input).println()
}

private fun parse(input: List<String>): Map<Pair<Int, Int>, Int> {
    val stack: Queue<Pair<Pair<Int, Int>, Int>> = ConcurrentLinkedDeque()
    stack.add(Pair(findStart(input), 0))
    val map = mutableMapOf<Pair<Int, Int>, Int>()
    while (stack.isNotEmpty()) {
        val (coord, dist) = stack.remove()
        if (map.containsKey(coord)) {
            continue
        }
        map[coord] = dist
        for (direction in directions) {
            val (_, next) = nextCoord(coord, direction, input.size)
            if (!map.containsKey(next) && input[next.second][next.first] != '#') {
                stack.add(Pair(next, dist + 1))
            }
        }
    }
    return map
}

private fun doRepeat(input: List<String>, count: Int): Int {
    val grid = input.map { line -> line.replace('S', '.') }
    var positions = setOf(findStart(input))
    repeat(count) {
        positions = compute(grid, positions)
    }
    for (y in input.indices) {
        for (x in input[y].indices) {
            if (positions.contains(Pair(x, y))) {
                print('O')
            } else {
                print(input[y][x])
            }
        }
        println()
    }
    return positions.size
}

private fun compute(grid: List<String>, positions: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
    val nextPositions = mutableSetOf<Pair<Int, Int>>()
    for (position in positions) {
        for (direction in directions) {
            val (original, next) = nextCoord(position, direction, grid.size)
            if (grid[next.second][next.first] == '.') {
                nextPositions.add(original)
            }
        }
    }
    return nextPositions
}

private fun nextCoord(p1: Pair<Int, Int>, p2: Pair<Int, Int>, size: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    val original = Pair(p1.first + p2.first, p1.second + p2.second)
    val x = BigInteger.valueOf(original.first.toLong()).mod(BigInteger.valueOf(size.toLong()))
    val y = BigInteger.valueOf(original.second.toLong()).mod(BigInteger.valueOf(size.toLong()))
    val relocated = Pair(x.toInt(), y.toInt())
    return Pair(original, relocated)
}

private fun findStart(input: List<String>): Pair<Int, Int> {
    for (y in input.indices) {
        for (x in input[y].indices) {
            if (input[y][x] == 'S') {
                return Pair(x, y)
            }
        }
    }
    error("")
}