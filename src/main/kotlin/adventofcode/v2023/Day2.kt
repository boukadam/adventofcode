package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import kotlin.math.min

private enum class RGB { red, green, blue }
private data class Game(val id: Int, val sets: List<Map<RGB, Int>>)

fun main() {

    val part1Limits = mapOf(
        RGB.red to 12,
        RGB.green to 13,
        RGB.blue to 14
    )

    fun checkSetLimits(set: Map<RGB, Int>, limits: Map<RGB, Int>): Boolean {
        return set.getOrDefault(RGB.red, 0 ) <= limits.getOrDefault(RGB.red, 0)
                && set.getOrDefault(RGB.green, 0 ) <= limits.getOrDefault(RGB.green, 0)
                && set.getOrDefault(RGB.blue, 0 ) <= limits.getOrDefault(RGB.blue, 0)
    }

    fun checkGameLimits(game: Game): Boolean {
        return game.sets.asSequence()
            .map { set -> checkSetLimits(set, part1Limits) }
            .reduce { acc, v -> acc && v }
    }

    fun part1(input: List<Game>): Int {
        return input.asSequence()
            .filter { game -> checkGameLimits(game) }
            .map { game -> game.id }
            .sum()
    }

    fun updateMinimum(minimum: Map<RGB, Int>, next: Map<RGB, Int>): Map<RGB, Int> {
        return minimum.keys.plus(next.keys)
            .associateWith { color -> maxOf(minimum.getOrDefault(color, 0), next.getOrDefault(color, 0)) }
    }

    fun getMinimumSet(game: Game): Map<RGB, Int> {
        val iterator = game.sets.asSequence().iterator()
        var minimum = iterator.next()
        while (iterator.hasNext()) {
            val next = iterator.next();
            if (!checkSetLimits(next, minimum)) {
                minimum = updateMinimum(minimum, next)
            }
        }
        return minimum
    }

    fun part2(input: List<Game>): Int {
        return input.asSequence()
            .map { game -> getMinimumSet(game) }
            .map { minimum -> minimum.values.reduce { acc, i ->  acc * i } }
            .sum()
    }

    fun parseCubes(input: String): Map<RGB, Int> {
        val regexCubes = Regex(RGB.entries.joinToString("|").let { "(?:\\s)*(\\d+) ($it)" })
        return input.split(",")
            .asSequence()
            .map(regexCubes::find)
            .filterNotNull()
            .map { result -> result.destructured }
            .associate { tuple -> Pair(RGB.valueOf(tuple.component2()), tuple.component1().toInt()) }
    }

    fun parseSets(input: String): List<Map<RGB, Int>> {
        return input.split(";")
            .asSequence()
            .map { set -> parseCubes(set) }
            .toList();
    }

    fun parse(input: List<String>): List<Game> {
        val regexLine = Regex("^Game (\\d+): (.*)\$")
        return input.asSequence()
            .map(regexLine::find)
            .filterNotNull()
            .map { result -> result.destructured}
            .map { tuple -> Game(tuple.component1().toInt(), parseSets(tuple.component2())) }
            .toList();
    }

    val input = readInput("v2023/d2/input")
    val games = parse(input)
    println(games)

    part1(games).println()
    part2(games).println()
}