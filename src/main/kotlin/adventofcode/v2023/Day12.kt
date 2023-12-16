package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

private val cache = mutableMapOf<String, Long>()

fun main() {

    fun part1(input: List<String>): Int {
        return parse(input).sumOf { row -> getArrangements(row) }
    }

    fun part2(input: List<String>): Long {
        return parse2(input).sumOf { row -> getArrangements2(row) }
    }

    val input = readInput("v2023/d12/input")

    part1(input).println()
    part2(input).println()
}

private fun getKey(string: String, numbers: List<Int>): String {
    return string + ":" + numbers.joinToString(",")
}

private fun getArrangements2(row: Pair<String, List<Int>>): Long {
    if (row.first.isEmpty()) {
        return when (row.second.size) {
            0 -> 1
            else -> 0
        }
    }
    if (row.second.isEmpty()) {
        return when (row.first.contains('#')) {
            true -> 0
            else -> 1
        }
    }

    val key = getKey(row.first, row.second)
    if (cache.containsKey(key)) {
        return cache.getValue(key)
    }

    var result = 0L
    val condition = row.first[0]
    if (condition == '.' || condition == '?') {
        result += getArrangements2(Pair(row.first.slice(1..<row.first.length), row.second));
    }

    val first = row.second[0]
    val rest = row.second.drop(1)
    if (condition == '#' || condition == '?') {
        if (
            first <= row.first.length &&
            !row.first.slice(0..<first).contains('.') &&
            (first == row.first.length || row.first[first] != '#')
        ) {
            result += getArrangements2(Pair(row.first.slice((first + 1)..<row.first.length), rest));
        }
    }
    cache[key] = result;
    return result
}

private fun getArrangements(row: Pair<String, List<Int>>): Int {
    val total = row.second.sum()
    val current = row.first.count { it == '#' }
    val missing = total - current

    val indexes = row.first.withIndex().filter { it.value == '?' }.map { it.index }

    return combinations(indexes, missing)
        .map { combination ->
            val arrangement = row.first.mapIndexed { i, c ->
                if (i in combination) {
                    '#'
                } else if (c == '?') {
                    '.'
                } else {
                    c
                }
            }.joinToString("") { it.toString() }

            arrangement.split(".").filter { it != "" }.map { it.length } == row.second
        }
        .filter { v -> v }
        .count()

}

fun combinations(list: List<Int>, length: Int): Sequence<List<Int>> {
    return sequence {
        val n = list.size
        val counters = Array(length) { it }
        val maxes = Array(length) { it + n - length }

        yield(counters.map { list[it] })
        while (true) {
            val lastNotAtMax = counters.indices.findLast { counter ->
                counters[counter] != maxes[counter]
            } ?: return@sequence

            counters[lastNotAtMax]++

            for (toUpdate in lastNotAtMax + 1 until length) {
                counters[toUpdate] = counters[toUpdate - 1] + 1
            }
            yield(counters.map { list[it] })
        }
    }
}

private fun parse(input: List<String>): List<Pair<String, List<Int>>> {
    return input.asSequence()
        .map { line -> line.split(" ") }
        .map { list -> Pair(list[0], list[1].split(",").map { s -> s.toInt() }.toList()) }
        .toList()
}

private fun parse2(input: List<String>): List<Pair<String, List<Int>>> {
    return input.asSequence()
        .map { line -> line.split(" ") }
        .map { list ->
            val ints = list[1].split(",").map { s -> s.toInt() }.toList()
            val newList = mutableListOf<Int>().apply {
                repeat(5) {
                    this.addAll(ints)
                }
            }
            Pair(List(5) {list[0]}.joinToString("?"), newList) }
        .toList()
}