package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

private data class Race(val time: Long, val distance: Long)

fun main() {

    fun part1(input: List<String>): Int {
        val races = parseRaces(input)
        return races.asSequence()
            .map(::simulate)
            .map { list -> list.size }
            .reduce { acc, i ->  acc * i }
    }

    fun part2(input: List<String>): Int {
        val times = parseLine(input[0])
        val distances = parseLine(input[1])

        val timeStr = "Time: " + times.joinToString("")
        val distanceStr = "Distance: " + distances.joinToString("")
        val races = parseRaces(listOf(timeStr, distanceStr))
        return races.asSequence()
            .map(::simulate)
            .map { list -> list.size }
            .reduce { acc, i ->  acc * i }
    }

    val input = readInput("v2023/d6/input")

    part1(input).println()
    part2(input).println()
}

private fun simulate(race: Race): List<Long> {
    return LongRange(1, race.time)
        .asSequence()
        .map { holdingTime -> holdingTime * (race.time - holdingTime) }
        .filter { distance -> distance > race.distance }
        .toList()
}

private fun parseRaces(input: List<String>): List<Race> {
    val times = parseLine(input[0])
    val distances = parseLine(input[1])

    return times.indices.asSequence()
        .map { i -> Race(times[i], distances[i]) }
        .toList()
}

private fun parseLine(input: String): List<Long> {
    return input.split(" ")
        .asSequence()
        .map { s -> s.filter { it.isDigit() } }
        .filter { s -> s.isNotBlank() }
        .map { s -> s.toLong() }
        .toList()
}