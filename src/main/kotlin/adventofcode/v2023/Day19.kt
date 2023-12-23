package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

private data class Rule(
    val condition: (r: Map<String, Int>) -> Boolean,
    val variable: String?,
    val operation: String?,
    val number: Int?,
    val next: String
)

fun main() {

    fun part1(workflows: Map<String, List<Rule>>, ratings: List<Map<String, Int>>): Int {
        return ratings.asSequence()
            .filter { rating -> run(rating, workflows) }
            .flatMap { e -> e.values.asSequence() }
            .sum()
    }

    fun part2(workflows: Map<String, List<Rule>>): Long {
        return count(listOf("x", "m", "a", "s").associateWith { _ -> 1..4000 }.toMutableMap(), workflows, "in")
    }

    val input = readInput("v2023/d19/input")
    val (workflows, ratings) = parse(input)

    part1(workflows, ratings).println()
    part2(workflows).println()
}

private fun count(ranges: MutableMap<String, IntRange>, workflows: Map<String, List<Rule>>, name: String): Long {
    if (name == "R") {
        return 0
    }
    if (name == "A") {
        return ranges.values.fold(1L) { acc, intRange -> acc * intRange.count() }
    }

    var total = 0L
    for (rule in workflows.getValue(name)) {
        if (rule.variable != null) {
            val global = ranges.getValue(rule.variable)
            val t: IntRange
            val f: IntRange

            if (rule.operation == "<") {
                t = global.first..minOf(rule.number!! - 1, global.last())
                f = maxOf(rule.number, global.first())..global.last()
            } else {
                t = maxOf(rule.number!! + 1, global.first())..global.last()
                f = global.first()..minOf(rule.number, global.last())
            }
            if (t.first() <= t.last()) {
                val copy = HashMap(ranges)
                copy[rule.variable] = t
                total += count(copy, workflows, rule.next)
            }
            if (f.first() <= f.last()) {
                ranges[rule.variable] = f
            } else {
                break
            }
        } else {
            total += count(ranges, workflows, rule.next)
        }
    }
    return total
}

private fun run(rating: Map<String, Int>, workflows: Map<String, List<Rule>>): Boolean {
    val workflow = workflows.getValue("in")
    var next = next(rating, workflow)
    while (next != "A" && next != "R") {
        next = next(rating, workflows.getValue(next))
    }
    return when (next) {
        "A" -> true
        else -> false
    }
}

private fun next(rating: Map<String, Int>, workflow: List<Rule>): String {
    for (rule in workflow) {
        if (rule.condition(rating)) {
            return rule.next
        }
    }
    error("")
}

private fun parse(input: List<String>): Pair<Map<String, List<Rule>>, List<Map<String, Int>>> {
    val workflows = mutableMapOf<String, List<Rule>>()
    val iterator = input.iterator()
    var next = iterator.next()
    while (next.isNotBlank()) {
        val workflow = parseWorkflow(next)
        workflows[workflow.first] = workflow.second
        next = iterator.next()
    }

    val ratings = mutableListOf<Map<String, Int>>()
    while (iterator.hasNext()) {
        ratings.add(parseRating(iterator.next()))
    }

    return Pair(workflows, ratings)
}

private fun parseConditionAndRange(variable: String, operation: String, value: Int): (Map<String, Int>) -> Boolean {
    return when (operation) {
        ">" -> {
            return { rating: Map<String, Int> -> rating.getValue(variable) > value }
        }

        "<" -> {
            return { rating: Map<String, Int> -> rating.getValue(variable) < value }
        }

        else -> error("")
    }
}

private fun parseWorkflow(input: String): Pair<String, List<Rule>> {
    val regex = Regex("^(.)([<>])(\\d+)\$")
    val id = input.substring(0, input.indexOf('{'))
    val rules = input.substring(input.indexOf('{') + 1, input.length - 1)
        .split(',')
        .asSequence()
        .map { r ->
            if (r.contains(':')) {
                val split = r.split(':')
                val matcher = regex.find(split[0])!!.destructured
                val value = matcher.component3().toInt()
                val condition = parseConditionAndRange(matcher.component1(), matcher.component2(), value)
                Rule(condition, matcher.component1(), matcher.component2(), value, split[1])
            } else {
                Rule({ _ -> true }, null, null, null, r)
            }
        }
        .toList()
    return Pair(id, rules)
}

private fun parseRating(input: String): Map<String, Int> {
    return input.substring(1, input.length - 1)
        .split(',')
        .asSequence()
        .map { e ->
            val split = e.split('=')
            split[0] to split[1].toInt()
        }
        .toList()
        .toMap()
}