package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import kotlin.streams.asStream

fun main() {

    fun part1(input: List<String>): Int {
        return input.asSequence()
            .map { line -> getTree(line) }
            .map { tree -> getNextValue(tree) }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .map { line -> getTree(line) }
            .map { tree -> getPreviousValue(tree) }
            .sum()
    }

    val input = readInput("v2023/d9/input")

    part1(input).println()
    part2(input).println()
}

fun getPreviousValue(tree: MutableList<MutableList<Int>>): Int {
    tree[0].add(0)
    for (i in (tree.size - 2) downTo 0) {
        val previousList = tree[i + 1]
        val current = tree[i]
        current.add(current[0] - previousList[previousList.size - 1])
    }
    return tree[0][tree[0].size - 1]
}

fun getNextValue(tree: MutableList<MutableList<Int>>): Int {
    tree[tree.size - 1].add(0)
    for (i in (tree.size - 2) downTo 0) {
        val previousList = tree[i + 1]
        val current = tree[i]
        current.add(current[current.size - 1] + previousList[previousList.size - 1])
    }
    return tree[0][tree[0].size - 1]
}

fun getTree(line: String): MutableList<MutableList<Int>> {
    val first = line.split(" ").map { e -> e.toInt() }.toMutableList()
    val tree = mutableListOf(first)
    var current = first
    do {
        current = getDifferences(current)
        tree.add(current)
    } while (current.asSequence().asStream().anyMatch() { e -> e != 0 })
    return tree
}

fun getDifferences(list: List<Int>): MutableList<Int> {
    val differences = mutableListOf<Int>()
    for (i in 0 until list.size - 1 step 1) {
        val element1 = list[i]
        val element2 = list[i + 1]
        differences.add(element2 - element1)
    }
    return differences
}