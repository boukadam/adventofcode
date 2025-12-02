package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

private val directions = listOf(Pair(0, -1), Pair(0, 1), Pair(-1, 0), Pair(1, 0))

fun main() {

    fun part1(input: List<String>): Int {
        val graph = parse(input)
        val start = Pair(1, 0)
        val end = Pair(input.size - 1, input[0].length - 2)

        var (path, others) = walk(start, end, graph)
        var paths = mutableSetOf(path)
        val stack: Queue<Pair<List<Pair<Int, Int>>, Pair<Int, Int>>> = ConcurrentLinkedDeque()
        others.flatMap { (k, v) -> v.map { n -> Pair(k, n) } }.forEach { s -> stack.add(s) }
        while (stack.isNotEmpty()) {
            val (subPath, next) = stack.remove()
            val (p, o) = walk(next, end, graph)
            paths.add(subPath + p)
            o.flatMap { (k, v) -> v.map { n -> Pair(k, n) } }.forEach { s -> stack.add(s) }
        }

        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val input = readInput("v2023/d23/example")

    part1(input).println()
    part2(input).println()
}

private fun walk(
    start: Pair<Int, Int>,
    end: Pair<Int, Int>,
    graph: Map<Pair<Int, Int>, List<Pair<Int, Int>>>
): Pair<List<Pair<Int, Int>>, Map<List<Pair<Int, Int>>, Set<Pair<Int, Int>>>> {
    var current = start
    val path = mutableListOf<Pair<Int, Int>>()
    val map = mutableMapOf<List<Pair<Int, Int>>, Set<Pair<Int, Int>>>()
    var i = 0
    while (current != end) {
        val nexts = graph.getValue(current)
        current = nexts.first()
        if (path.contains(current)) {
            return Pair(listOf(), map)
        }
        i++
        path.add(current)
        if (nexts.size > 1) {
            map[path.toList()] = nexts.drop(1).toSet()
        }
    }
    return Pair(path, map)
}

private fun walk(
    start: Pair<Int, Int>,
    end: Pair<Int, Int>,
    graph: Map<Pair<Int, Int>, Set<Pair<Int, Int>>>,
    path: MutableSet<Pair<Int, Int>>
): MutableList<MutableSet<Pair<Int, Int>>> {
    var current = start
    val paths = mutableListOf(path)
    while (current != end) {
        val nexts = graph.getValue(current)
        if (nexts.size == 1) {
            current = nexts.iterator().next()
            path.add(current)
        } else {
            for ((i, next) in nexts.withIndex()) {
                if (paths.any { p -> p.contains(next) }) {
                    continue
                }
                if (i == 0) {
                    walk(next, end, graph, path)
                } else {
                    val newPath = path.toMutableSet()
                    paths.add(newPath)
                    walk(next, end, graph, newPath)
                }
            }
        }
    }
    return paths
}

private fun parse(input: List<String>): Map<Pair<Int, Int>, List<Pair<Int, Int>>> {
    val start = Pair(1, 0)
    val map = mutableMapOf(start to mutableListOf(Pair(1, 1)))
    for (y in 1..<input.size - 1) {
        for (x in 1..<input[0].length - 1) {
            if (input[y][x] == '#') {
                continue
            }
            val current = Pair(x, y)
            map[current] = mutableListOf()
            for (direction in directions) {
                val next = nextCoord(current, direction)
                val tile = input[next.second][next.first]
                if (tile == '#'
                    || (direction.first == 1 && !">.".contains(tile))
                    || (direction.first == -1 && !"<.".contains(tile))
                    || (direction.second == 1 && !"v.".contains(tile))
                    || (direction.second == -1 && !"^.".contains(tile))
                ) {
                    continue
                }
                map.getValue(current).add(next)
            }
        }
    }
    return map
}

private fun nextCoord(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(p1.first + p2.first, p1.second + p2.second)
}