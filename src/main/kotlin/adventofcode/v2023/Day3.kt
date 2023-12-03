package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput
import java.lang.StringBuilder

data class Part(val number: Int, val x: Int, val y: Int, val symbol: Symbol?)
data class Symbol(val id: String, val symbol: Char, val x: Int, val y: Int)

fun main() {

    fun part1(parts: List<Part>): Int {
        return parts.asSequence()
            .filter { part -> part.symbol != null }
            .sumOf { part -> part.number }
    }

    fun part2(input: List<Part>): Int {
        return input.asSequence()
            .filter { part -> part.symbol != null }
            .filter { part -> part.symbol?.symbol == '*' }
            .groupBy { part -> part.symbol }
            .values
            .asSequence()
            .filter { list -> list.size > 1 }
            .map { list -> list.map { part -> part.number }.reduce { acc, i ->  acc * i } }
            .sum()
    }

    fun parseSymbols(input: List<String>): List<Symbol> {
        val symbols = ArrayList<Symbol>()
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (!c.isDigit() && c != '.') {
                    symbols.add(Symbol("$x-$y", c, x, y))
                }
            }
        }
        return symbols
    }

    fun parseParts(input: List<String>, symbols: List<Symbol>): List<Part> {
        val parts = ArrayList<Part>()
        input.forEachIndexed { y, line ->
            val currentPart = StringBuilder()
            var start = 0
            line.forEachIndexed { x, c ->
                if (c.isDigit()) {
                    if (currentPart.isEmpty()) {
                        start = x
                    }
                    currentPart.append(c)
                } else if (currentPart.isNotEmpty()) {
                    val adjacentSymbols = symbols.asSequence()
                        .filter { symbol -> symbol.x >= start - 1 && symbol.x <= x }
                        .filter { symbol -> symbol.y >= y - 1 && symbol.y <= y + 1 }
                        .toList()
                    parts.add(Part(currentPart.toString().toInt(), start, y, adjacentSymbols.firstOrNull()))
                    currentPart.clear()
                }
            }
            if (currentPart.isNotEmpty()) {
                val adjacentSymbols = symbols.asSequence()
                    .filter { symbol -> symbol.x >= start - 1 && symbol.x <= line.length }
                    .filter { symbol -> symbol.y >= y - 1 && symbol.y <= y + 1 }
                    .toList()
                parts.add(Part(currentPart.toString().toInt(), start, y, adjacentSymbols.firstOrNull()))
                currentPart.clear()
            }
        }
        return parts
    }

    val input = readInput("v2023/d3/input")

    fun print(parts: List<Part>, symbols: List<Symbol>) {
        val red = "\u001b[31m"
        val reset = "\u001b[0m"
        for (y in input.indices) {
            var continueStep = 0
            for (x in input.indices) {
                if (continueStep > 0) {
                    continueStep--
                    continue
                }
                val symbolOrNull = symbols.asSequence()
                    .filter { symbol -> symbol.x == x && symbol.y == y }
                    .firstOrNull()
                if (symbolOrNull == null) {
                    val partOrNull = parts.asSequence()
                        .filter { part -> part.x == x && part.y == y }
                        .firstOrNull()
                    if (partOrNull == null) {
                        print('.')
                    } else {
                        if (partOrNull.symbol != null) {
                            print(red + partOrNull.number + reset)
                        } else {
                            print(partOrNull.number)
                        }
                        continueStep = partOrNull.number.toString().count { it.isDigit() } - 1
                    }
                } else {
                    print(symbolOrNull.symbol)
                }
            }
            println("")
        }
    }

    val symbols = parseSymbols(input)
    val parts = parseParts(input, symbols)

    print(parts, symbols)
    part1(parts).println()
    part2(parts).println()
    
}