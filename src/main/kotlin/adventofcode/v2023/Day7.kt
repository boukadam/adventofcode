package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import java.util.Comparator

private enum class Type(val rank: Int) {
    FIVE_OF_A_KIND(7),
    FOUR_OF_A_KIND(6),
    FULL_HOUSE(5),
    THREE_OF_A_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1)
}

private data class Hand(val value: String, val bid: Int, val type: Type)

fun cardValue(card: Char, part1: Boolean): Int {
    return when (card) {
        'A' -> 14
        'K' -> 13
        'Q' -> 12
        'J' -> when (part1) {
            true -> 11
            false -> 1
        }
        'T' -> 10
        else -> card.toString().toInt()
    }
}

private fun getType(hand: String): Type {
    val groupBy = hand.asSequence()
        .groupBy { c -> c }
        .values.sortedByDescending { c -> c.size }
        .toList()
    when (groupBy.size) {
        1 -> return Type.FIVE_OF_A_KIND
        2 -> when (groupBy[0].size) {
            4 -> return Type.FOUR_OF_A_KIND
            3 -> return Type.FULL_HOUSE
        }

        3 -> when (groupBy[0].size) {
            3 -> return Type.THREE_OF_A_KIND
            2 -> when (groupBy[1].size) {
                2 -> return Type.TWO_PAIR
            }
        }

        4 -> return Type.ONE_PAIR
        5 -> return Type.HIGH_CARD
    }
    return Type.FIVE_OF_A_KIND
}

private fun getTypePart2(hand: String): Type {
    val groupBy = hand.asSequence().groupBy { c -> c }
    val jokers = groupBy.getOrDefault('J', listOf())
    if (jokers.isEmpty()) {
        return getType(hand)
    }
    if (jokers.size == 5) {
        return Type.FIVE_OF_A_KIND
    }
    val maxCard = groupBy.asSequence()
        .filter { entry -> entry.key != 'J' }
        .maxBy { entry -> entry.value.size }
        .key

    val newHand = hand.replace('J', maxCard)
    return getType(newHand)
}

private fun compareTo(hand1: Hand, hand2: Hand, part1: Boolean): Int {
    if (hand1.type != hand2.type) {
        return hand1.type.rank - hand2.type.rank
    }
    hand1.value.forEachIndexed { i, c ->
        val v1 = cardValue(c, part1)
        val v2 = cardValue(hand2.value[i], part1)
        if (v1 != v2) {
            return v1 - v2
        }
    }
    return 0
}

fun main() {

    fun part1(input: List<Hand>): Int {
        return input.asSequence()
            .sortedWith(Comparator.comparing(
                { it },
                { h1, h2 -> compareTo(h1, h2, true) }
            ))
            .mapIndexed { index, hand -> (index + 1) * hand.bid }
            .sum()
    }

    fun part2(input: List<Hand>): Int {
        return input.asSequence()
            .sortedWith(Comparator.comparing(
                { it },
                { h1, h2 -> compareTo(h1, h2, false) }
            ))
            .mapIndexed { index, hand -> (index + 1) * hand.bid }
            .sum()
    }

    val input = readInput("v2023/d7/input")

    part1(parse(input)).println()
    part2(parsePart2(input)).println()
}

private fun parse(input: List<String>): List<Hand> {
    return input.asSequence()
        .map { line -> line.split(" ") }
        .map { elements -> Hand(elements[0], elements[1].toInt(), getType(elements[0])) }
        .toList()
}

private fun parsePart2(input: List<String>): List<Hand> {
    return input.asSequence()
        .map { line -> line.split(" ") }
        .map { elements -> Hand(elements[0], elements[1].toInt(), getTypePart2(elements[0])) }
        .toList()
}