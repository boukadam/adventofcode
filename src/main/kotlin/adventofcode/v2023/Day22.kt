package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

private data class Brick(
    val id: Int,
    val size: Triple<Int, Int, Int>,
    var position: Triple<Int, Int, Int>,
    var canBeDisintegrated: Boolean = true
)

fun main() {

    val input = readInput("v2023/d22/example")
    val map = parseGrid(input)
    val bricks = parseBricks(input)
    val xs = map.keys.maxOf { t -> t.first }
    val ys = map.keys.maxOf { t -> t.second }
    val zs = map.keys.maxOf { t -> t.third }
    val grid = Array(zs + 1) { Array(ys + 1) { Array(xs + 1) { 0 } } }
    map.forEach { (k, v) -> grid[k.third][k.second][k.first] = v }
    var change: Boolean
    do {
        change = false
        for (brick in bricks) {
            change = change || move(brick, grid)
        }
    } while (change)
    val (supports, supportedBy) = whoIsSupportingWho(grid, bricks)

    fun part1(): Int {
        var count = 0
        for (brick in bricks) {
            val brickSupporting = supports.getValue(brick.id)
            if (brickSupporting.isNotEmpty()) {
                for (supportedBrick in brickSupporting) {
                    if (supportedBy.getValue(supportedBrick).size < 2) {
                        brick.canBeDisintegrated = false
                        break
                    }
                }
            }
            if (brick.canBeDisintegrated) {
                count++
            }
        }

        return count
    }
    
    fun part2(): Int {
        // KO, solved by python script in resources
        val bricksMap = bricks.associateBy { it.id }
        return bricks
            .filter { b -> !b.canBeDisintegrated }
            .map { b -> computeFall(b, setOf(b.id), bricksMap, supports, supportedBy) }
            .sumOf { l -> l.size }
    }

    part1().println()
    part2().println()
}

private fun computeFall(
    brick: Brick,
    neighbors: Set<Int>,
    bricks: Map<Int, Brick>,
    supports: MutableMap<Int, MutableSet<Int>>,
    supportedBy: MutableMap<Int, MutableSet<Int>>
): Set<Int> {
    val brickSupports = supports.getValue(brick.id)
    val relevant = brickSupports.filter { s -> neighbors.containsAll(supportedBy.getValue(s)) }.toMutableSet()
    val result = relevant.toMutableSet()
    relevant.forEach { s -> result.addAll(computeFall(bricks.getValue(s), setOf(brick.id) + relevant + neighbors, bricks, supports, supportedBy)) }
    return result
}

private fun whoIsSupportingWho(
    grid: Array<Array<Array<Int>>>,
    bricks: List<Brick>
): Pair<MutableMap<Int, MutableSet<Int>>, MutableMap<Int, MutableSet<Int>>> {
    val supported = mutableMapOf<Int, MutableSet<Int>>()
    val supporting = mutableMapOf<Int, MutableSet<Int>>()
    bricks.forEach { b ->
        supported[b.id] = mutableSetOf()
        supporting[b.id] = mutableSetOf()
    }
    for (brick in bricks) {
        for (y in brick.position.second..brick.position.second + brick.size.second) {
            for (x in brick.position.first..brick.position.first + brick.size.first) {
                val v = grid[brick.position.third + brick.size.third + 1][y][x]
                if (v > 0) {
                    supported.getValue(brick.id).add(v)
                    supporting.getValue(v).add(brick.id)
                }
            }
        }
    }
    return Pair(supported, supporting)
}

private fun parseBricks(input: List<String>): List<Brick> {
    return input
        .map { line -> line.split('~') }
        .mapIndexed { i, split ->
            val (x1, y1, z1) = split[0].split(',').map { e -> e.toInt() }
            val (x2, y2, z2) = split[1].split(',').map { e -> e.toInt() }
            Brick(i + 1, Triple(x2 - x1, y2 - y1, z2 - z1), Triple(x1, y1, z1 - 1))
        }
}

private fun move(brick: Brick, grid: Array<Array<Array<Int>>>): Boolean {
    if (brick.position.third == 0) {
        return false
    }
    for (y in brick.position.second..brick.position.second + brick.size.second) {
        for (x in brick.position.first..brick.position.first + brick.size.first) {
            if (grid[brick.position.third - 1][y][x] > 0) {
                return false
            }
        }
    }
    for (y in brick.position.second..brick.position.second + brick.size.second) {
        for (x in brick.position.first..brick.position.first + brick.size.first) {
            grid[brick.position.third - 1][y][x] = brick.id
            grid[brick.position.third + brick.size.third][y][x] = 0
        }
    }
    brick.position = Triple(brick.position.first, brick.position.second, brick.position.third - 1)
    return true
}

private fun parseGrid(input: List<String>): Map<Triple<Int, Int, Int>, Int> {
    return input.asSequence()
        .map { line -> line.split('~') }
        .flatMapIndexed { i, split ->
            val (x1, y1, z1) = split[0].split(',').map { e -> e.toInt() }
            val (x2, y2, z2) = split[1].split(',').map { e -> e.toInt() }
            val list = mutableListOf<Pair<Triple<Int, Int, Int>, Int>>()
            for (z in z1..z2) {
                for (y in y1..y2) {
                    for (x in x1..x2) {
                        list.add(Triple(x, y, z - 1) to i + 1)
                    }
                }
            }
            return@flatMapIndexed list.asSequence()
        }
        .toMap()
}

private fun print(grid: Array<Array<Array<Int>>>) {
    for (z in grid.indices.reversed()) {
        for (x in grid[z][0].indices) {
            print(printTile(grid[z][0][x]))
        }
        print("  ")
        for (x in grid[z][0].indices) {
            print(printTile(grid[z][1][x]))
        }
        print("  ")
        for (x in grid[z][0].indices) {
            print(printTile(grid[z][grid[z].size - 1][x]))
        }
        println()
    }
    println()
    for (z in grid.indices.reversed()) {
        for (y in grid[z].indices) {
            print(printTile(grid[z][y][0]))
        }
        print("  ")
        for (y in grid[z].indices) {
            print(printTile(grid[z][y][1]))
        }
        print("  ")
        for (y in grid[z].indices) {
            print(printTile(grid[z][y][grid[z][y].size - 1]))
        }
        println()
    }
}

private fun printTile(tile: Int): Char {
    return when (tile) {
        0 -> '.'
        else -> tile.toString()[0]
    }
}