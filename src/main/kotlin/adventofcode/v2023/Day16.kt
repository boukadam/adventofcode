package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

private data class Tile(val x: Int, val y: Int, val symbol: Char, val incoming: MutableList<Pair<Int, Int>>)

fun main() {

    fun part1(input: List<String>): Int {
        val grid = parse(input)
        return compute(grid, Triple(0, 0, '>'))
    }

    fun part2(input: List<String>): Int {

        val zeros = IntArray(input.size)
        val ends = IntArray(input.size) { input.size - 1 }
        val range = IntArray(input.size) { it + 1 }
        val values = mutableListOf<Int>()
        for (direction in "<>v^") {
            val loop = when (direction) {
                '<' -> ends.zip(range)
                '>' -> zeros.zip(range)
                'v' -> range.zip(zeros)
                '^' -> range.zip(ends)
                else -> error("")
            }
            for ((i, j) in loop) {
                val grid = parse(input)
                values.add(compute(grid, Triple(i, j, direction)))
            }
        }

        return values.max()
    }

    val input = readInput("v2023/d16/input")

    part1(input).println()
    part2(input).println()
}

private fun compute(grid: Map<Int, Map<Int, Tile>>, start: Triple<Int, Int, Char>): Int {
    val previousTile = when (start.third) {
        '>' -> Pair(start.first - 1, start.second)
        '<' -> Pair(start.first + 1, start.second)
        'v' -> Pair(start.first, start.second - 1)
        '^' -> Pair(start.first, start.second + 1)
        else -> error("")
    }
    var lights = listOf(Pair(Tile(previousTile.first, previousTile.second, '.', mutableListOf()), start))
    while (lights.isNotEmpty()) {
        lights = lights.flatMap { pair -> visit(pair.first, pair.second, grid) }
    }
    return grid.values.asSequence()
        .flatMap { line ->
            line.values.asSequence().map { tile ->
                when (tile.incoming.isEmpty()) {
                    true -> 0
                    false -> 1
                }
            }
        }
        .sum()
}

private fun visit(previous: Tile, light: Triple<Int, Int, Char>, grid: Map<Int, Map<Int, Tile>>): List<Pair<Tile, Triple<Int, Int, Char>>> {
    var nextLight = light
    val branches = mutableListOf<Pair<Tile, Triple<Int, Int, Char>>>()
    var previousTile = previous
    while (nextLight.first >= 0 && nextLight.second >= 0
        && nextLight.first < grid.size && nextLight.second < grid.size
    ) {
        val tile = grid[nextLight.second]?.get(nextLight.first)!!
        val previousCoordinate = Pair(previousTile.x, previousTile.y)
        if (tile.incoming.contains(previousCoordinate)) {
            break
        }
        tile.incoming.add(previousCoordinate)
        val next = getNext(nextLight, tile)
        nextLight = next[0]
        next.drop(1).forEach { l -> branches.add(Pair(tile, l)) }
        previousTile = tile
    }
    return branches
}

private fun getNext(light: Triple<Int, Int, Char>, tile: Tile): List<Triple<Int, Int, Char>> {
    return when (light.third) {
        '>' -> when (tile.symbol) {
            '-', '.' -> listOf(Triple(light.first + 1, light.second, '>'))
            '|' -> {
                listOf(Triple(light.first, light.second - 1, '^'), Triple(light.first, light.second + 1, 'v'))
            }

            '/' -> listOf(Triple(light.first, light.second - 1, '^'))
            '\\' -> listOf(Triple(light.first, light.second + 1, 'v'))
            else -> listOf()
        }

        '<' -> when (tile.symbol) {
            '-', '.' -> listOf(Triple(light.first - 1, light.second, '<'))
            '|' -> {
                listOf(Triple(light.first, light.second - 1, '^'), Triple(light.first, light.second + 1, 'v'))
            }

            '/' -> listOf(Triple(light.first, light.second + 1, 'v'))
            '\\' -> listOf(Triple(light.first, light.second - 1, '^'))
            else -> listOf()
        }

        'v' -> when (tile.symbol) {
            '|', '.' -> listOf(Triple(light.first, light.second + 1, 'v'))
            '-' -> {
                listOf(Triple(light.first - 1, light.second, '<'), Triple(light.first + 1, light.second, '>'))
            }

            '/' -> listOf(Triple(light.first - 1, light.second, '<'))
            '\\' -> listOf(Triple(light.first + 1, light.second, '>'))
            else -> listOf()
        }

        '^' -> when (tile.symbol) {
            '|', '.' -> listOf(Triple(light.first, light.second - 1, '^'))
            '-' -> {
                listOf(Triple(light.first - 1, light.second, '<'), Triple(light.first + 1, light.second, '>'))
            }

            '/' -> listOf(Triple(light.first + 1, light.second, '>'))
            '\\' -> listOf(Triple(light.first - 1, light.second, '<'))
            else -> listOf()
        }

        else -> listOf()
    }
}

private fun parse(input: List<String>): Map<Int, Map<Int, Tile>> {
    val map = mutableMapOf<Int, Map<Int, Tile>>()
    input.forEachIndexed { y, line ->
        val lineMap = mutableMapOf<Int, Tile>()
        line.forEachIndexed { x, c -> lineMap[x] = Tile(x, y, c, mutableListOf()) }
        map[y] = lineMap
    }
    return map
}