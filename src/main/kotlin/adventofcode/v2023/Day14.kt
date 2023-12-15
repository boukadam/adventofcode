package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

fun main() {

    fun part1(input: List<String>): Int {
        return count(compute(input))
    }

    fun part2(input: List<String>): Int {
        var result: List<String> = input
        var cycle = 0
        var loopLength = 0
        val seen = mutableMapOf<String, Int>()
        val total = 1_000_000_000
        while (cycle < total) {
            val current = result.joinToString("")
            if (current in seen) {
                loopLength = cycle - seen[current]!!
                break
            }
            seen[current] = cycle
            result = cycle(result)
            cycle++
        }

        val remaining = (total - (cycle - loopLength)) % loopLength

        repeat(remaining) {
            result = cycle(result)
        }

        return count(result)
    }

    val input = readInput("v2023/d14/input")

    part1(input).println()
    part2(input).println()
}

private fun cycle(platform: List<String>): List<String> {
    var result: List<String> = platform
    repeat(4) {
        result = reverse(compute(result))
    }
    return result
}

private fun reverse(platform: List<String>): List<String> {
    val newPlatform = mutableListOf<String>()
    val newSize = platform[0].length
    for (i in 0..<newSize) {
        val newLineBuilder = StringBuilder()
        for (j in (newSize - 1) downTo 0) {
            newLineBuilder.append(platform[j][i])
        }
        newPlatform.add(newLineBuilder.toString())
    }
    return newPlatform
}

private fun count(platform: List<String>): Int {
    return platform.asSequence()
        .mapIndexed { index, line -> line.count { it == 'O' } * (platform.size - index) }
        .sum()
}

private fun compute(platform: List<String>): List<String> {
    var result: List<String> = mutableListOf()
    repeat(platform.size - 1) {
        result = tilt(
            when (result.isEmpty()) {
                true -> platform
                else -> result
            }
        )
    }
    return result
}

private fun tilt(platform: List<String>): List<String> {

    val map = platform.mapIndexed() { index, s -> index to s }.toMap().toMutableMap()
    for (i in (platform.size - 1) downTo 1) {
        val top = map.getValue(i)
        val bottom = map.getValue(i - 1)
        val topBuilder = StringBuilder()
        val bottomBuilder = StringBuilder()
        for (j in top.indices) {
            if (bottom[j] == '.' && top[j] == 'O') {
                bottomBuilder.append('O')
                topBuilder.append('.')
            } else {
                bottomBuilder.append(bottom[j])
                topBuilder.append(top[j])
            }
        }
        map[i] = topBuilder.toString()
        map[i - 1] = bottomBuilder.toString()
    }
    return map.values.toList()
}

