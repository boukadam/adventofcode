package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

fun main() {

    fun part1(input: List<String>): Int {
        return parse(input).asSequence()
            .map { group -> compute(group)[0] }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return parse(input).asSequence()
            .map {group -> smudge(group) }
            .sum()
    }

    val input = readInput("v2023/d13/input")

    part1(input).println()
    part2(input).println()
}

private fun smudge(group: List<String>): Int {
    val count = compute(group)
    for (i in group.indices) {
        for (j in group[i].indices) {
            val newGroup = smudgeGroup(group, i, j)
            val newCount = compute(newGroup)
            val rest = newCount.minus(count.toSet())
            if (rest.isNotEmpty()) {
                return rest[0]
            }
        }
    }
    return count[0]
}

private fun smudgeGroup(group: List<String>, line: Int, column: Int): List<String> {
    val newGroup = mutableListOf<String>()
    for (i in group.indices) {
        if (i == line) {
            newGroup.add(smudgeLine(group[i], column))
        } else {
            newGroup.add(group[i])
        }
    }
    return newGroup
}

private fun smudgeLine(line: String, position: Int): String {
    return line.substring(0, position) + reverse(line[position]) + line.substring(position + 1)
}

private fun reverse(c: Char): Char {
    return when (c) {
        '#' -> '.'
        else -> '#'
    }
}

private fun compute(group: List<String>): List<Int> {
    val rowLine = findRowOfReflection(group)
    val result = mutableListOf<Int>()
    result.addAll(rowLine.map { r -> r * 100 }.toList())
    result.addAll(findColumnOfReflection(group))
    return result.toList()
}

private fun findRowOfReflection(group: List<String>): List<Int> {
    val validLines = mutableListOf<Int>()
    for (line in 1..<group.size) {
        var isLineReflection = true
        for ((i, j) in ((line - 1) downTo 0).zip(line..<group.size)) {
            if (group[i] != group[j]) {
                isLineReflection = false
                break
            }
        }
        if (isLineReflection) {
            validLines.add(line)
        }
    }
    return validLines
}

private fun findColumnOfReflection(group: List<String>): List<Int> {
    return findRowOfReflection(reverse(group))
}

private fun reverse(group: List<String>): List<String> {
    val newGroup = mutableListOf<String>()
    val newSize = group[0].length
    for (i in 0..<newSize) {
        val newLineBuilder = StringBuilder()
        for (j in group.indices) {
            newLineBuilder.append(group[j][i])
        }
        newGroup.add(newLineBuilder.toString())
    }
    return newGroup
}

private fun parse(input: List<String>): List<List<String>> {
    val groups = mutableListOf<List<String>>()
    val iterator = input.iterator()
    val tmp = mutableListOf<String>()
    while (iterator.hasNext()) {
        val line = iterator.next();
        if (line.isEmpty()) {
            groups.add(tmp.toList())
            tmp.clear()
        } else {
            tmp.add(line)
        }
    }
    if (tmp.isNotEmpty()) {
        groups.add(tmp.toList())
    }
    return groups
}