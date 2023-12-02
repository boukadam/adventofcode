package adventofcode.v2023

import adventofcode.println
import adventofcode.readInput

fun main() {

    fun String.getFirstAndLast() = first() to last()

    fun part1(input: List<String>): Int {
        return input.asSequence()
                .map { s -> s.filter { it.isDigit() } }
                .map { s -> s.getFirstAndLast() }
                .map { (f, l) -> "$f$l".toInt() }
                .sum();
    }

    fun replaceWordsWithNumbers(input: String): String {
        val wordMap = mapOf(
                "one" to "1",
                "two" to "2",
                "three" to "3",
                "four" to "4",
                "five" to "5",
                "six" to "6",
                "seven" to "7",
                "eight" to "8",
                "nine" to "9"
        )

        val result = StringBuilder()
        val currentWord = StringBuilder()
        val regex = Regex(wordMap.keys.joinToString("|").let { "($it)" })

        for (char in input) {
            if (char.isLetter()) {
                currentWord.append(char)
                val matches = regex.find(currentWord.toString())
                if (matches != null) {
                    result.append(wordMap[matches.value])
                    break
                }
            } else {
                result.append(char)
                currentWord.clear()
                break
            }
        }

        for (char in input.reversed()) {
            if (char.isLetter()) {
                currentWord.append(char)
                val matches = regex.find(currentWord.toString().reversed())
                if (matches != null) {
                    result.append(wordMap[matches.value])
                    break
                }
            } else {
                result.append(char)
                currentWord.clear()
                break
            }
        }

        return result.toString()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
                .map { s -> replaceWordsWithNumbers(s) }
                .map { s -> s.filter { it.isDigit() } }
                .map { s -> s.getFirstAndLast() }
                .map { (f, l) -> "$f$l".toInt() }
                .sum();
    }

    val input = readInput("v2023/d1/input")

    part1(input).println()
    part2(input).println()
}