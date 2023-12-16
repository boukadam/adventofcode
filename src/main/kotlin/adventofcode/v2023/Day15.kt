package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import org.apache.commons.collections4.map.LinkedMap

private data class Step(val label: String, val hash: Int, val operation: Char, val length: Int?)

fun main() {

    fun part1(input: List<String>): Int {
        return input.asSequence()
            .map(::hash)
            .sum()
    }

    fun part2(input: List<String>): Int {
        val boxes = mutableMapOf<Int, LinkedHashMap<String, Int>>()
        for (step in parseSteps((input))) {
            when (step.operation) {
                '=' -> {
                    if (!boxes.containsKey(step.hash)) {
                        boxes[step.hash] = linkedMapOf()
                    }
                    boxes[step.hash]?.put(step.label, step.length!!)
                }
                '-' -> {
                    boxes[step.hash]?.remove(step.label)
                }
            }
        }
        return boxes.asSequence()
            .flatMap { e -> e.value.asSequence()
                .mapIndexed { i, l -> (e.key + 1) * (i + 1) * l.value }
            }
            .sum()
    }

    val input = parse(readInput("v2023/d15/input"))

    part1(input).println()
    part2(input).println()
}

private fun hash(input: String): Int {
    return input.asSequence()
        .map { c -> c.code }
        .reduce { acc, i -> ((acc + i) * 17) % 256 }
}

private fun parse(input: List<String>): List<String> {
    return input[0].split(',').map { s -> Char(0) + s };
}

private fun parseSteps(input: List<String>): List<Step> {
    return input.asSequence()
        .map { s ->
            if (s.contains('-')) {
                val label = s.dropLast(1)
                Step(label, hash(label), '-', null)
            } else {
                val label = s.substring(0, s.indexOf('='))
                Step(label, hash(label), '=', s.substring(s.indexOf('=') + 1).toInt())
            }
        }
        .toList()
}