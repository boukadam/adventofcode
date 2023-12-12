package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

fun main() {

    fun part1(input: List<String>): Long {
        val expanded = expand(input)
        val galaxies = parseGalaxies(expanded)
        val couples = buildCouples(galaxies)
        return couples.asSequence()
            .map(::distance)
            .sum()
    }

    fun part2(input: List<String>): Long {
        val galaxies = parseGalaxiesPart2(input)
        val couples = buildCouples(galaxies)
        return couples.asSequence()
            .map(::distance)
            .sum()
    }

    val input = readInput("v2023/d11/input")

    part1(input).println()
    part2(input).println()
}

private fun distance(couple: Pair<Pair<Long, Long>, Pair<Long, Long>>): Long {
    val diffX = maxOf(couple.first.first, couple.second.first) - minOf(couple.first.first, couple.second.first)
    val diffY = maxOf(couple.first.second, couple.second.second) - minOf(couple.first.second, couple.second.second)
    if (diffX == 0L) {
        return diffY
    }
    if (diffY == 0L) {
        return diffX
    }
    val square = minOf(diffX, diffY)
    return ((square * 2) + (maxOf(diffX, diffY) - square))
}

private fun buildCouples(galaxies: List<Pair<Long, Long>>): List<Pair<Pair<Long, Long>, Pair<Long, Long>>> {

    val set = mutableSetOf<Set<Pair<Long, Long>>>()
    for (g1 in galaxies) {
        for (g2 in galaxies) {
            set.add(mutableSetOf(g1, g2))
        }
    }
    return set.asSequence()
        .filter { list -> list.size > 1 }
        .map { it.toList().let { it[0] to it[1] } }.toList()
}

private fun parseGalaxiesPart2(input: List<String>): List<Pair<Long, Long>> {
    val times = 1_000_000
    var emptyColumns = IntRange(0, input.size).asSequence().map { i -> i to true }.toMap()
    val emptyRows = IntRange(0, input.size - 1).asSequence().map { i -> i to false }.toMap().toMutableMap()
    for ((lineNumber, line) in input.withIndex()) {
        if (line.contains(".".repeat(line.length))) {
            emptyRows[lineNumber] = true
        }
        emptyColumns = line.asSequence().mapIndexed { index, c -> index to ((c == '.') && emptyColumns.getValue(index)) }.toMap()
    }
    val galaxies = parseGalaxies(input)
    val newGalaxies = mutableListOf<Pair<Long, Long>>()
    galaxies.forEach { galaxy ->
        val nbEmptyColumnsBefore = emptyColumns.count { e -> e.value && e.key < galaxy.first }
        val nbEmptyRowsBefore = emptyRows.count { e -> e.value && e.key < galaxy.second }
        newGalaxies.add(Pair(galaxy.first - nbEmptyColumnsBefore + nbEmptyColumnsBefore * times,
            galaxy.second - nbEmptyRowsBefore + nbEmptyRowsBefore * times))
    }
    return newGalaxies
}

private fun parseGalaxies(input: List<String>): List<Pair<Long, Long>> {
    val galaxies = mutableListOf<Pair<Long, Long>>()
    for ((y, line) in input.withIndex()) {
        for ((x, value) in line.withIndex()) {
            if (value == '#') {
                galaxies.add(Pair(x.toLong(), y.toLong()))
            }
        }
    }
    return galaxies
}

private fun expand(input: List<String>): List<String> {
    var emptyColumns = IntRange(0, input.size).asSequence().map { i -> i to true }.toMap()
    val newInput = mutableListOf<String>()
    for (line in input) {
        newInput.add(line)
        if (line.contains(".".repeat(line.length))) {
            newInput.add(line)
        }
        emptyColumns = line.asSequence().mapIndexed { index, c -> index to ((c == '.') && emptyColumns.getValue(index)) }.toMap()
    }
    return newInput.asSequence()
        .map { line -> insertCharsAtIndices(line, emptyColumns.filter { e -> e.value }.keys.toList()) }
        .toMutableList()
}

private fun insertCharsAtIndices(original: String, indices: List<Int>): String {
    val builder = StringBuilder(original)

    for (i in indices.indices) {
        val index = indices[i] + i
        builder.insert(index, '.')
    }

    return builder.toString()
}