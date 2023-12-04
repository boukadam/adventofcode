package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import java.util.*
import kotlin.collections.HashMap

data class Card(val id: Int, val winnings: Set<Int>, val possessions: Set<Int>)

fun main() {

    fun suite(n: Int): Int {
        if (n == 1) {
            return 1
        }
        return suite(n - 1) * 2
    }

    fun part1(input: List<Card>): Int {
        return input.asSequence()
            .map { card -> card.possessions.intersect(card.winnings) }
            .filter { set -> set.isNotEmpty() }
            .map { winningPossessions -> suite(winningPossessions.size) }
            .sum()
    }

    fun part2(input: List<Card>): Int {
        val map = HashMap<Int, Int>()
        input.forEach { card -> map.merge(card.id, card.possessions.intersect(card.winnings).size, Int::plus) }
        val queue = LinkedList<Int>()
        queue.addAll(map.keys)
        val result = HashMap<Int, Int>()

        val queueIterator = queue.listIterator(queue.size)
        while (queueIterator.hasPrevious()) {
            val e = queueIterator.previous()
            repeat(map.getValue(e)) { i -> queueIterator.add(e + i + 1) }
            result.merge(e, 1, Int::plus)
        }

        return result.values.sum()
    }

    val input = readInput("v2023/d4/input")
    val cards = parseCards(input)

    part1(cards).println()
    part2(cards).println()
}

fun parseCards(input: List<String>): List<Card> {
    val regexLine = Regex("^Card\\s+(\\d+): (.*) \\| (.*)\$")
    return input.asSequence()
        .map(regexLine::find)
        .filterNotNull()
        .map { result -> result.destructured }
        .map { tuple ->
            val winnings = tuple.component2().split(" ")
                .map { x -> x.trim() }
                .filter { x -> x != "" }
                .map { s -> s.toInt() }
                .toSet()
            val possessions = tuple.component3().split(" ")
                .map { x -> x.trim() }
                .filter { x -> x != "" }
                .map { s -> s.toInt() }
                .toSet()
            Card(tuple.component1().toInt(), winnings, possessions)
        }
        .toList()
}