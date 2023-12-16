package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import java.math.BigInteger

private enum class NodeType {
    START, MIDDLE, END
}

private data class Node(val id: String, val left: String, val right: String, val type: NodeType)

fun main() {

    fun part1(input: List<String>): Int {
        val instructions = input[0]
        val map = parse(input)
        return compute("AAA", instructions, map)
    }

    fun part2(input: List<String>): BigInteger {
        val instructions = input[0]
        val map = parse(input)
        return map.filter { entry -> entry.value.type == NodeType.START }
            .keys
            .map { node -> compute(node, instructions, map).toBigInteger() }
            .reduce { acc, v -> acc * v / acc.gcd(v) }

    }

    val input = readInput("v2023/d8/input")

    part1(input).println()
    part2(input).println()
}

private fun compute(start: String, instructions: String, map: Map<String, Node>): Int {
    var currentNode = map[start]
    var index = 0
    while (true) {
        currentNode = if (instructions[index % instructions.length] == 'L') {
            map[currentNode?.left]
        } else {
            map[currentNode?.right]
        }
        index++
        if ((start == "AAA" && currentNode?.id.equals("ZZZ")) || currentNode?.type == NodeType.END) {
            break
        }
    }
    return index
}

private fun parse(input: List<String>): Map<String, Node> {
    val regexLine = Regex("^([A-Z1-9]{3}) = \\(([A-Z1-9]{3}), ([A-Z1-9]{3})\\)\$")
    return input.asSequence()
        .drop(2)
        .map(regexLine::find)
        .filterNotNull()
        .map { result -> result.destructured }
        .map { tuple ->
            tuple.component1() to Node(
                tuple.component1(), tuple.component2(), tuple.component3(), when {
                    tuple.component1().endsWith('A') -> NodeType.START
                    tuple.component1().endsWith('Z') -> NodeType.END
                    else -> NodeType.MIDDLE
                }
            )
        }
        .toList()
        .toMap()
}